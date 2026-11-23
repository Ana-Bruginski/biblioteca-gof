package br.pucpr.ib2b.devolucao;

import br.pucpr.ib2b.financeiro.iFinanceiro;

/** ConcreteObserver: so solicita multa ao Financeiro quando ha atraso. */
public class OuvinteMulta implements OuvinteDeDevolucao {
    private final iFinanceiro financeiro;

    public OuvinteMulta(iFinanceiro financeiro) {
        this.financeiro = financeiro;
    }

    @Override
    public void aoDevolver(EventoDevolucao ev) {
        if (ev.diasAtraso() > 0) {
            financeiro.solicitarMulta(ev.cpfEstudante(), ev.idDevolucao(), ev.idLivro(), ev.diasAtraso());
        }
    }
}
