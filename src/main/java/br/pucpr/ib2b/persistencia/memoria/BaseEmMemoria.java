package br.pucpr.ib2b.persistencia.memoria;

/** Dados "confirmados" da familia em memoria (o equivalente ao banco de dados). */
public class BaseEmMemoria {
    private Tabelas confirmadas = new Tabelas();

    /** Copia usada por uma unidade de trabalho; as escritas ficam nela ate confirmar(). */
    synchronized Tabelas copiar() {
        return confirmadas.copia();
    }

    /** COMMIT: as tabelas da unidade passam a ser as confirmadas. */
    synchronized void substituir(Tabelas novas) {
        this.confirmadas = novas.copia();
    }
}
