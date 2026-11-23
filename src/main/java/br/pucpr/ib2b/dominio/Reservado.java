package br.pucpr.ib2b.dominio;

import java.time.LocalDate;

/** ConcreteState RESERVADO. */
public class Reservado extends EstadoLivro {
    public static final Reservado INSTANCIA = new Reservado();

    private Reservado() { }

    /** RESERVADO --emprestar(e) [e = reservante] / vencimento := hoje + prazo--> EMPRESTADO */
    @Override
    void emprestar(Livro l, Estudante e, LocalDate hoje) {
        if (e == null || !e.getCpf().equals(l.getCpfReservante())) {
            throw new ReservaInvalidaException("a reserva do livro " + l.getId() + " pertence a outro estudante");
        }
        l.definirVencimento(hoje.plusDays(Livro.PRAZO_EMPRESTIMO_DIAS));
        l.mudarPara(Emprestado.INSTANCIA);
    }

    @Override
    public Situacao situacao() { return Situacao.RESERVADO; }
}
