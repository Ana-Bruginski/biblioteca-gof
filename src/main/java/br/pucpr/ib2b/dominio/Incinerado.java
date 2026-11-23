package br.pucpr.ib2b.dominio;

/** ConcreteState INCINERADO: estado final, nenhum evento e aceito. */
public class Incinerado extends EstadoLivro {
    public static final Incinerado INSTANCIA = new Incinerado();

    private Incinerado() { }

    @Override
    public Situacao situacao() { return Situacao.INCINERADO; }
}
