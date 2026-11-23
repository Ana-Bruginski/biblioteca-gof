package br.pucpr.ib2b.persistencia.mysql;

import br.pucpr.ib2b.persistencia.ErroDePersistencia;
import br.pucpr.ib2b.persistencia.UnidadeDeTrabalho;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConcreteProduct (familia relacional): uma conexao com autoCommit desligado.
 * Todos os repositorios criados com esta unidade usam a MESMA conexao, logo um unico COMMIT.
 * A conexao so e aberta no primeiro uso.
 */
public class UnidadeDeTrabalhoMySQL implements UnidadeDeTrabalho {
    private final ParametrosMySQL parametros;
    private Connection conexao;

    UnidadeDeTrabalhoMySQL(ParametrosMySQL parametros) {
        this.parametros = parametros;
    }

    Connection conexao() {
        try {
            if (conexao == null) {
                conexao = DriverManager.getConnection(parametros.url(), parametros.usuario(), parametros.senha());
                conexao.setAutoCommit(false);
            }
            return conexao;
        } catch (SQLException e) {
            throw new ErroDePersistencia("nao foi possivel conectar ao MySQL principal", e);
        }
    }

    boolean usa(ParametrosMySQL outros) { return parametros == outros; }

    @Override
    public void confirmar() {
        if (conexao == null) {
            return;
        }
        try {
            conexao.commit();
        } catch (SQLException e) {
            throw new ErroDePersistencia("falha no COMMIT", e);
        } finally {
            fechar();
        }
    }

    @Override
    public void desfazer() {
        if (conexao == null) {
            return;
        }
        try {
            conexao.rollback();
        } catch (SQLException e) {
            throw new ErroDePersistencia("falha no ROLLBACK", e);
        } finally {
            fechar();
        }
    }

    private void fechar() {
        try {
            conexao.close();
        } catch (SQLException ignorada) {
            // conexao ja inutilizavel
        }
        conexao = null;
    }
}
