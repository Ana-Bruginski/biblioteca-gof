package br.pucpr.ib2b.dominio;

/** ConcreteState LIVRE. Instancia unica (Singleton): o estado nao tem atributos proprios. */
public class Livre extends EstadoLivro {
    public static final Livre INSTANCIA = new Livre();

    private Livre() { }

    /** LIVRE --reservar(e) [e cadastrado] / reservante := e--> RESERVADO */
    @Override
    void reservar(Livro l, Estudante e) {
        if (e == null) {                       // defesa: o tratador ja verificou no Relacionamento
            throw new ReservaInvalidaException("estudante nao cadastrado");
        }
        l.definirReservante(e.getCpf());
        l.mudarPara(Reservado.INSTANCIA);
    }

    /** LIVRE --enviarParaReparo() [dano identificado no acervo]--> REPARANDO (T9 do Trabalho 02) */
    @Override
    void enviarParaReparo(Livro l) {
        l.mudarPara(Reparando.INSTANCIA);
    }

    /** LIVRE --excluirLivro()--> estado final. So um livro LIVRE pode sair do acervo. */
    @Override
    void excluir(Livro l) {
        // permitido: quem remove do armazenamento e o TratadorDeAcervo
    }

    @Override
    public Situacao situacao() { return Situacao.LIVRE; }
}
