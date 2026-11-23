package br.pucpr.ib2b.tratadores;

import br.pucpr.ib2b.cadastro.iLivro;
import br.pucpr.ib2b.dominio.ExcecaoDeDominio;
import br.pucpr.ib2b.organizacao.OrganizadorDoAcervo;
import br.pucpr.ib2b.organizacao.TipoDeGrupo;

/** Tratador do Administrador para a organizacao hierarquica do acervo (Composite). */
public class TratadorDeOrganizacao {
    private final OrganizadorDoAcervo organizador;
    private final iLivro livros;

    public TratadorDeOrganizacao(OrganizadorDoAcervo organizador, iLivro livros) {
        this.organizador = organizador;
        this.livros = livros;
    }

    public Resultado criarGrupo(String nome, TipoDeGrupo tipo, String nomeDoPai) {
        try {
            organizador.criarGrupo(nome, tipo, nomeDoPai);
            return Resultado.ok("grupo " + nome + " criado em " + nomeDoPai);
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    public Resultado adicionarLivro(String idLivro, String nomeDoGrupo) {
        if (livros.consultarLivro(idLivro).isEmpty()) {
            return Resultado.recusado("livro " + idLivro + " nao cadastrado");
        }
        try {
            organizador.adicionarLivro(idLivro, nomeDoGrupo);
            return Resultado.ok("livro " + idLivro + " adicionado a " + nomeDoGrupo);
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    public Resultado moverLivro(String idLivro, String nomeDoDestino) {
        try {
            organizador.mover(idLivro, nomeDoDestino);
            return Resultado.ok("livro " + idLivro + " movido para " + nomeDoDestino);
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    public Resultado contarLivros(String nomeDoGrupo) {
        try {
            return Resultado.ok(nomeDoGrupo + ": " + organizador.contarLivros(nomeDoGrupo) + " livro(s)");
        } catch (ExcecaoDeDominio e) {
            return Resultado.recusado(e.getMessage());
        }
    }

    public String arvore() { return organizador.desenhar(); }
}
