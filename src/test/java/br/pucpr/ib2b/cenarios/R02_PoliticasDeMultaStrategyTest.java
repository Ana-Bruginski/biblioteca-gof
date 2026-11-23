package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.financeiro.Multa;
import br.pucpr.ib2b.financeiro.MultaComTeto;
import br.pucpr.ib2b.financeiro.MultaComTolerancia;
import br.pucpr.ib2b.financeiro.MultaDiaria;
import br.pucpr.ib2b.financeiro.PoliticaDeMulta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 2 - Politicas de calculo de multa - Strategy. */
class R02_PoliticasDeMultaStrategyTest extends CenarioBase {

    @Test
    void R02_cincoDiasDaoCincoReaisNaOriginalETresReaisNaTolerancia() {
        assertEquals(new BigDecimal("5.00"), new MultaDiaria().calcular(5));
        assertEquals(new BigDecimal("3.00"), new MultaComTolerancia().calcular(5));
        assertEquals(new BigDecimal("5.00"), new MultaComTeto().calcular(5));

        Map<String, BigDecimal> simulacao = sistema.administracao().simularMultas(5);
        assertEquals(new BigDecimal("5.00"), simulacao.get("ORIGINAL"));
        assertEquals(new BigDecimal("3.00"), simulacao.get("TOLERANCIA"));
    }

    @Test
    void R02_semAtrasoOValorEZeroEmTodasAsPoliticas() {
        for (PoliticaDeMulta p : List.of(new MultaDiaria(), new MultaComTeto(), new MultaComTolerancia())) {
            assertEquals(new BigDecimal("0.00"), p.calcular(0), p.id());
        }
    }

    @Test
    void R02_tetoLimitaEmVinteReais() {
        assertEquals(new BigDecimal("20.00"), new MultaComTeto().calcular(30));
    }

    @Test
    void R02_trocaDePoliticaNaoRecalculaMultaJaEmitida() {
        Multa antes = sistema.servicoDeMulta().gerarMulta(ANA, "DEV-1", "L1", 5).orElseThrow();
        assertTrue(sistema.administracao().definirPoliticaDeMulta("TOLERANCIA").sucesso());
        Multa depois = sistema.servicoDeMulta().gerarMulta(ANA, "DEV-2", "L2", 5).orElseThrow();

        assertEquals(new BigDecimal("5.00"), antes.valor());
        assertEquals("ORIGINAL", antes.politicaAplicada());
        assertEquals(new BigDecimal("3.00"), depois.valor());
        assertEquals("TOLERANCIA", depois.politicaAplicada());
        assertEquals(new BigDecimal("5.00"), sistema.multas().consultarMultas(ANA).get(0).valor());
    }

    @Test
    void R02_politicaInexistenteERecusadaAoConfigurar() {
        assertFalse(sistema.administracao().definirPoliticaDeMulta("XYZ").sucesso());
        assertEquals("ORIGINAL", sistema.administracao().politicaVigente());
    }
}
