package br.pucpr.ib2b.devolucao;

/** ConcreteObserver: alimenta o painel de indicadores. */
public class OuvinteIndicadores implements OuvinteDeDevolucao {
    private final PainelDeIndicadores painel;

    public OuvinteIndicadores(PainelDeIndicadores painel) {
        this.painel = painel;
    }

    @Override
    public void aoDevolver(EventoDevolucao ev) {
        painel.registrar(ev.diasAtraso());
    }
}
