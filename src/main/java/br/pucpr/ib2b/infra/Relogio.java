package br.pucpr.ib2b.infra;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Fonte da data atual. Permite simular atrasos nos testes e na demonstracao. */
public interface Relogio {
    LocalDateTime agora();

    default LocalDate hoje() { return agora().toLocalDate(); }
}
