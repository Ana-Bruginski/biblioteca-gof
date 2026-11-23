package br.pucpr.ib2b.circulacao;

import java.time.LocalDate;

public record ComprovanteEmprestimo(String idEmprestimo, String idLivro, String cpf, LocalDate emprestadoEm,
                                    LocalDate vencimento) { }
