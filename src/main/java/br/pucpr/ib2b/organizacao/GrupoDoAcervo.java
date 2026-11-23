package br.pucpr.ib2b.organizacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Padrao Composite - papel Composite: unidade, secao ou subsecao.
 * Soma/concatena os resultados dos filhos (recursao) e protege as invariantes da arvore:
 * sem ciclos, sem componente com dois pais e sem o mesmo livro em dois grupos.
 */
public class GrupoDoAcervo extends ComponenteDoAcervo {
    private final TipoDeGrupo tipo;
    private final List<ComponenteDoAcervo> filhos = new ArrayList<>();

    public GrupoDoAcervo(String nome, TipoDeGrupo tipo) {
        super(nome);
        this.tipo = tipo;
    }

    public void adicionar(ComponenteDoAcervo c) {
        if (c == this || ehAncestralDe(c)) {
            throw new HierarquiaInvalidaException("ciclo: " + c.getNome() + " ja esta acima de " + getNome());
        }
        if (c.getPai() != null) {
            throw new HierarquiaInvalidaException(c.getNome() + " ja pertence ao grupo " + c.getPai().getNome());
        }
        Set<String> jaNaArvore = new HashSet<>(raiz().listarIds());
        for (String id : c.listarIds()) {
            if (jaNaArvore.contains(id)) {
                throw new HierarquiaInvalidaException("o livro " + id + " ja pertence a outro grupo");
            }
        }
        filhos.add(c);
        c.definirPai(this);
    }

    public void remover(ComponenteDoAcervo c) {
        if (filhos.remove(c)) {
            c.definirPai(null);
        }
    }

    @Override
    public int contarLivros() {
        int total = 0;
        for (ComponenteDoAcervo filho : filhos) {
            total += filho.contarLivros();
        }
        return total;
    }

    @Override
    public List<String> listarIds() {
        List<String> ids = new ArrayList<>();
        for (ComponenteDoAcervo filho : filhos) {
            ids.addAll(filho.listarIds());
        }
        return ids;
    }

    public List<ComponenteDoAcervo> getFilhos() { return List.copyOf(filhos); }

    public TipoDeGrupo getTipo() { return tipo; }

    /** c e ancestral deste grupo? (percorre os pais para cima) */
    private boolean ehAncestralDe(ComponenteDoAcervo c) {
        for (GrupoDoAcervo g = getPai(); g != null; g = g.getPai()) {
            if (g == c) {
                return true;
            }
        }
        return false;
    }

    private GrupoDoAcervo raiz() {
        GrupoDoAcervo g = this;
        while (g.getPai() != null) {
            g = g.getPai();
        }
        return g;
    }
}
