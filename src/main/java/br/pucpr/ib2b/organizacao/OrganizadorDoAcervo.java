package br.pucpr.ib2b.organizacao;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Padrao Composite - papel Client. Navega a partir da raiz e move itens.
 * Mover altera so o pai do item: o Livro (id, estado) e a Movimentacao nao sao tocados.
 */
public class OrganizadorDoAcervo {
    private final GrupoDoAcervo raiz = new GrupoDoAcervo("Acervo iB2B", TipoDeGrupo.UNIDADE);
    private final Map<String, GrupoDoAcervo> grupos = new LinkedHashMap<>();

    public OrganizadorDoAcervo() {
        grupos.put(raiz.getNome(), raiz);
    }

    public GrupoDoAcervo raiz() { return raiz; }

    public GrupoDoAcervo criarGrupo(String nome, TipoDeGrupo tipo, String nomeDoPai) {
        if (grupos.containsKey(nome)) {
            throw new HierarquiaInvalidaException("ja existe um grupo chamado " + nome);
        }
        GrupoDoAcervo g = new GrupoDoAcervo(nome, tipo);
        grupo(nomeDoPai).adicionar(g);
        grupos.put(nome, g);
        return g;
    }

    public void adicionarLivro(String idLivro, String nomeDoGrupo) {
        grupo(nomeDoGrupo).adicionar(new ItemDoAcervo(idLivro));
    }

    /** Remover + adicionar como uma operacao unica: se adicionar falhar, o item volta a origem. */
    public void mover(String idLivro, String nomeDoDestino) {
        ItemDoAcervo item = localizar(raiz, idLivro)
                .orElseThrow(() -> new HierarquiaInvalidaException("livro " + idLivro + " nao esta organizado"));
        GrupoDoAcervo origem = item.getPai();
        GrupoDoAcervo destino = grupo(nomeDoDestino);
        origem.remover(item);
        try {
            destino.adicionar(item);
        } catch (HierarquiaInvalidaException e) {
            origem.adicionar(item);
            throw e;
        }
    }

    public int contarLivros(String nomeDoGrupo) { return grupo(nomeDoGrupo).contarLivros(); }

    public GrupoDoAcervo grupo(String nome) {
        GrupoDoAcervo g = grupos.get(nome);
        if (g == null) {
            throw new HierarquiaInvalidaException("grupo inexistente: " + nome);
        }
        return g;
    }

    private Optional<ItemDoAcervo> localizar(GrupoDoAcervo g, String idLivro) {
        for (ComponenteDoAcervo c : g.getFilhos()) {
            if (c instanceof ItemDoAcervo i && i.getIdLivro().equals(idLivro)) {
                return Optional.of(i);
            }
            if (c instanceof GrupoDoAcervo sub) {
                Optional<ItemDoAcervo> achado = localizar(sub, idLivro);
                if (achado.isPresent()) {
                    return achado;
                }
            }
        }
        return Optional.empty();
    }

    /** Arvore em texto, para a interface. */
    public String desenhar() {
        StringBuilder sb = new StringBuilder();
        desenhar(raiz, 0, sb);
        return sb.toString();
    }

    private void desenhar(ComponenteDoAcervo c, int nivel, StringBuilder sb) {
        sb.append("  ".repeat(nivel));
        if (c instanceof GrupoDoAcervo g) {
            sb.append("[").append(g.getTipo()).append("] ").append(g.getNome())
                    .append(" (").append(g.contarLivros()).append(" livros)\n");
            for (ComponenteDoAcervo filho : g.getFilhos()) {
                desenhar(filho, nivel + 1, sb);
            }
        } else {
            sb.append("- livro ").append(c.getNome()).append('\n');
        }
    }
}
