package br.pucpr.ib2b.devolucao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Padrao Observer - papel Subject.
 * publicar() e chamado DEPOIS da confirmacao da devolucao. Cada ouvinte e isolado:
 * uma excecao e registrada e nao se propaga, entao nao afeta os demais nem desfaz a devolucao.
 */
public class PublicadorDeDevolucao {
    private final List<OuvinteDeDevolucao> ouvintes = new CopyOnWriteArrayList<>();
    private final List<FalhaDeOuvinte> falhas = new CopyOnWriteArrayList<>();

    public void registrar(OuvinteDeDevolucao o) { ouvintes.add(o); }

    public void remover(OuvinteDeDevolucao o) { ouvintes.remove(o); }

    public void publicar(EventoDevolucao evento) {
        for (OuvinteDeDevolucao o : ouvintes) {
            try {
                o.aoDevolver(evento);
            } catch (RuntimeException erro) {
                falhas.add(new FalhaDeOuvinte(o.getClass().getSimpleName(), evento.idDevolucao(), erro.getMessage()));
            }
        }
    }

    public List<FalhaDeOuvinte> falhas() { return List.copyOf(falhas); }

    public int quantidadeDeOuvintes() { return ouvintes.size(); }
}
