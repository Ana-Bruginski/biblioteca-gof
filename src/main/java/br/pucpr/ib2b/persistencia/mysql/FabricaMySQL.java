package br.pucpr.ib2b.persistencia.mysql;

import br.pucpr.ib2b.persistencia.ErroDePersistencia;
import br.pucpr.ib2b.persistencia.FabricaDePersistencia;
import br.pucpr.ib2b.persistencia.FamiliaIncompativelException;
import br.pucpr.ib2b.persistencia.RepositorioLivro;
import br.pucpr.ib2b.persistencia.RepositorioMovimentacao;
import br.pucpr.ib2b.persistencia.RepositorioRelacionamento;
import br.pucpr.ib2b.persistencia.UnidadeDeTrabalho;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract Factory - ConcreteFactory da familia relacional (perfil PRODUCAO).
 * Escreve somente no MySQL principal; o Oracle e replica mantida pela infraestrutura.
 */
public class FabricaMySQL implements FabricaDePersistencia {
    private final ParametrosMySQL parametros;

    public FabricaMySQL(ParametrosMySQL parametros) {
        this.parametros = parametros;
    }

    @Override
    public UnidadeDeTrabalho criarUnidadeDeTrabalho() {
        return new UnidadeDeTrabalhoMySQL(parametros);
    }

    @Override
    public RepositorioLivro criarRepositorioLivro(UnidadeDeTrabalho uow) {
        return new RepositorioLivroMySQL(daFamilia(uow));
    }

    @Override
    public RepositorioMovimentacao criarRepositorioMovimentacao(UnidadeDeTrabalho uow) {
        return new RepositorioMovimentacaoMySQL(daFamilia(uow));
    }

    @Override
    public RepositorioRelacionamento criarRepositorioRelacionamento(UnidadeDeTrabalho uow) {
        return new RepositorioRelacionamentoMySQL(daFamilia(uow));
    }

    @Override
    public String familia() { return "mysql"; }

    /** Cria as tabelas (schema.sql) se ainda nao existirem. */
    @Override
    public void inicializar() {
        try (InputStream in = getClass().getResourceAsStream("/schema.sql");
             Connection c = DriverManager.getConnection(parametros.url(), parametros.usuario(), parametros.senha());
             Statement st = c.createStatement()) {
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String comando : sql.split(";")) {
                if (!comando.isBlank()) {
                    st.execute(comando);
                }
            }
        } catch (SQLException | IOException e) {
            throw new ErroDePersistencia("nao foi possivel preparar o esquema no MySQL", e);
        }
    }

    private UnidadeDeTrabalhoMySQL daFamilia(UnidadeDeTrabalho uow) {
        if (uow instanceof UnidadeDeTrabalhoMySQL m && m.usa(parametros)) {
            return m;
        }
        throw new FamiliaIncompativelException("FabricaMySQL", uow);
    }
}
