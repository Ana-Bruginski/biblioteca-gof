package br.pucpr.ib2b.cenarios;

import br.pucpr.ib2b.consulta.ConfigInstalacao;
import br.pucpr.ib2b.consulta.ConsultaAcervo;
import br.pucpr.ib2b.consulta.ConsultaAcervoBasica;
import br.pucpr.ib2b.consulta.Criterio;
import br.pucpr.ib2b.consulta.DecoradorDeCache;
import br.pucpr.ib2b.consulta.DecoradorDeMedicao;
import br.pucpr.ib2b.consulta.MetadadosLivro;
import br.pucpr.ib2b.consulta.MontadorDeConsulta;
import br.pucpr.ib2b.consulta.RegistroDeMetricas;
import br.pucpr.ib2b.dominio.Situacao;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Requisito 9 - Servico de consulta com recursos opcionais - Decorator. */
class R09_ConsultaDecoratorTest extends CenarioBase {

    @Test
    void R09_segundaConsultaUsaCacheEAindaAssimGeraMedicao() {
        RegistroDeMetricas metricas = new RegistroDeMetricas();
        DecoradorDeCache cache = new DecoradorDeCache(new ConsultaAcervoBasica(sistema.fabrica()),
                Duration.ofMinutes(5), relogio);
        ConsultaAcervo consulta = new DecoradorDeMedicao(cache, metricas);     // Medicao(Cache(Basica))

        List<MetadadosLivro> primeira = consulta.buscarMetadados(new Criterio("dom"));
        List<MetadadosLivro> segunda = consulta.buscarMetadados(new Criterio(" Dom "));

        assertEquals(primeira, segunda);
        assertEquals(1, cache.faltas(), "so a primeira foi ao acervo");
        assertEquals(1, cache.acertos(), "a segunda veio do cache");
        assertEquals(2, metricas.quantidade(), "as duas foram medidas");
    }

    @Test
    void R09_resultadoAntigoDoCacheNaoAutorizaReservaDeLivroQueDeixouDeEstarLivre() {
        List<MetadadosLivro> antes = sistema.acervo().buscarNoCatalogo("Dom Casmurro");   // L1 entra no cache
        emprestar("L1", ANA);                                                             // L1 deixa de estar LIVRE
        List<MetadadosLivro> depois = sistema.acervo().buscarNoCatalogo("Dom Casmurro");  // cache ainda tem L1

        assertEquals(antes, depois);
        assertFalse(sistema.emprestimos().reservarLivro("L1", BIA).sucesso(), "a reserva olha o Livro atual");
        assertEquals(Situacao.EMPRESTADO, situacao("L1").situacao());
        assertEquals(2, MetadadosLivro.class.getRecordComponents().length, "cache guarda so id e titulo");
    }

    @Test
    void R09_cadeiaEMontadaConformeAInstalacaoComMedicaoPorFora() {
        ConsultaAcervo basica = new ConsultaAcervoBasica(sistema.fabrica());
        RegistroDeMetricas m = new RegistroDeMetricas();

        assertInstanceOf(DecoradorDeMedicao.class,
                MontadorDeConsulta.montar(basica, new ConfigInstalacao(true, true, Duration.ofMinutes(1)), m, relogio));
        assertInstanceOf(DecoradorDeMedicao.class,
                MontadorDeConsulta.montar(basica, new ConfigInstalacao(true, false, Duration.ZERO), m, relogio));
        assertSame(basica,
                MontadorDeConsulta.montar(basica, new ConfigInstalacao(false, false, Duration.ZERO), m, relogio));
    }

    @Test
    void R09_cacheExpiradoVoltaAConsultarOAcervo() {
        DecoradorDeCache cache = new DecoradorDeCache(new ConsultaAcervoBasica(sistema.fabrica()),
                Duration.ofMinutes(5), relogio);
        cache.buscarMetadados(new Criterio("vidas"));
        relogio.avancarSegundos(301);
        cache.buscarMetadados(new Criterio("vidas"));
        assertEquals(2, cache.faltas());
    }
}
