package br.pucpr.ib2b.devolucao;

/** Registro de uma reacao que falhou e deve ser reenviada (fila de reenvio). */
public record FalhaDeOuvinte(String ouvinte, String idDevolucao, String motivo) { }
