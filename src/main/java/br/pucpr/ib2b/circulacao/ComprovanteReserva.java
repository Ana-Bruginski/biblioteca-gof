package br.pucpr.ib2b.circulacao;

import java.time.LocalDateTime;

/** DTO devolvido pela fachada: a interface nunca recebe colecoes de Movimentacao. */
public record ComprovanteReserva(String idReserva, String idLivro, String cpf, LocalDateTime reservadaEm) { }
