package br.pucpr.ib2b.organizacao;

import java.util.List;

/** Padrao Composite - papel Component: grupos e itens respondem as mesmas operacoes. */
public abstract class ComponenteDoAcervo {
    private final String nome;
    private GrupoDoAcervo pai;

    protected ComponenteDoAcervo(String nome) {
        this.nome = nome;
    }

    public abstract int contarLivros();

    public abstract List<String> listarIds();

    public String getNome() { return nome; }

    public GrupoDoAcervo getPai() { return pai; }

    void definirPai(GrupoDoAcervo novoPai) { this.pai = novoPai; }
}
