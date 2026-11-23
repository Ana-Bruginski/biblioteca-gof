package br.pucpr.ib2b.financeiro;

import java.math.BigDecimal;

/** Padrao Strategy - papel Strategy: contrato de calculo da multa. */
public interface PoliticaDeMulta {
    String id();

    /** dias = dias corridos inteiros de atraso (>= 0). Valor em reais, escala 2. */
    BigDecimal calcular(int dias);
}
