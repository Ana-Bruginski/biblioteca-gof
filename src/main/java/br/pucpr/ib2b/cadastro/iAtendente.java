package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.dominio.Atendente;

import java.util.Optional;

/** Interface de manter Atendente (Trabalho 02), implementada pelo TratadorDeAtendente. */
public interface iAtendente {
    ResultadoCadastro<Atendente> inserirAtendente(String cpf, String idAtendente, String setor);

    Optional<Atendente> consultarAtendente(String cpf);

    boolean atualizarAtendente(String cpf, String setor);

    boolean excluirAtendente(String cpf);
}
