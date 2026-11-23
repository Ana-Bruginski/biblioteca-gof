package br.pucpr.ib2b.cadastro;

import br.pucpr.ib2b.consulta.ConsultaAcervo;
import br.pucpr.ib2b.consulta.Criterio;
import br.pucpr.ib2b.consulta.MetadadosLivro;
import br.pucpr.ib2b.dominio.ExcecaoDeDominio;
import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;

import java.util.List;
import java.util.Optional;

/**
 * Template Method - ConcreteClass do cadastro de livros; realiza iLivro.
 * Tambem expoe a consulta publica ao catalogo, que usa a cadeia de decoradores (Decorator).
 */
public class TratadorDeAcervo extends TratadorDeCadastro<Livro, DadosLivro> implements iLivro {
    private final ConsultaAcervo consulta;

    public TratadorDeAcervo(FabricaDePersistencia fabrica, ConsultaAcervo consulta) {
        super(fabrica);
        this.consulta = consulta;
    }

    @Override
    public ResultadoCadastro<Livro> inserirLivro(String idLivro, String titulo) {
        return incluir(new DadosLivro(idLivro, titulo));
    }

    @Override
    protected Livro buscarExistente(DadosLivro d) {
        return ler(s -> s.acervo().procurarLivro(d.idLivro()).orElse(null));
    }

    @Override
    protected void validar(DadosLivro d) {
        exigir(!vazio(d.idLivro()), "o id do livro e obrigatorio");
        exigir(!vazio(d.titulo()), "o titulo do livro e obrigatorio");
    }

    @Override
    protected Livro criar(DadosLivro d) { return new Livro(d.idLivro().trim(), d.titulo().trim()); }

    @Override
    protected void armazenar(Livro livro) { gravar(s -> s.acervo().incluir(livro)); }

    @Override
    public Optional<Livro> consultarLivro(String idLivro) {
        return ler(s -> s.acervo().procurarLivro(idLivro));
    }

    @Override
    public boolean atualizarLivro(String idLivro, String titulo) {
        if (vazio(titulo) || consultarLivro(idLivro).isEmpty()) {
            return false;
        }
        gravar(s -> {
            Livro l = s.acervo().buscarLivro(idLivro);
            l.setTitulo(titulo.trim());
            s.acervo().atualizar(l);
        });
        return true;
    }

    /** So um livro LIVRE pode ser excluido: quem decide e o estado (Livro.excluir). */
    @Override
    public boolean excluirLivro(String idLivro) {
        try {
            gravar(s -> {
                Livro l = s.acervo().buscarLivro(idLivro);
                l.excluir();
                s.acervo().excluir(idLivro);
            });
            return true;
        } catch (ExcecaoDeDominio e) {
            return false;
        }
    }

    /** Consulta publica de metadados (id e titulo). Nao informa disponibilidade. */
    public List<MetadadosLivro> buscarNoCatalogo(String termo) {
        return consulta.buscarMetadados(new Criterio(termo));
    }
}
