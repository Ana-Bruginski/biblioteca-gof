package br.pucpr.ib2b.devolucao;

/** Indicadores de devolucao (modulo de indicadores da extensao didatica). */
public class PainelDeIndicadores {
    private int devolucoes;
    private int devolucoesAtrasadas;
    private int totalDiasAtraso;

    public synchronized void registrar(int diasAtraso) {
        devolucoes++;
        if (diasAtraso > 0) {
            devolucoesAtrasadas++;
            totalDiasAtraso += diasAtraso;
        }
    }

    public synchronized int devolucoes() { return devolucoes; }

    public synchronized int devolucoesAtrasadas() { return devolucoesAtrasadas; }

    public synchronized int totalDiasAtraso() { return totalDiasAtraso; }

    @Override
    public synchronized String toString() {
        return "devolucoes: " + devolucoes + " | atrasadas: " + devolucoesAtrasadas + " | dias de atraso: "
                + totalDiasAtraso;
    }
}
