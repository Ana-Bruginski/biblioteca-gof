package br.pucpr.ib2b.consulta;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Destino das medicoes de tempo de resposta. */
public class RegistroDeMetricas {
    private final List<Duration> medicoes = new CopyOnWriteArrayList<>();

    public void registrar(Duration duracao) { medicoes.add(duracao); }

    public int quantidade() { return medicoes.size(); }

    public List<Duration> medicoes() { return List.copyOf(medicoes); }
}
