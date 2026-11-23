package br.pucpr.ib2b.circulacao;

import java.time.LocalDate;

public record ComprovanteRenovacao(String idLivro, LocalDate novoVencimento, int numRenovacoes) { }
