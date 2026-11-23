package br.pucpr.ib2b.persistencia.memoria;

import br.pucpr.ib2b.persistencia.UnidadeDeTrabalho;

/** ConcreteProduct (familia em memoria): trabalha numa copia; confirmar() publica a copia. */
public class UnidadeDeTrabalhoEmMemoria implements UnidadeDeTrabalho {
    private final BaseEmMemoria base;
    private final Tabelas tabelas;
    private boolean aberta = true;

    UnidadeDeTrabalhoEmMemoria(BaseEmMemoria base) {
        this.base = base;
        this.tabelas = base.copiar();
    }

    Tabelas tabelas() {
        if (!aberta) {
            throw new IllegalStateException("unidade de trabalho ja encerrada");
        }
        return tabelas;
    }

    boolean pertenceA(BaseEmMemoria outra) { return base == outra; }

    @Override
    public void confirmar() {
        if (aberta) {
            base.substituir(tabelas);
            aberta = false;
        }
    }

    @Override
    public void desfazer() {
        aberta = false;     // a copia e descartada: nada chega a base
    }
}
