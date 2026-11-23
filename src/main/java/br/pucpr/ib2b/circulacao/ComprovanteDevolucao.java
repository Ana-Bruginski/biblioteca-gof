package br.pucpr.ib2b.circulacao;

import java.time.LocalDate;

/** Carrega os dados que os ouvintes da devolucao precisam (o emprestimo ativo e removido). */
public record ComprovanteDevolucao(String idDevolucao, String idLivro, String cpf, String emailEstudante,
                                   LocalDate vencimento, LocalDate devolvidoEm) { }
