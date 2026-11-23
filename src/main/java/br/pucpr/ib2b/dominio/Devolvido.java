package br.pucpr.ib2b.dominio;

/**
 * ConcreteState DEVOLVIDO: aguarda a inspecao.
 * Nao sobrescreve reservar(): a recusa herdada impede reservar um livro ainda nao inspecionado.
 */
public class Devolvido extends EstadoLivro {
    public static final Devolvido INSTANCIA = new Devolvido();

    private Devolvido() { }

    /** DEVOLVIDO --aprovarInspecao() / limparCiclo()--> LIVRE */
    @Override
    void aprovarInspecao(Livro l) {
        l.limparCiclo();
        l.mudarPara(Livre.INSTANCIA);
    }

    /** DEVOLVIDO --enviarParaReparo()--> REPARANDO */
    @Override
    void enviarParaReparo(Livro l) {
        l.mudarPara(Reparando.INSTANCIA);
    }

    @Override
    public Situacao situacao() { return Situacao.DEVOLVIDO; }
}
