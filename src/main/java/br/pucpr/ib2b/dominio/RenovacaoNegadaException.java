package br.pucpr.ib2b.dominio;

/** Guarda numRenovacoes < 2 falhou: a terceira renovacao e recusada. */
public class RenovacaoNegadaException extends ExcecaoDeDominio {
    public RenovacaoNegadaException(String idLivro) {
        super("renovacao recusada para o livro " + idLivro + ": limite de 2 renovacoes atingido");
    }
}
