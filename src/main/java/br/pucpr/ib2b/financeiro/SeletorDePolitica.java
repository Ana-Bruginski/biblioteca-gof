package br.pucpr.ib2b.financeiro;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Traduz o identificador gravado na configuracao administrativa no objeto de politica.
 * Uma nova politica = nova classe + registrar(...) na montagem; nenhum if por politica.
 */
public class SeletorDePolitica {
    private final Map<String, PoliticaDeMulta> politicas = new LinkedHashMap<>();
    private final ConfiguracaoAdministrativa config;

    public SeletorDePolitica(ConfiguracaoAdministrativa config) {
        this.config = config;
    }

    public void registrar(PoliticaDeMulta p) { politicas.put(p.id(), p); }

    public PoliticaDeMulta obter(String id) {
        PoliticaDeMulta p = politicas.get(id);
        if (p == null) {
            throw new IllegalArgumentException("politica de multa inexistente: " + id);
        }
        return p;
    }

    public PoliticaDeMulta vigente() { return obter(config.getPoliticaVigente()); }

    /** Valida o id ANTES de gravar, para o calculo nunca encontrar um id invalido. */
    public void definirVigente(String id) {
        obter(id);
        config.definirPolitica(id);
    }

    public List<PoliticaDeMulta> todas() { return new ArrayList<>(politicas.values()); }
}
