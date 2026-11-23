package br.pucpr.ib2b.consulta;

import br.pucpr.ib2b.infra.Relogio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcreteDecorator: guarda metadados (id e titulo) por um tempo de validade (TTL).
 * Se o cache falhar, a consulta segue pelo componente interno.
 */
public class DecoradorDeCache extends DecoradorDeConsulta {
    private record Entrada(List<MetadadosLivro> valor, LocalDateTime expiraEm) { }

    private final Map<Criterio, Entrada> cache = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final Relogio relogio;
    private final AtomicInteger acertos = new AtomicInteger();
    private final AtomicInteger faltas = new AtomicInteger();

    public DecoradorDeCache(ConsultaAcervo interno, Duration ttl, Relogio relogio) {
        super(interno);
        this.ttl = ttl;
        this.relogio = relogio;
    }

    @Override
    public List<MetadadosLivro> buscarMetadados(Criterio criterio) {
        try {
            Entrada e = cache.get(criterio);
            if (e != null && relogio.agora().isBefore(e.expiraEm())) {
                acertos.incrementAndGet();
                return e.valor();
            }
        } catch (RuntimeException falhaNoCache) {
            return interno.buscarMetadados(criterio);
        }
        faltas.incrementAndGet();
        List<MetadadosLivro> resultado = interno.buscarMetadados(criterio);
        cache.put(criterio, new Entrada(List.copyOf(resultado), relogio.agora().plus(ttl)));
        return resultado;
    }

    public void invalidar() { cache.clear(); }

    public int acertos() { return acertos.get(); }

    public int faltas() { return faltas.get(); }
}
