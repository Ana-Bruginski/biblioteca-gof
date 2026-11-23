package br.pucpr.ib2b.financeiro;

/** Guarda o identificador da politica vigente. A padrao e a original. */
public class ConfiguracaoAdministrativa {
    private volatile String politicaVigente = "ORIGINAL";

    public String getPoliticaVigente() { return politicaVigente; }

    /** So e chamado depois de o SeletorDePolitica confirmar que o id existe. */
    void definirPolitica(String id) { this.politicaVigente = id; }
}
