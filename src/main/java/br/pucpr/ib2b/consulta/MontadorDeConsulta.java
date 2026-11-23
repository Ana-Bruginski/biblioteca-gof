package br.pucpr.ib2b.consulta;

import br.pucpr.ib2b.infra.Relogio;

/**
 * Monta a cadeia de decoradores conforme a instalacao.
 * Ordem: medicao POR FORA do cache -> Medicao(Cache(Basica)). Assim, uma consulta atendida
 * pelo cache tambem e medida. Se o cache ficasse por fora, o acerto nao passaria pela medicao.
 */
public final class MontadorDeConsulta {
    private MontadorDeConsulta() { }

    public static ConsultaAcervo montar(ConsultaAcervo basica, ConfigInstalacao cfg, RegistroDeMetricas metricas,
                                        Relogio relogio) {
        ConsultaAcervo c = basica;
        if (cfg.cache()) {
            c = new DecoradorDeCache(c, cfg.ttlCache(), relogio);
        }
        if (cfg.medicao()) {
            c = new DecoradorDeMedicao(c, metricas);
        }
        return c;
    }
}
