package br.pucpr.ib2b.financeiro.banco;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Padrao Adapter - papel Adaptee (EXTERNO, simulado).
 * Imita a API do banco B: valor DECIMAL, documento formatado, identificador "txid" e notificacao em XML.
 */
public class ApiBancoB {
    public record CobrancaB(String txid, String copiaECola, BigDecimal valor) { }

    private final Map<String, CobrancaB> cobrancas = new HashMap<>();
    private final Map<String, String> situacoes = new HashMap<>();
    private final AtomicInteger sequencia = new AtomicInteger(500);
    private BigDecimal ultimoValorDecimal;
    private String ultimoDocumento;

    public synchronized CobrancaB abrirCobranca(String documento, BigDecimal valorDecimal) {
        ultimoValorDecimal = valorDecimal;
        ultimoDocumento = documento;
        String txid = "TX" + sequencia.incrementAndGet();
        CobrancaB c = new CobrancaB(txid, "00020126...B..." + txid, valorDecimal);
        cobrancas.put(txid, c);
        situacoes.put(txid, "ATIVA");
        return c;
    }

    public synchronized String statusCobranca(String txid) {
        return situacoes.get(txid);
    }

    /** Simula o pagamento e devolve o XML que o banco B enviaria na notificacao. */
    public synchronized String concluir(String txid, String e2eid) {
        situacoes.put(txid, "CONCLUIDA");
        return "<notificacao><txid>" + txid + "</txid><e2eid>" + e2eid + "</e2eid><valor>"
                + cobrancas.get(txid).valor().toPlainString() + "</valor><situacao>CONCLUIDA</situacao></notificacao>";
    }

    public synchronized BigDecimal ultimoValorDecimal() { return ultimoValorDecimal; }

    public synchronized String ultimoDocumento() { return ultimoDocumento; }
}
