package br.pucpr.ib2b.consulta;

import java.time.Duration;

/** Quais recursos opcionais esta instalacao quer na consulta. */
public record ConfigInstalacao(boolean medicao, boolean cache, Duration ttlCache) { }
