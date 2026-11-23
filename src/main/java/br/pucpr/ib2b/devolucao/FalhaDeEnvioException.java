package br.pucpr.ib2b.devolucao;

public class FalhaDeEnvioException extends RuntimeException {
    public FalhaDeEnvioException(String mensagem) {
        super(mensagem);
    }
}
