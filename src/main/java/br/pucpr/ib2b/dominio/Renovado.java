package br.pucpr.ib2b.dominio;

/** ConcreteState RENOVADO: e um emprestimo ja renovado, herda renovar() e devolver(). */
public class Renovado extends Emprestado {
    public static final Renovado INSTANCIA = new Renovado();

    private Renovado() { }

    @Override
    public Situacao situacao() { return Situacao.RENOVADO; }
}
