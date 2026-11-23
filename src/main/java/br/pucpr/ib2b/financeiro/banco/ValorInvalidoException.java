package br.pucpr.ib2b.financeiro.banco;

public class ValorInvalidoException extends RuntimeException {
    public ValorInvalidoException(String mensagem) {
        super(mensagem);
    }
}
