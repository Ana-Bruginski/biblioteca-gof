package br.pucpr.ib2b.solicitacao;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro idempotente das execucoes. reservarExecucao() e atomica (como uma chave unica):
 * com duas submissoes simultaneas do mesmo id, so uma recebe true.
 */
public class RegistroDeSolicitacoes {
    private final Set<UUID> reservadas = ConcurrentHashMap.newKeySet();
    private final Map<UUID, ResultadoSolicitacao> resultados = new ConcurrentHashMap<>();

    public boolean reservarExecucao(UUID id) { return reservadas.add(id); }

    public void concluir(UUID id, ResultadoSolicitacao r) { resultados.put(id, r); }

    public Optional<ResultadoSolicitacao> resultadoDe(UUID id) { return Optional.ofNullable(resultados.get(id)); }

    /** Falha tecnica: libera o id para uma nova tentativa. */
    public void liberar(UUID id) { reservadas.remove(id); }
}
