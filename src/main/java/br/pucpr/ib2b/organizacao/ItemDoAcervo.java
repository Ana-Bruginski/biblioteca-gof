package br.pucpr.ib2b.organizacao;

import java.util.List;

/** Padrao Composite - papel Leaf: representa um exemplar pelo id do Livro (nao o substitui). */
public class ItemDoAcervo extends ComponenteDoAcervo {
    private final String idLivro;

    public ItemDoAcervo(String idLivro) {
        super(idLivro);
        this.idLivro = idLivro;
    }

    public String getIdLivro() { return idLivro; }

    @Override
    public int contarLivros() { return 1; }

    @Override
    public List<String> listarIds() { return List.of(idLivro); }
}
