package br.pucpr.ib2b.consulta;

/** So identificador e titulo. NAO contem disponibilidade: o cache nunca decide reservas. */
public record MetadadosLivro(String id, String titulo) { }
