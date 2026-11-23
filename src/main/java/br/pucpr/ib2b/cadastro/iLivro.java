package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.dominio.Livro;

import java.util.Optional;

/** Interface de manter Livro (Trabalho 02), implementada pelo TratadorDeAcervo. */
public interface iLivro {
    ResultadoCadastro<Livro> inserirLivro(String idLivro, String titulo);

    Optional<Livro> consultarLivro(String idLivro);

    boolean atualizarLivro(String idLivro, String titulo);

    boolean excluirLivro(String idLivro);
}
