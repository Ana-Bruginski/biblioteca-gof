package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.dominio.Estudante;

import java.util.Optional;

/** Interface de manter Estudante (Trabalho 02), implementada pelo TratadorDeEstudante. */
public interface iEstudante {
    ResultadoCadastro<Estudante> inserirEstudante(String cpf, String idEstudante, String matricula);

    Optional<Estudante> consultarEstudante(String cpf);

    boolean atualizarEstudante(String cpf, String matricula);

    boolean excluirEstudante(String cpf);
}
