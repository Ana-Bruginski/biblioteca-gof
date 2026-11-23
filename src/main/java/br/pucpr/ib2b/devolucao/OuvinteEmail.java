package br.pucpr.ib2b.devolucao;

/** ConcreteObserver: avisa o estudante por e-mail. Se falhar, o publicador registra e segue. */
public class OuvinteEmail implements OuvinteDeDevolucao {
    private final ServicoDeEmail email;

    public OuvinteEmail(ServicoDeEmail email) {
        this.email = email;
    }

    @Override
    public void aoDevolver(EventoDevolucao ev) {
        String corpo = "Recebemos o livro " + ev.idLivro() + " em " + ev.devolvidoEm()
                + (ev.diasAtraso() > 0 ? ". Atraso de " + ev.diasAtraso() + " dia(s): o boleto sera enviado." : ".");
        email.enviar(ev.emailEstudante(), "Devolucao registrada", corpo);
    }
}
