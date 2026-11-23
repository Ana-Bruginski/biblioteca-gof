package br.pucpr.ib2b.financeiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Multa emitida: grava o valor e a politica aplicada NA EMISSAO.
 * Como e imutavel e nao guarda a estrategia, uma troca de politica nao a recalcula.
 */
public record Multa(String idMulta, String idDevolucao, String idLivro, String cpfEstudante, int diasAtraso,
                    BigDecimal valor, String politicaAplicada, LocalDateTime emitidaEm) { }
