package br.pucpr.ib2b.dominio;

import java.time.LocalDate;

/** Transacao de emprestimo, guardada na associacao "emprestimos" de Movimentacao. */
public record Emprestimo(String id, String idLivro, String cpfEstudante, LocalDate emprestadoEm, LocalDate vencimento) {
    public Emprestimo comVencimento(LocalDate novo) {
        return new Emprestimo(id, idLivro, cpfEstudante, emprestadoEm, novo);
    }
}
