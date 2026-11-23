package br.pucpr.ib2b.financeiro.banco;

/** Apoio a demonstracao: faz o banco simulado "pagar" e disparar a notificacao real do adaptador. */
public interface BancoSimulavel {
    void simularPagamento(Cobranca cobranca, String idPagamento);
}
