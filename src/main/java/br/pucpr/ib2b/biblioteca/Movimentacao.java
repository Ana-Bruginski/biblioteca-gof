package br.pucpr.ib2b.biblioteca;

import br.pucpr.ib2b.dominio.Devolucao;
import br.pucpr.ib2b.dominio.Emprestimo;
import br.pucpr.ib2b.dominio.Reserva;
import br.pucpr.ib2b.persistencia.RepositorioMovimentacao;

import java.util.List;
import java.util.Optional;

/** Ponto interno UNICO de acesso a reservas, emprestimos e devolucoes (associacoes distintas). */
public class Movimentacao {
    private final RepositorioMovimentacao repositorio;

    public Movimentacao(RepositorioMovimentacao repositorio) {
        this.repositorio = repositorio;
    }

    public void registrarReserva(Reserva r) { repositorio.registrarReserva(r); }

    public Optional<Reserva> buscarReservaAtiva(String idLivro) { return repositorio.buscarReservaAtiva(idLivro); }

    public List<Reserva> listarReservasDe(String cpf) { return repositorio.listarReservasDe(cpf); }

    public void removerReserva(Reserva r) { repositorio.removerReserva(r.idLivro()); }

    public void registrarEmprestimo(Emprestimo e) { repositorio.registrarEmprestimo(e); }

    public Optional<Emprestimo> buscarEmprestimoAtivo(String idLivro) { return repositorio.buscarEmprestimoAtivo(idLivro); }

    public void removerEmprestimoAtivo(String idLivro) { repositorio.removerEmprestimoAtivo(idLivro); }

    public void registrarDevolucao(Devolucao d) { repositorio.registrarDevolucao(d); }

    public List<Devolucao> listarDevolucoes() { return repositorio.listarDevolucoes(); }
}
