package br.pucpr.ib2b.solicitacao;

import br.pucpr.ib2b.circulacao.iCirculacao;

import java.time.Instant;
import java.util.UUID;

/**
 * Padrao Command - papel Command.
 * A solicitacao e um objeto com identificador, autor, instante de criacao e a operacao executar().
 * O receptor (iCirculacao) e entregue pelo invocador, para que as telas nao precisem conhece-lo.
 * Nao ha desfazer(): a entrega fisica do livro nao e revertida por software.
 */
public abstract class SolicitacaoDeCirculacao {
    private final UUID id;
    private final String autor;
    private final Instant criadaEm;

    protected SolicitacaoDeCirculacao(UUID id, String autor, Instant criadaEm) {
        this.id = id;
        this.autor = autor;
        this.criadaEm = criadaEm;
    }

    public abstract ResultadoSolicitacao executar(iCirculacao circulacao);

    public UUID getId() { return id; }

    public String getAutor() { return autor; }

    public Instant getCriadaEm() { return criadaEm; }
}
