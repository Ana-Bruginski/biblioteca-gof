package br.pucpr.ib2b.devolucao;

import br.pucpr.ib2b.circulacao.ComprovanteDevolucao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Padrao Observer - estado notificado (modelo push).
 * Objeto de valor imutavel com tudo que os ouvintes precisam: o emprestimo ativo ja foi removido,
 * entao eles nao poderiam consultar o vencimento depois.
 */
public record EventoDevolucao(String idDevolucao, String idLivro, String cpfEstudante, String emailEstudante,
                              LocalDate vencimento, LocalDate devolvidoEm, Instant ocorridoEm) {

    public static EventoDevolucao de(ComprovanteDevolucao c) {
        return new EventoDevolucao(c.idDevolucao(), c.idLivro(), c.cpf(), c.emailEstudante(), c.vencimento(),
                c.devolvidoEm(), Instant.now());
    }

    /** Dias corridos inteiros de atraso; devolucao no prazo -> 0. */
    public int diasAtraso() {
        return (int) Math.max(0, ChronoUnit.DAYS.between(vencimento, devolvidoEm));
    }
}
