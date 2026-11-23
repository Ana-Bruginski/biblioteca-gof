package br.pucpr.ib2b.dominio;

import java.time.LocalDate;

/** Transacao de devolucao, guardada na associacao "devolucoes" de Movimentacao. */
public record Devolucao(String id, String idLivro, String cpfEstudante, LocalDate vencimento, LocalDate devolvidoEm) { }
