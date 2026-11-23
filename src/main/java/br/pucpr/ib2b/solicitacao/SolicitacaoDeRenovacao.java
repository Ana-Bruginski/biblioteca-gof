package br.pucpr.ib2b.solicitacao;

import br.pucpr.ib2b.circulacao.ComprovanteRenovacao;
import br.pucpr.ib2b.circulacao.iCirculacao;

import java.time.Instant;
import java.util.UUID;

/** Padrao Command - ConcreteCommand de renovacao. */
public class SolicitacaoDeRenovacao extends SolicitacaoDeCirculacao {
    private final String idLivro;

    public SolicitacaoDeRenovacao(UUID id, String autor, Instant criadaEm, String idLivro) {
        super(id, autor, criadaEm);
        this.idLivro = idLivro;
    }

    public static SolicitacaoDeRenovacao nova(String autor, String idLivro) {
        return new SolicitacaoDeRenovacao(UUID.randomUUID(), autor, Instant.now(), idLivro);
    }

    @Override
    public ResultadoSolicitacao executar(iCirculacao circulacao) {
        ComprovanteRenovacao c = circulacao.renovar(idLivro);
        return ResultadoSolicitacao.executada(getId(),
                "livro " + idLivro + " renovado; novo vencimento " + c.novoVencimento()
                        + " (renovacoes: " + c.numRenovacoes() + ")");
    }
}
