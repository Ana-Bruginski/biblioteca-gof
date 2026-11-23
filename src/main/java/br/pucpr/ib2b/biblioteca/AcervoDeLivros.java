package br.pucpr.ib2b.biblioteca;

import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.dominio.RecursoNaoEncontradoException;
import br.pucpr.ib2b.persistencia.RepositorioLivro;

import java.util.List;
import java.util.Optional;

/** Ponto interno de acesso aos livros (Acervo do Trabalho 02). Os tratadores delegam a ele. */
public class AcervoDeLivros {
    private final RepositorioLivro livros;

    public AcervoDeLivros(RepositorioLivro livros) {
        this.livros = livros;
    }

    public Livro buscarLivro(String id) {
        return livros.buscar(id).orElseThrow(() -> new RecursoNaoEncontradoException("livro " + id + " nao cadastrado"));
    }

    public Optional<Livro> procurarLivro(String id) { return livros.buscar(id); }

    public List<Livro> listar() { return livros.listar(); }

    public void incluir(Livro l) { livros.salvar(l); }

    public void atualizar(Livro l) { livros.salvar(l); }

    public void excluir(String id) { livros.remover(id); }
}
