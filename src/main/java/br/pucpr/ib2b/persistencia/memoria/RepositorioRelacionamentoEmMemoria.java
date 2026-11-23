package br.pucpr.ib2b.persistencia.memoria;

import br.pucpr.ib2b.dominio.Atendente;
import br.pucpr.ib2b.dominio.Estudante;
import br.pucpr.ib2b.dominio.Pessoa;
import br.pucpr.ib2b.persistencia.RepositorioRelacionamento;

import java.util.Optional;

/** Guarda e devolve copias, para que alteracoes fora de uma transacao nao "vazem" para a base. */
public class RepositorioRelacionamentoEmMemoria implements RepositorioRelacionamento {
    private final UnidadeDeTrabalhoEmMemoria uow;

    RepositorioRelacionamentoEmMemoria(UnidadeDeTrabalhoEmMemoria uow) {
        this.uow = uow;
    }

    public UnidadeDeTrabalhoEmMemoria unidade() { return uow; }

    @Override
    public Optional<Pessoa> buscarPessoa(String cpf) {
        return Optional.ofNullable(uow.tabelas().pessoas.get(cpf)).map(Pessoa::copiar);
    }

    @Override
    public void salvarPessoa(Pessoa p) { uow.tabelas().pessoas.put(p.getCpf(), p.copiar()); }

    @Override
    public void removerPessoa(String cpf) { uow.tabelas().pessoas.remove(cpf); }

    @Override
    public Optional<Estudante> buscarEstudante(String cpf) {
        return Optional.ofNullable(uow.tabelas().estudantes.get(cpf)).map(Estudante::copiar);
    }

    @Override
    public void salvarEstudante(Estudante e) { uow.tabelas().estudantes.put(e.getCpf(), e.copiar()); }

    @Override
    public void removerEstudante(String cpf) { uow.tabelas().estudantes.remove(cpf); }

    @Override
    public Optional<Atendente> buscarAtendente(String cpf) {
        return Optional.ofNullable(uow.tabelas().atendentes.get(cpf)).map(Atendente::copiar);
    }

    @Override
    public void salvarAtendente(Atendente a) { uow.tabelas().atendentes.put(a.getCpf(), a.copiar()); }

    @Override
    public void removerAtendente(String cpf) { uow.tabelas().atendentes.remove(cpf); }
}
