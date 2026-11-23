package br.pucpr.ib2b.persistencia.memoria;

import br.pucpr.ib2b.dominio.Devolucao;
import br.pucpr.ib2b.dominio.Emprestimo;
import br.pucpr.ib2b.dominio.Reserva;
import br.pucpr.ib2b.persistencia.RepositorioMovimentacao;

import java.util.List;
import java.util.Optional;

public class RepositorioMovimentacaoEmMemoria implements RepositorioMovimentacao {
    private final UnidadeDeTrabalhoEmMemoria uow;

    RepositorioMovimentacaoEmMemoria(UnidadeDeTrabalhoEmMemoria uow) {
        this.uow = uow;
    }

    public UnidadeDeTrabalhoEmMemoria unidade() { return uow; }

    @Override
    public void registrarReserva(Reserva r) { uow.tabelas().reservasPorLivro.put(r.idLivro(), r); }

    @Override
    public Optional<Reserva> buscarReservaAtiva(String idLivro) {
        return Optional.ofNullable(uow.tabelas().reservasPorLivro.get(idLivro));
    }

    @Override
    public List<Reserva> listarReservasDe(String cpf) {
        return uow.tabelas().reservasPorLivro.values().stream().filter(r -> r.cpfEstudante().equals(cpf)).toList();
    }

    @Override
    public void removerReserva(String idLivro) { uow.tabelas().reservasPorLivro.remove(idLivro); }

    @Override
    public void registrarEmprestimo(Emprestimo e) { uow.tabelas().emprestimosPorLivro.put(e.idLivro(), e); }

    @Override
    public Optional<Emprestimo> buscarEmprestimoAtivo(String idLivro) {
        return Optional.ofNullable(uow.tabelas().emprestimosPorLivro.get(idLivro));
    }

    @Override
    public void removerEmprestimoAtivo(String idLivro) { uow.tabelas().emprestimosPorLivro.remove(idLivro); }

    @Override
    public void registrarDevolucao(Devolucao d) { uow.tabelas().devolucoes.put(d.id(), d); }

    @Override
    public List<Devolucao> listarDevolucoes() { return List.copyOf(uow.tabelas().devolucoes.values()); }
}
