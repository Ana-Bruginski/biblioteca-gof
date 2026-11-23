package br.pucpr.ib2b.financeiro;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** ConcreteStrategy TOLERANCIA: cobra so os dias que excedem 2 -> max(0; d - 2) x R$ 1,00. */
public class MultaComTolerancia implements PoliticaDeMulta {
    private static final BigDecimal VALOR_DIA = new BigDecimal("1.00");
    private static final int TOLERANCIA_DIAS = 2;

    @Override
    public String id() { return "TOLERANCIA"; }

    @Override
    public BigDecimal calcular(int dias) {
        int cobrados = Math.max(0, dias - TOLERANCIA_DIAS);
        return VALOR_DIA.multiply(BigDecimal.valueOf(cobrados)).setScale(2, RoundingMode.HALF_EVEN);
    }
}
