package br.pucpr.ib2b.persistencia.mysql;

import br.pucpr.ib2b.dominio.Devolucao;
import br.pucpr.ib2b.dominio.Emprestimo;
import br.pucpr.ib2b.dominio.Reserva;
import br.pucpr.ib2b.persistencia.ErroDePersistencia;
import br.pucpr.ib2b.persistencia.RepositorioMovimentacao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioMovimentacaoMySQL implements RepositorioMovimentacao {
    private final UnidadeDeTrabalhoMySQL uow;

    RepositorioMovimentacaoMySQL(UnidadeDeTrabalhoMySQL uow) {
        this.uow = uow;
    }

    public UnidadeDeTrabalhoMySQL unidade() { return uow; }

    @Override
    public void registrarReserva(Reserva r) {
        executar("INSERT INTO reserva (id, id_livro, cpf, reservada_em) VALUES (?, ?, ?, ?)",
                r.id(), r.idLivro(), r.cpfEstudante(), Timestamp.valueOf(r.reservadaEm()));
    }

    @Override
    public Optional<Reserva> buscarReservaAtiva(String idLivro) {
        List<Reserva> lista = reservas("SELECT id, id_livro, cpf, reservada_em FROM reserva WHERE id_livro = ?", idLivro);
        return lista.stream().findFirst();
    }

    @Override
    public List<Reserva> listarReservasDe(String cpf) {
        return reservas("SELECT id, id_livro, cpf, reservada_em FROM reserva WHERE cpf = ?", cpf);
    }

    @Override
    public void removerReserva(String idLivro) {
        executar("DELETE FROM reserva WHERE id_livro = ?", idLivro);
    }

    @Override
    public void registrarEmprestimo(Emprestimo e) {
        executar("INSERT INTO emprestimo (id, id_livro, cpf, emprestado_em, vencimento) VALUES (?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE vencimento = VALUES(vencimento)",
                e.id(), e.idLivro(), e.cpfEstudante(), Date.valueOf(e.emprestadoEm()), Date.valueOf(e.vencimento()));
    }

    @Override
    public Optional<Emprestimo> buscarEmprestimoAtivo(String idLivro) {
        String sql = "SELECT id, id_livro, cpf, emprestado_em, vencimento FROM emprestimo WHERE id_livro = ?";
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql)) {
            ps.setString(1, idLivro);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Emprestimo(rs.getString("id"), rs.getString("id_livro"), rs.getString("cpf"),
                        rs.getDate("emprestado_em").toLocalDate(), rs.getDate("vencimento").toLocalDate()));
            }
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao buscar emprestimo", e);
        }
    }

    @Override
    public void removerEmprestimoAtivo(String idLivro) {
        executar("DELETE FROM emprestimo WHERE id_livro = ?", idLivro);
    }

    @Override
    public void registrarDevolucao(Devolucao d) {
        executar("INSERT INTO devolucao (id, id_livro, cpf, vencimento, devolvido_em) VALUES (?, ?, ?, ?, ?)",
                d.id(), d.idLivro(), d.cpfEstudante(), Date.valueOf(d.vencimento()), Date.valueOf(d.devolvidoEm()));
    }

    @Override
    public List<Devolucao> listarDevolucoes() {
        String sql = "SELECT id, id_livro, cpf, vencimento, devolvido_em FROM devolucao ORDER BY devolvido_em";
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Devolucao> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(new Devolucao(rs.getString("id"), rs.getString("id_livro"), rs.getString("cpf"),
                        rs.getDate("vencimento").toLocalDate(), rs.getDate("devolvido_em").toLocalDate()));
            }
            return lista;
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao listar devolucoes", e);
        }
    }

    private List<Reserva> reservas(String sql, String parametro) {
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql)) {
            ps.setString(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                List<Reserva> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(new Reserva(rs.getString("id"), rs.getString("id_livro"), rs.getString("cpf"),
                            rs.getTimestamp("reservada_em").toLocalDateTime()));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao buscar reservas", e);
        }
    }

    private void executar(String sql, Object... valores) {
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) {
                ps.setObject(i + 1, valores[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro de gravacao em Movimentacao", e);
        }
    }
}
