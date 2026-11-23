package br.pucpr.ib2b.financeiro.banco;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Padrao Adapter - papel Adaptee (EXTERNO, simulado).
 * Imita a API do banco A: valor em CENTAVOS (long), identificador "nossoNumero" e notificacao em JSON.
 */
public class ApiBancoA {
    public enum StatusA { EM_ABERTO, LIQUIDADO, BAIXADO }

    public record TituloA(String nossoNumero, String linhaDigitavel, long centavos) { }

    private final Map<String, TituloA> titulos = new HashMap<>();
    private final Map<String, StatusA> status = new HashMap<>();
    private final AtomicInteger sequencia = new AtomicInteger(1000);
    private long ultimoValorEmCentavos = -1;

    public synchronized TituloA gerarTitulo(String cpf, long centavos) {
        ultimoValorEmCentavos = centavos;
        String nossoNumero = "A" + sequencia.incrementAndGet();
        TituloA t = new TituloA(nossoNumero, "34191.79001 01043." + nossoNumero, centavos);
        titulos.put(nossoNumero, t);
        status.put(nossoNumero, StatusA.EM_ABERTO);
        return t;
    }

    public synchronized StatusA consultarTitulo(String nossoNumero) {
        return status.get(nossoNumero);
    }

    /** Simula a liquidacao no banco e devolve o JSON que o banco enviaria no webhook. */
    public synchronized String liquidar(String nossoNumero, String idPagamento) {
        status.put(nossoNumero, StatusA.LIQUIDADO);
        long centavos = titulos.get(nossoNumero).centavos();
        return "{\"nossoNumero\":\"" + nossoNumero + "\",\"idPagamento\":\"" + idPagamento
                + "\",\"valorCentavos\":" + centavos + ",\"status\":\"LIQUIDADO\"}";
    }

    public synchronized long ultimoValorEmCentavos() { return ultimoValorEmCentavos; }
}
