package br.pucpr.ib2b.financeiro.banco;

/** Padrao Adapter - papel Target (entrada): porta que recebe confirmacoes ja traduzidas. */
public interface ReceptorDePagamento {
    void confirmar(ConfirmacaoPagamento confirmacao);
}
