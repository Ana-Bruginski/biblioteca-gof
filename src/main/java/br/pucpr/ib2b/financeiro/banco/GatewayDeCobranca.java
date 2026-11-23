package br.pucpr.ib2b.financeiro.banco;

/** Padrao Adapter - papel Target (saida): contrato institucional estavel de cobranca. */
public interface GatewayDeCobranca {
    Cobranca emitirBoleto(SolicitacaoCobranca solicitacao);

    SituacaoCobranca consultarSituacao(Cobranca cobranca);
}
