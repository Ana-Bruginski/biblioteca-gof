package br.pucpr.ib2b.circulacao;

import br.pucpr.ib2b.biblioteca.SessaoDeDados;
import br.pucpr.ib2b.dominio.Devolucao;
import br.pucpr.ib2b.dominio.Emprestimo;
import br.pucpr.ib2b.dominio.Estudante;
import br.pucpr.ib2b.dominio.ExcecaoDeDominio;
import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.dominio.Pessoa;
import br.pucpr.ib2b.dominio.Reserva;
import br.pucpr.ib2b.dominio.ReservaInvalidaException;
import br.pucpr.ib2b.infra.Ids;
import br.pucpr.ib2b.infra.Relogio;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

/**
 * Padrao Facade - papel Facade.
 * Coordena Relacionamento, Movimentacao, Acervo e Livro dentro de UMA unidade de trabalho:
 * se qualquer passo falhar, desfazer() descarta tudo que ja tinha sido feito naquela operacao.
 * As regras de estado continuam no Livro (State); a fachada so orquestra.
 */
public class ServicoDeCirculacao implements iCirculacao {
    private final FabricaDePersistencia fabrica;
    private final Relogio relogio;

    public ServicoDeCirculacao(FabricaDePersistencia fabrica, Relogio relogio) {
        this.fabrica = fabrica;
        this.relogio = relogio;
    }

    /** Regra 5: livro cadastrado + estudante cadastrado + livro LIVRE (o estado decide). */
    @Override
    public ComprovanteReserva reservar(String idLivro, String cpf) {
        return emTransacao(s -> {
            Estudante e = s.relacionamento().buscarEstudante(cpf);
            if (e == null) {
                throw new ReservaInvalidaException("estudante com CPF " + cpf + " nao cadastrado");
            }
            Livro livro = s.acervo().buscarLivro(idLivro);
            livro.reservar(e);                                   // LIVRE -> RESERVADO (ou recusa)
            Reserva r = new Reserva(Ids.novo("RES"), idLivro, cpf, relogio.agora());
            s.movimentacao().registrarReserva(r);
            s.acervo().atualizar(livro);
            return new ComprovanteReserva(r.id(), idLivro, cpf, r.reservadaEm());
        });
    }

    /** Regra 6: consulta as reservas registradas para o estudante. */
    @Override
    public List<ComprovanteReserva> consultarReservas(String cpf) {
        return emTransacao(s -> s.movimentacao().listarReservasDe(cpf).stream()
                .map(r -> new ComprovanteReserva(r.id(), r.idLivro(), r.cpfEstudante(), r.reservadaEm()))
                .toList());
    }

    /**
     * Regra 7: valida a reserva, empresta (RESERVADO -> EMPRESTADO), remove a reserva ativa,
     * registra o emprestimo e atualiza o Livro. Tudo na mesma transacao.
     */
    @Override
    public ComprovanteEmprestimo efetivarEmprestimo(String idLivro, String cpf) {
        return emTransacao(s -> {
            Estudante e = s.relacionamento().buscarEstudante(cpf);
            Reserva r = s.movimentacao().buscarReservaAtiva(idLivro).orElse(null);
            if (e == null || r == null || !r.cpfEstudante().equals(cpf)) {
                throw new ReservaInvalidaException("nao ha reserva do livro " + idLivro + " para o CPF " + cpf);
            }
            Livro livro = s.acervo().buscarLivro(idLivro);
            LocalDate hoje = relogio.hoje();
            livro.emprestar(e, hoje);
            s.movimentacao().removerReserva(r);
            Emprestimo emp = new Emprestimo(Ids.novo("EMP"), idLivro, cpf, hoje, livro.getVencimento());
            s.movimentacao().registrarEmprestimo(emp);
            s.acervo().atualizar(livro);
            return new ComprovanteEmprestimo(emp.id(), idLivro, cpf, hoje, emp.vencimento());
        });
    }

    /** Regra 8: o empréstimo precisa existir; a guarda numRenovacoes < 2 fica no estado. */
    @Override
    public ComprovanteRenovacao renovar(String idLivro) {
        return emTransacao(s -> {
            Emprestimo emp = s.movimentacao().buscarEmprestimoAtivo(idLivro)
                    .orElseThrow(() -> new ExcecaoDeDominio("nao ha emprestimo ativo do livro " + idLivro));
            Livro livro = s.acervo().buscarLivro(idLivro);
            livro.renovar();                                     // pode lancar RenovacaoNegadaException
            s.movimentacao().registrarEmprestimo(emp.comVencimento(livro.getVencimento()));
            s.acervo().atualizar(livro);
            return new ComprovanteRenovacao(idLivro, livro.getVencimento(), livro.getNumRenovacoes());
        });
    }

    @Override
    public SituacaoDTO consultar(String idLivro) {
        return emTransacao(s -> paraDTO(s.acervo().buscarLivro(idLivro)));
    }

    /** Regra 9: remove o emprestimo ativo, registra a Devolucao e leva o livro a DEVOLVIDO. */
    @Override
    public ComprovanteDevolucao devolver(String idLivro) {
        return emTransacao(s -> {
            Emprestimo emp = s.movimentacao().buscarEmprestimoAtivo(idLivro)
                    .orElseThrow(() -> new ExcecaoDeDominio("nao ha emprestimo ativo do livro " + idLivro));
            Livro livro = s.acervo().buscarLivro(idLivro);
            LocalDate vencimento = livro.getVencimento();
            livro.devolver();                                    // EMPRESTADO/RENOVADO -> DEVOLVIDO
            s.movimentacao().removerEmprestimoAtivo(idLivro);
            Devolucao d = new Devolucao(Ids.novo("DEV"), idLivro, emp.cpfEstudante(), vencimento, relogio.hoje());
            s.movimentacao().registrarDevolucao(d);
            s.acervo().atualizar(livro);
            String email = s.relacionamento().buscarPessoa(emp.cpfEstudante()).map(Pessoa::getEmail).orElse(null);
            return new ComprovanteDevolucao(d.id(), idLivro, d.cpfEstudante(), email, d.vencimento(), d.devolvidoEm());
        });
    }

    /** Decisao da inspecao: BOM_ESTADO -> LIVRE; DANIFICADO -> REPARANDO. */
    @Override
    public SituacaoDTO registrarInspecao(String idLivro, Laudo laudo) {
        return alterarLivro(idLivro, livro -> {
            if (laudo == Laudo.BOM_ESTADO) {
                livro.aprovarInspecao();
            } else {
                livro.enviarParaReparo();
            }
        });
    }

    @Override
    public SituacaoDTO enviarParaReparo(String idLivro) {
        return alterarLivro(idLivro, Livro::enviarParaReparo);
    }

    @Override
    public SituacaoDTO concluirReparo(String idLivro) {
        return alterarLivro(idLivro, Livro::concluirReparo);
    }

    @Override
    public SituacaoDTO registrarLaudoIrrecuperavel(String idLivro) {
        return alterarLivro(idLivro, Livro::registrarLaudoIrrecuperavel);
    }

    // ------------------------------------------------------------------

    private SituacaoDTO alterarLivro(String idLivro, java.util.function.Consumer<Livro> evento) {
        return emTransacao(s -> {
            Livro livro = s.acervo().buscarLivro(idLivro);
            evento.accept(livro);
            s.acervo().atualizar(livro);
            return paraDTO(livro);
        });
    }

    /** Abre a sessao (mesma familia), executa a operacao e confirma; em qualquer erro, desfaz. */
    private <T> T emTransacao(Function<SessaoDeDados, T> operacao) {
        SessaoDeDados s = SessaoDeDados.abrir(fabrica);
        try {
            T resultado = operacao.apply(s);
            s.confirmar();
            return resultado;
        } catch (RuntimeException e) {
            s.desfazer();
            throw e;
        }
    }

    private static SituacaoDTO paraDTO(Livro l) {
        return new SituacaoDTO(l.getId(), l.getTitulo(), l.getSituacao(), l.getVencimento(), l.getNumRenovacoes());
    }
}
