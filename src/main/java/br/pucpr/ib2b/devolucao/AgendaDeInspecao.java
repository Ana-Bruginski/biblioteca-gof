package br.pucpr.ib2b.devolucao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Livros devolvidos aguardando a decisao da inspecao. */
public class AgendaDeInspecao {
    private final Set<String> pendentes = new LinkedHashSet<>();

    public synchronized void agendar(String idLivro) { pendentes.add(idLivro); }

    public synchronized void concluir(String idLivro) { pendentes.remove(idLivro); }

    public synchronized boolean estaPendente(String idLivro) { return pendentes.contains(idLivro); }

    public synchronized List<String> pendentes() { return new ArrayList<>(pendentes); }
}
