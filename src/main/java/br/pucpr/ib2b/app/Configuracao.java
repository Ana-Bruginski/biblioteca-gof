package br.pucpr.ib2b.app;

import br.pucpr.ib2b.consulta.ConfigInstalacao;
import br.pucpr.ib2b.financeiro.banco.CodigoBanco;
import br.pucpr.ib2b.persistencia.Perfil;
import br.pucpr.ib2b.persistencia.mysql.ParametrosMySQL;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/** Le app.properties; qualquer chave pode ser sobrescrita com -Dchave=valor. */
public class
Configuracao {
    private final Properties props = new Properties();

    private Configuracao() { }

    public static Configuracao carregar() {
        Configuracao c = new Configuracao();
        try (InputStream in = Configuracao.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                c.props.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.getProperties().forEach((k, v) -> {
            if (c.props.containsKey(k.toString())) {
                c.props.setProperty(k.toString(), v.toString());
            }
        });
        return c;
    }

    /** Configuracao usada pelos testes: familia em memoria. */
    public static Configuracao paraTeste() {
        Configuracao c = carregar();
        c.props.setProperty("perfil", "TESTE");
        return c;
    }

    public Configuracao com(String chave, String valor) {
        props.setProperty(chave, valor);
        return this;
    }

    public Perfil perfil() { return Perfil.valueOf(props.getProperty("perfil").trim().toUpperCase()); }

    public ParametrosMySQL mysql() {
        return new ParametrosMySQL(props.getProperty("mysql.url"), props.getProperty("mysql.usuario"),
                props.getProperty("mysql.senha"));
    }

    public CodigoBanco banco() { return CodigoBanco.valueOf(props.getProperty("banco", "A").trim().toUpperCase()); }

    public String politicaInicial() { return props.getProperty("multa.politica", "ORIGINAL").trim().toUpperCase(); }

    public ConfigInstalacao consulta() {
        return new ConfigInstalacao(
                Boolean.parseBoolean(props.getProperty("consulta.medicao", "true")),
                Boolean.parseBoolean(props.getProperty("consulta.cache", "true")),
                Duration.ofSeconds(Long.parseLong(props.getProperty("consulta.cache.ttlSegundos", "300"))));
    }
}
