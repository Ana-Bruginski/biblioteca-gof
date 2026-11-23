package br.pucpr.ib2b.persistencia;

import br.pucpr.ib2b.dominio.Devolucao;
import br.pucpr.ib2b.dominio.Emprestimo;
import br.pucpr.ib2b.dominio.Reserva;

import java.util.List;
import java.util.Optional;

/** Abstract Factory - AbstractProduct. Reservas, emprestimos e devolucoes em colecoes distintas. */
public interface RepositorioMovimentacao {
    void registrarReserva(Reserva r);

    Optional<Reserva> buscarReservaAtiva(String idLivro);

    List<Reserva> listarReservasDe(String cpf);

    void removerReserva(String idLivro);

    void registrarEmprestimo(Emprestimo e);

    Optional<Emprestimo> buscarEmprestimoAtivo(String idLivro);

    void removerEmprestimoAtivo(String idLivro);

    void registrarDevolucao(Devolucao d);

    List<Devolucao> listarDevolucoes();
}
