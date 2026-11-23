package br.pucpr.ib2b.financeiro.banco;

import java.math.BigDecimal;

/** Notificacao de pagamento ja traduzida para o formato institucional. */
public record ConfirmacaoPagamento(CodigoBanco banco, String idExterno, String idPagamento, BigDecimal valorPago) { }
