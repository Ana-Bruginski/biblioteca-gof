package br.pucpr.ib2b.dominio;

/** Evento que chegou a um estado que nao o aceita (State: implementacao padrao de EstadoLivro). */
public class OperacaoInvalidaException extends ExcecaoDeDominio {
    public OperacaoInvalidaException(Situacao situacao, String operacao) {
        super("operacao '" + operacao + "' nao e permitida quando o livro esta " + situacao);
    }
}
