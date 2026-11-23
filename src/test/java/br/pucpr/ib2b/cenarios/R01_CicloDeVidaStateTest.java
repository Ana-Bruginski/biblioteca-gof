package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.circulacao.Laudo;
import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.dominio.OperacaoInvalidaException;
import br.pucpr.ib2b.dominio.Situacao;
import br.pucpr.ib2b.tratadores.Resultado;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 1 - Ciclo de vida do livro - State. */
class R01_CicloDeVidaStateTest extends CenarioBase {

    @Test
    void R01_terceiraRenovacaoRecusadaSemAlterarVencimento() {
        emprestar("L1", ANA);
        assertTrue(sistema.emprestimos().renovarEmprestimo("L1").sucesso());
        assertTrue(sistema.emprestimos().renovarEmprestimo("L1").sucesso());
        LocalDate vencimentoAntes = situacao("L1").vencimento();

        Resultado terceira = sistema.emprestimos().renovarEmprestimo("L1");

        assertFalse(terceira.sucesso());
        assertTrue(terceira.mensagem().contains("limite de 2 renovacoes"));
        assertEquals(vencimentoAntes, situacao("L1").vencimento(), "o vencimento nao pode mudar");
        assertEquals(2, situacao("L1").numRenovacoes());
        assertEquals(Situacao.RENOVADO, situacao("L1").situacao());
    }

    @Test
    void R01_livroDevolvidoAindaNaoInspecionadoNaoPodeSerReservado() {
        emprestar("L1", ANA);
        assertTrue(sistema.devolucoes().registrarDevolucao("L1").sucesso());
        assertEquals(Situacao.DEVOLVIDO, situacao("L1").situacao());

        Resultado reserva = sistema.emprestimos().reservarLivro("L1", BIA);

        assertFalse(reserva.sucesso());
        assertTrue(reserva.mensagem().contains("DEVOLVIDO"));
        assertEquals(Situacao.DEVOLVIDO, situacao("L1").situacao());
        assertTrue(sistema.emprestimos().consultarReservas(BIA).isEmpty());
    }

    @Test
    void R01_eventoNaoAceitoPeloEstadoERecusadoSemAlterarOLivro() {
        Livro livro = new Livro("X1", "Livro novo");
        assertThrows(OperacaoInvalidaException.class, livro::renovar);
        assertThrows(OperacaoInvalidaException.class, livro::devolver);
        assertEquals(Situacao.LIVRE, livro.getSituacao());
        assertNull(livro.getVencimento());
        assertEquals(0, livro.getNumRenovacoes());
    }

    @Test
    void R01_laudoIrrecuperavelLevaDeReparandoAIncinerado() {
        emprestar("L1", ANA);
        sistema.devolucoes().registrarDevolucao("L1");
        assertTrue(sistema.devolucoes().registrarInspecao("L1", Laudo.DANIFICADO).sucesso());
        assertEquals(Situacao.REPARANDO, situacao("L1").situacao());

        assertTrue(sistema.devolucoes().registrarLaudoIrrecuperavel("L1").sucesso());

        assertEquals(Situacao.INCINERADO, situacao("L1").situacao());
        assertFalse(sistema.emprestimos().reservarLivro("L1", BIA).sucesso(), "INCINERADO e estado final");
    }

    @Test
    void R01_cicloCompletoVoltaALivreELimpaOsDados() {
        emprestar("L1", ANA);
        sistema.emprestimos().renovarEmprestimo("L1");
        sistema.devolucoes().registrarDevolucao("L1");
        sistema.devolucoes().registrarInspecao("L1", Laudo.BOM_ESTADO);

        assertEquals(Situacao.LIVRE, situacao("L1").situacao());
        assertNull(situacao("L1").vencimento());
        assertEquals(0, situacao("L1").numRenovacoes());
        assertTrue(sistema.emprestimos().reservarLivro("L1", BIA).sucesso(), "novo ciclo pode comecar");
    }

    @Test
    void R01_estadoEGravadoComoSituacaoEReconstituidoNaLeitura() {
        for (Situacao s : Situacao.values()) {
            Livro reconstituido = Livro.reconstituir("X", "T", s, null, 0, null);
            assertEquals(s, reconstituido.getSituacao());
        }
        sistema.emprestimos().reservarLivro("L2", ANA);
        assertEquals(Situacao.RESERVADO, situacao("L2").situacao(), "lido de nova unidade de trabalho");
    }
}
