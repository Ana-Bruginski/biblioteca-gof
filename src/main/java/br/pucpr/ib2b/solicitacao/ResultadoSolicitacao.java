package br.pucpr.ib2b.solicitacao;

import java.util.UUID;

public record ResultadoSolicitacao(UUID idSolicitacao, Status status, String mensagem) {
    public enum Status { EXECUTADA, RECUSADA, EM_PROCESSAMENTO }

    public static ResultadoSolicitacao executada(UUID id, String mensagem) {
        return new ResultadoSolicitacao(id, Status.EXECUTADA, mensagem);
    }

    public static ResultadoSolicitacao recusada(UUID id, String motivo) {
        return new ResultadoSolicitacao(id, Status.RECUSADA, motivo);
    }

    public static ResultadoSolicitacao emProcessamento(UUID id) {
        return new ResultadoSolicitacao(id, Status.EM_PROCESSAMENTO, "solicitacao ja esta em execucao");
    }

    @Override
    public String toString() {
        return status + ": " + mensagem + " (solicitacao " + idSolicitacao + ")";
    }
}
