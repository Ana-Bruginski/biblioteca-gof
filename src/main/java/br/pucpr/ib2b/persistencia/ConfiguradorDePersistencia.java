package br.pucpr.ib2b.persistencia;

import br.pucpr.ib2b.persistencia.memoria.BaseEmMemoria;
import br.pucpr.ib2b.persistencia.memoria.FabricaEmMemoria;
import br.pucpr.ib2b.persistencia.mysql.FabricaMySQL;
import br.pucpr.ib2b.persistencia.mysql.ParametrosMySQL;

/**
 * Unico ponto em que a familia de persistencia e escolhida, pelo perfil de execucao.
 * Oracle nao aparece aqui: continua replica do MySQL e nao recebe escritas da aplicacao.
 */
public final class ConfiguradorDePersistencia {
    private ConfiguradorDePersistencia() { }

    public static FabricaDePersistencia fabricaPara(Perfil perfil, ParametrosMySQL parametros) {
        if (perfil == null) {
            throw new IllegalStateException("perfil de execucao ausente: a aplicacao nao escolhe familia por omissao");
        }
        return switch (perfil) {
            case TESTE -> new FabricaEmMemoria(new BaseEmMemoria());
            case PRODUCAO -> new FabricaMySQL(parametros);
        };
    }
}
