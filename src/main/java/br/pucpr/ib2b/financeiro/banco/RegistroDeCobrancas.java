package br.pucpr.ib2b.financeiro.banco;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Guarda as cobrancas emitidas. quitarSeAberta e atomica (synchronized). */
public class RegistroDeCobrancas {
    private final Map<String, Cobranca> porId = new LinkedHashMap<>();

    public synchronized void registrar(Cobranca c) { porId.put(c.getIdCobranca(), c); }

    public synchronized Optional<Cobranca> buscarPorId(String idCobranca) {
        return Optional.ofNullable(porId.get(idCobranca));
    }

    public synchronized Optional<Cobranca> buscar(CodigoBanco banco, String idExterno) {
        return porId.values().stream()
                .filter(c -> c.getBanco() == banco && c.getIdExterno().equals(idExterno))
                .findFirst();
    }

    /** Atualizacao condicional: so quita se ainda estiver ABERTA. Devolve false se ja estava quitada. */
    public synchronized boolean quitarSeAberta(String idCobranca, String idPagamento) {
        Cobranca c = porId.get(idCobranca);
        if (c == null || c.getSituacao() != SituacaoCobranca.ABERTA) {
            return false;
        }
        c.quitar(idPagamento);
        return true;
    }

    public synchronized List<Cobranca> todas() { return new ArrayList<>(porId.values()); }
}
