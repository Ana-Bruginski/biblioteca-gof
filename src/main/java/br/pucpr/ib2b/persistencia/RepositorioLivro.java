package br.pucpr.ib2b.persistencia;

import br.pucpr.ib2b.dominio.Livro;

import java.util.List;
import java.util.Optional;

/** Abstract Factory - AbstractProduct. */
public interface RepositorioLivro {
    Optional<Livro> buscar(String id);

    List<Livro> listar();

    void salvar(Livro livro);

    void remover(String id);
}
