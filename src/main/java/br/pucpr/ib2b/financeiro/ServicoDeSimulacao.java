package br.pucpr.ib2b.financeiro;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Strategy - outro Context: aplica todas as politicas sem criar Multa nem emitir boleto. */
public class ServicoDeSimulacao {
    private final SeletorDePolitica seletor;

    public ServicoDeSimulacao(SeletorDePolitica seletor) {
        this.seletor = seletor;
    }

    public Map<String, BigDecimal> simular(int dias) {
        Map<String, BigDecimal> resultado = new LinkedHashMap<>();
        for (PoliticaDeMulta p : seletor.todas()) {
            resultado.put(p.id(), p.calcular(dias));
        }
        return resultado;
    }
}
