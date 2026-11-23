package br.pucpr.ib2b.persistencia.mysql;

import br.pucpr.ib2b.dominio.Atendente;
import br.pucpr.ib2b.dominio.Estudante;
import br.pucpr.ib2b.dominio.Pessoa;
import br.pucpr.ib2b.persistencia.ErroDePersistencia;
import br.pucpr.ib2b.persistencia.RepositorioRelacionamento;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RepositorioRelacionamentoMySQL implements RepositorioRelacionamento {
    private final UnidadeDeTrabalhoMySQL uow;

    RepositorioRelacionamentoMySQL(UnidadeDeTrabalhoMySQL uow) {
        this.uow = uow;
    }

    public UnidadeDeTrabalhoMySQL unidade() { return uow; }

    @Override
    public Optional<Pessoa> buscarPessoa(String cpf) {
        try (PreparedStatement ps = uow.conexao().prepareStatement(
                "SELECT cpf, nome, sobrenome, email FROM pessoa WHERE cpf = ?")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new Pessoa(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao buscar pessoa", e);
        }
    }

    @Override
    public void salvarPessoa(Pessoa p) {
        executar("INSERT INTO pessoa (cpf, nome, sobrenome, email) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                        + "nome = VALUES(nome), sobrenome = VALUES(sobrenome), email = VALUES(email)",
                p.getCpf(), p.getNome(), p.getSobrenome(), p.getEmail());
    }

    @Override
    public void removerPessoa(String cpf) { executar("DELETE FROM pessoa WHERE cpf = ?", cpf); }

    @Override
    public Optional<Estudante> buscarEstudante(String cpf) {
        try (PreparedStatement ps = uow.conexao().prepareStatement(
                "SELECT cpf, id_estudante, matricula FROM estudante WHERE cpf = ?")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new Estudante(rs.getString(1), rs.getString(2), rs.getString(3)))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao buscar estudante", e);
        }
    }

    @Override
    public void salvarEstudante(Estudante e) {
        executar("INSERT INTO estudante (cpf, id_estudante, matricula) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE matricula = VALUES(matricula)", e.getCpf(), e.getId(), e.getMatricula());
    }

    @Override
    public void removerEstudante(String cpf) { executar("DELETE FROM estudante WHERE cpf = ?", cpf); }

    @Override
    public Optional<Atendente> buscarAtendente(String cpf) {
        try (PreparedStatement ps = uow.conexao().prepareStatement(
                "SELECT cpf, id_atendente, setor FROM atendente WHERE cpf = ?")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new Atendente(rs.getString(1), rs.getString(2), rs.getString(3)))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao buscar atendente", e);
        }
    }

    @Override
    public void salvarAtendente(Atendente a) {
        executar("INSERT INTO atendente (cpf, id_atendente, setor) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE setor = VALUES(setor)", a.getCpf(), a.getId(), a.getSetor());
    }

    @Override
    public void removerAtendente(String cpf) { executar("DELETE FROM atendente WHERE cpf = ?", cpf); }

    private void executar(String sql, Object... valores) {
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro de gravacao em Relacionamento", e);
        }
    }
}
