package br.pucpr.ib2b.devolucao;

/** ConcreteObserver: agenda a inspecao. O livro continua DEVOLVIDO ate a decisao (registrarInspecao). */
public class OuvinteInspecao implements OuvinteDeDevolucao {
    private final AgendaDeInspecao agenda;

    public OuvinteInspecao(AgendaDeInspecao agenda) {
        this.agenda = agenda;
    }

    @Override
    public void aoDevolver(EventoDevolucao ev) {
        agenda.agendar(ev.idLivro());
    }
}
