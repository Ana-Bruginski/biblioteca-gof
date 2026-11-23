package br.pucpr.ib2b.consulta;

import java.time.Duration;
import java.util.List;

/** ConcreteDecorator: mede o tempo de qualquer consulta, inclusive as atendidas pelo cache. */
public class DecoradorDeMedicao extends DecoradorDeConsulta {
    private final RegistroDeMetricas metricas;

    public DecoradorDeMedicao(ConsultaAcervo interno, RegistroDeMetricas metricas) {
        super(interno);
        this.metricas = metricas;
    }

    @Override
    public List<MetadadosLivro> buscarMetadados(Criterio criterio) {
        long inicio = System.nanoTime();
        try {
            return interno.buscarMetadados(criterio);
        } finally {
            try {
                metricas.registrar(Duration.ofNanos(System.nanoTime() - inicio));
            } catch (RuntimeException falhaNaMetrica) {
                // registrar metrica nunca derruba a consulta
            }
        }
    }
}
