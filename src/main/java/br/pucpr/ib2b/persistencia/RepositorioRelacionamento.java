package br.pucpr.ib2b.persistencia;

import br.pucpr.ib2b.dominio.Atendente;
import br.pucpr.ib2b.dominio.Estudante;
import br.pucpr.ib2b.dominio.Pessoa;

import java.util.Optional;

/** Abstract Factory - AbstractProduct. Pessoas, estudantes e atendentes em colecoes distintas. */
public interface RepositorioRelacionamento {
    Optional<Pessoa> buscarPessoa(String cpf);

    void salvarPessoa(Pessoa p);

    void removerPessoa(String cpf);

    Optional<Estudante> buscarEstudante(String cpf);

    void salvarEstudante(Estudante e);

    void removerEstudante(String cpf);

    Optional<Atendente> buscarAtendente(String cpf);

    void salvarAtendente(Atendente a);

    void removerAtendente(String cpf);
}
