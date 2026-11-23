package br.pucpr.ib2b.tratadores;

import br.pucpr.ib2b.circulacao.ComprovanteEmprestimo;
import br.pucpr.ib2b.circulacao.ComprovanteRenovacao;
import br.pucpr.ib2b.circulacao.ComprovanteReserva;
import br.pucpr.ib2b.circulacao.SituacaoDTO;
import br.pucpr.ib2b.circulacao.iCirculacao;
import br.pucpr.ib2b.dominio.ExcecaoDeDominio;
import br.pucpr.ib2b.solicitacao.ExecutorDeSolicitacoes;
import br.pucpr.ib2b.solicitacao.ResultadoSolicitacao;
import br.pucpr.ib2b.solicitacao.SolicitacaoDeCirculacao;

import java.util.List;

/**
 * Tratador dos servicos do Estudante (f1 a f4 do Trabalho 01).
 * Cliente da Facade: delega tudo a iCirculacao e NAO testa a situacao do livro.
 * Tambem recebe as solicitacoes (Command) criadas pelas telas e as entrega ao executor.
 */
public class TratadorDeEmprestimo {
    private final iCirculacao circulacao;
    private final ExecutorDeSolicitacoes executor;

    public TratadorDeEmprestimo(iCirculacao circulacao, ExecutorDeSolicitacoes executor) {
        this.circulacao = circulacao;
        this.executor = executor;
    }

    /** f1 - solicitar reserva de livro. */
    public Resultado reservarLivro(String idLivro, String cpf) {
        try {
            ComprovanteReserva c = circulacao.reservar(idLivro, cpf);
            return Resultado.ok("reserva " + c.idReserva() + " registrada para o livro " + idLivro);
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    /** f2 - consultar reservas de livros do estudante. */
    public List<ComprovanteReserva> consultarReservas(String cpf) {
        return circulacao.consultarReservas(cpf);
    }

    /** f3 - realizar/efetivar emprestimo de livro reservado. */
    public Resultado emprestarLivro(String idLivro, String cpf) {
        try {
            ComprovanteEmprestimo c = circulacao.efetivarEmprestimo(idLivro, cpf);
            return Resultado.ok("emprestimo " + c.idEmprestimo() + " efetivado; vencimento " + c.vencimento());
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    /** f4 - renovar emprestimo de livro. */
    public Resultado renovarEmprestimo(String idLivro) {
        try {
            ComprovanteRenovacao c = circulacao.renovar(idLivro);
            return Resultado.ok("novo vencimento " + c.novoVencimento() + " (renovacoes: " + c.numRenovacoes() + ")");
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    public Resultado consultarSituacao(String idLivro) {
        try {
            SituacaoDTO s = circulacao.consultar(idLivro);
            return Resultado.ok(s.toString());
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    /** Command: a tela criou a solicitacao; o tratador so a encaminha ao invocador. */
    public ResultadoSolicitacao submeter(SolicitacaoDeCirculacao solicitacao) {
        return executor.submeter(solicitacao);
    }
}
