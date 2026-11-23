package br.pucpr.ib2b.dominio;

import java.time.LocalDate;

/**
 * Padrao State - papel State.
 * Declara todos os eventos da maquina de estados. A implementacao padrao recusa o evento
 * SEM alterar o Livro; cada estado concreto sobrescreve apenas o que e valido para ele.
 */
public abstract class EstadoLivro {

    void reservar(Livro l, Estudante e) { throw recusa("reservar"); }

    void emprestar(Livro l, Estudante e, LocalDate hoje) { throw recusa("emprestar"); }

    void renovar(Livro l) { throw recusa("renovar"); }

    void devolver(Livro l) { throw recusa("devolver"); }

    void aprovarInspecao(Livro l) { throw recusa("aprovarInspecao"); }

    void enviarParaReparo(Livro l) { throw recusa("enviarParaReparo"); }

    void concluirReparo(Livro l) { throw recusa("concluirReparo"); }

    void registrarLaudoIrrecuperavel(Livro l) { throw recusa("registrarLaudoIrrecuperavel"); }

    void excluir(Livro l) { throw recusa("excluirLivro"); }

    public abstract Situacao situacao();

    protected OperacaoInvalidaException recusa(String operacao) {
        return new OperacaoInvalidaException(situacao(), operacao);
    }
}
