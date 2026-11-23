package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.app.Main;
import br.pucpr.ib2b.biblioteca.Movimentacao;
import br.pucpr.ib2b.biblioteca.SessaoDeDados;
import br.pucpr.ib2b.circulacao.ServicoDeCirculacao;
import br.pucpr.ib2b.circulacao.iCirculacao;
import br.pucpr.ib2b.dominio.Devolucao;
import br.pucpr.ib2b.dominio.Emprestimo;
import br.pucpr.ib2b.dominio.Reserva;
import br.pucpr.ib2b.dominio.Situacao;
import br.pucpr.ib2b.persistencia.ErroDePersistencia;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;
import br.pucpr.ib2b.persistencia.RepositorioLivro;
import br.pucpr.ib2b.persistencia.RepositorioMovimentacao;
import br.pucpr.ib2b.persistencia.RepositorioRelacionamento;
import br.pucpr.ib2b.persistencia.UnidadeDeTrabalho;
import br.pucpr.ib2b.tratadores.Resultado;
import br.pucpr.ib2b.tratadores.TratadorDeEmprestimo;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 4 - Entrada unica para as operacoes de circulacao - Facade. */
class R04_FachadaDeCirculacaoFacadeTest extends CenarioBase {

    @Test
    void R04_efetivacaoValidaReservaRemoveReservaCriaEmprestimoEAtualizaLivro() {
        assertTrue(sistema.emprestimos().reservarLivro("L1", ANA).sucesso());

        Resultado r = sistema.emprestimos().emprestarLivro("L1", ANA);

        assertTrue(r.sucesso(), r.mensagem());
        assertTrue(sistema.emprestimos().consultarReservas(ANA).isEmpty(), "reserva ativa removida");
        assertEquals(Situacao.EMPRESTADO, situacao("L1").situacao(), "Livro atualizado");
        assertEquals(relogio.hoje().plusDays(7), situacao("L1").vencimento());
        SessaoDeDados s = SessaoDeDados.abrir(sistema.fabrica());
        assertTrue(s.movimentacao().buscarEmprestimoAtivo("L1").isPresent(), "emprestimo criado");
        s.desfazer();
    }

    @Test
    void R04_interfaceRecebeComprovantesENaoColecoesInternas() {
        for (Method m : iCirculacao.class.getMethods()) {
            String pacote = m.getReturnType().getPackageName();
            assertTrue(pacote.endsWith("circulacao") || m.getReturnType() == List.class,
                    m.getName() + " devolve " + m.getReturnType());
        }
        for (Class<?> c : List.of(TratadorDeEmprestimo.class, Main.class)) {
            for (Field f : c.getDeclaredFields()) {
                assertNotEquals(Movimentacao.class, f.getType(), c.getSimpleName() + " nao acessa Movimentacao");
            }
        }
    }

    @Test
    void R04_reservaDeOutroEstudanteNaoAlteraNada() {
        sistema.emprestimos().reservarLivro("L1", ANA);

        assertFalse(sistema.emprestimos().emprestarLivro("L1", BIA).sucesso());

        assertEquals(Situacao.RESERVADO, situacao("L1").situacao());
        assertEquals(1, sistema.emprestimos().consultarReservas(ANA).size());
    }

    @Test
    void R04_falhaNoMeioDaOperacaoDesfazOsPassosAnteriores() {
        sistema.emprestimos().reservarLivro("L1", ANA);
        iCirculacao comFalha = new ServicoDeCirculacao(new FabricaQueFalhaAoRegistrarEmprestimo(sistema.fabrica()),
                relogio);

        // livro.emprestar e removerReserva ja rodaram quando registrarEmprestimo falha
        assertThrows(ErroDePersistencia.class, () -> comFalha.efetivarEmprestimo("L1", ANA));

        assertEquals(Situacao.RESERVADO, situacao("L1").situacao(), "Livro voltou ao que era");
        assertEquals(1, sistema.emprestimos().consultarReservas(ANA).size(), "reserva restaurada");
    }

    /** Fabrica de teste: igual a original, mas registrarEmprestimo falha (simula queda do banco). */
    private record FabricaQueFalhaAoRegistrarEmprestimo(FabricaDePersistencia original) implements FabricaDePersistencia {
        public UnidadeDeTrabalho criarUnidadeDeTrabalho() { return original.criarUnidadeDeTrabalho(); }

        public RepositorioLivro criarRepositorioLivro(UnidadeDeTrabalho u) { return original.criarRepositorioLivro(u); }

        public RepositorioRelacionamento criarRepositorioRelacionamento(UnidadeDeTrabalho u) {
            return original.criarRepositorioRelacionamento(u);
        }

        public String familia() { return original.familia(); }

        public RepositorioMovimentacao criarRepositorioMovimentacao(UnidadeDeTrabalho u) {
            RepositorioMovimentacao m = original.criarRepositorioMovimentacao(u);
            return new RepositorioMovimentacao() {
                public void registrarReserva(Reserva r) { m.registrarReserva(r); }
                public Optional<Reserva> buscarReservaAtiva(String id) { return m.buscarReservaAtiva(id); }
                public List<Reserva> listarReservasDe(String cpf) { return m.listarReservasDe(cpf); }
                public void removerReserva(String id) { m.removerReserva(id); }
                public void registrarEmprestimo(Emprestimo e) {
                    throw new ErroDePersistencia("falha simulada ao gravar o emprestimo", null);
                }
                public Optional<Emprestimo> buscarEmprestimoAtivo(String id) { return m.buscarEmprestimoAtivo(id); }
                public void removerEmprestimoAtivo(String id) { m.removerEmprestimoAtivo(id); }
                public void registrarDevolucao(Devolucao d) { m.registrarDevolucao(d); }
                public List<Devolucao> listarDevolucoes() { return m.listarDevolucoes(); }
            };
        }
    }
}
