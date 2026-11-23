package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.dominio.Pessoa;

import java.util.Optional;

/** Interface de manter Pessoa (Trabalho 02), implementada pelo TratadorDePessoa. */
public interface iPessoa {
    ResultadoCadastro<Pessoa> inserirPessoa(String cpf, String nome, String sobrenome, String email);

    Optional<Pessoa> consultarPessoa(String cpf);

    boolean atualizarPessoa(String cpf, String nome, String sobrenome, String email);

    boolean excluirPessoa(String cpf);
}
