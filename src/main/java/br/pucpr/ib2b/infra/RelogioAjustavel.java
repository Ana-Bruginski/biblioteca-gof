package br.pucpr.ib2b.infra;

import java.time.LocalDateTime;

/** Relogio que comeca na data real e pode ser adiantado (simulacao de atraso). */
public class RelogioAjustavel implements Relogio {
    private LocalDateTime agora;

    public RelogioAjustavel() { this(LocalDateTime.now()); }

    public RelogioAjustavel(LocalDateTime inicio) { this.agora = inicio; }

    @Override
    public synchronized LocalDateTime agora() { return agora; }

    public synchronized void avancarDias(int dias) { agora = agora.plusDays(dias); }

    public synchronized void avancarSegundos(long segundos) { agora = agora.plusSeconds(segundos); }
}
