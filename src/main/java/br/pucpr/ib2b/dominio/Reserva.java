package br.pucpr.ib2b.dominio;

import java.time.LocalDateTime;

/** Transacao de reserva, guardada na associacao "reservas" de Movimentacao. */
public record Reserva(String id, String idLivro, String cpfEstudante, LocalDateTime reservadaEm) { }
