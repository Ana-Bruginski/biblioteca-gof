package br.pucpr.ib2b.dominio;

/** ConcreteState REPARANDO. */
public class Reparando extends EstadoLivro {
    public static final Reparando INSTANCIA = new Reparando();

    private Reparando() { }

    /** REPARANDO --concluirReparo() / limparCiclo()--> LIVRE */
    @Override
    void concluirReparo(Livro l) {
        l.limparCiclo();
        l.mudarPara(Livre.INSTANCIA);
    }

    /** REPARANDO --registrarLaudoIrrecuperavel()--> INCINERADO (extensao pedida na lista) */
    @Override
    void registrarLaudoIrrecuperavel(Livro l) {
        l.limparCiclo();
        l.mudarPara(Incinerado.INSTANCIA);
    }

    @Override
    public Situacao situacao() { return Situacao.REPARANDO; }
}
