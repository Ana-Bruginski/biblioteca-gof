package br.pucpr.ib2b.biblioteca;

import br.pucpr.ib2b.dominio.Atendente;
import br.pucpr.ib2b.dominio.Estudante;
import br.pucpr.ib2b.dominio.Pessoa;
import br.pucpr.ib2b.persistencia.RepositorioRelacionamento;

import java.util.Optional;

/** Ponto interno de acesso a pessoas, estudantes e atendentes (associacoes distintas). */
public class Relacionamento {
    private final RepositorioRelacionamento repositorio;

    public Relacionamento(RepositorioRelacionamento repositorio) {
        this.repositorio = repositorio;
    }

    public Optional<Pessoa> buscarPessoa(String cpf) { return repositorio.buscarPessoa(cpf); }

    public void gravarPessoa(Pessoa p) { repositorio.salvarPessoa(p); }

    public void excluirPessoa(String cpf) { repositorio.removerPessoa(cpf); }

    /** Devolve null quando nao ha estudante com o CPF (multiplicidade 0..1). */
    public Estudante buscarEstudante(String cpf) { return repositorio.buscarEstudante(cpf).orElse(null); }

    public void gravarEstudante(Estudante e) { repositorio.salvarEstudante(e); }

    public void excluirEstudante(String cpf) { repositorio.removerEstudante(cpf); }

    public Optional<Atendente> buscarAtendente(String cpf) { return repositorio.buscarAtendente(cpf); }

    public void gravarAtendente(Atendente a) { repositorio.salvarAtendente(a); }

    public void excluirAtendente(String cpf) { repositorio.removerAtendente(cpf); }
}
