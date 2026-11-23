package br.pucpr.ib2b.persistencia.memoria;

import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.persistencia.RepositorioLivro;

import java.util.List;
import java.util.Optional;

public class RepositorioLivroEmMemoria implements RepositorioLivro {
    private final UnidadeDeTrabalhoEmMemoria uow;

    RepositorioLivroEmMemoria(UnidadeDeTrabalhoEmMemoria uow) {
        this.uow = uow;
    }

    public UnidadeDeTrabalhoEmMemoria unidade() { return uow; }

    @Override
    public Optional<Livro> buscar(String id) {
        return Optional.ofNullable(uow.tabelas().livros.get(id)).map(LinhaLivro::paraLivro);
    }

    @Override
    public List<Livro> listar() {
        return uow.tabelas().livros.values().stream().map(LinhaLivro::paraLivro).toList();
    }

    @Override
    public void salvar(Livro livro) {
        uow.tabelas().livros.put(livro.getId(), LinhaLivro.de(livro));
    }

    @Override
    public void remover(String id) {
        uow.tabelas().livros.remove(id);
    }
}
