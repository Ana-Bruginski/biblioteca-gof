package br.pucpr.ib2b.financeiro.banco;

import java.math.BigDecimal;

/** Pedido institucional de cobranca: valor sempre em reais (BigDecimal). */
public record SolicitacaoCobranca(String idCobranca, String cpf, BigDecimal valor) { }
