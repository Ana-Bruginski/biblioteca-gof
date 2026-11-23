package br.pucpr.ib2b.dominio;

/** ConcreteState EMPRESTADO. A regra de renovacao fica SO aqui (Renovado herda). */
public class Emprestado extends EstadoLivro {
    public static final Emprestado INSTANCIA = new Emprestado();

    protected Emprestado() { }

    /**
     * EMPRESTADO/RENOVADO --renovar() [numRenovacoes < 2] / vencimento += 30; numRenovacoes++--> RENOVADO
     * A guarda e avaliada ANTES de qualquer alteracao: na recusa, vencimento e contador ficam intactos.
     */
    @Override
    void renovar(Livro l) {
        if (l.getNumRenovacoes() >= 2) {       // guarda: numRenovacoes < 2
            throw new RenovacaoNegadaException(l.getId());
        }
        l.prorrogar(Livro.DIAS_POR_RENOVACAO);
        l.mudarPara(Renovado.INSTANCIA);
    }

    /** EMPRESTADO/RENOVADO --devolver()--> DEVOLVIDO */
    @Override
    void devolver(Livro l) {
        l.mudarPara(Devolvido.INSTANCIA);
    }

    @Override
    public Situacao situacao() { return Situacao.EMPRESTADO; }
}
