package br.pucpr.ib2b.persistencia.mysql;

import br.pucpr.ib2b.dominio.Livro;
import br.pucpr.ib2b.dominio.Situacao;
import br.pucpr.ib2b.persistencia.ErroDePersistencia;
import br.pucpr.ib2b.persistencia.RepositorioLivro;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** O estado do Livro e gravado como texto (coluna situacao) e reconstituido com Situacao.estado(). */
public class RepositorioLivroMySQL implements RepositorioLivro {
    private final UnidadeDeTrabalhoMySQL uow;

    RepositorioLivroMySQL(UnidadeDeTrabalhoMySQL uow) {
        this.uow = uow;
    }

    public UnidadeDeTrabalhoMySQL unidade() { return uow; }

    @Override
    public Optional<Livro> buscar(String id) {
        String sql = "SELECT id, titulo, situacao, vencimento, num_renovacoes, cpf_reservante FROM livro WHERE id = ?";
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(ler(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao buscar livro", e);
        }
    }

    @Override
    public List<Livro> listar() {
        String sql = "SELECT id, titulo, situacao, vencimento, num_renovacoes, cpf_reservante FROM livro ORDER BY id";
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Livro> livros = new ArrayList<>();
            while (rs.next()) {
                livros.add(ler(rs));
            }
            return livros;
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao listar livros", e);
        }
    }

    @Override
    public void salvar(Livro l) {
        String sql = "INSERT INTO livro (id, titulo, situacao, vencimento, num_renovacoes, cpf_reservante) "
                + "VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE titulo = VALUES(titulo), "
                + "situacao = VALUES(situacao), vencimento = VALUES(vencimento), "
                + "num_renovacoes = VALUES(num_renovacoes), cpf_reservante = VALUES(cpf_reservante)";
        try (PreparedStatement ps = uow.conexao().prepareStatement(sql)) {
            ps.setString(1, l.getId());
            ps.setString(2, l.getTitulo());
            ps.setString(3, l.getSituacao().name());
            ps.setDate(4, l.getVencimento() == null ? null : Date.valueOf(l.getVencimento()));
            ps.setInt(5, l.getNumRenovacoes());
            ps.setString(6, l.getCpfReservante());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao salvar livro", e);
        }
    }

    @Override
    public void remover(String id) {
        try (PreparedStatement ps = uow.conexao().prepareStatement("DELETE FROM livro WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErroDePersistencia("erro ao remover livro", e);
        }
    }

    private Livro ler(ResultSet rs) throws SQLException {
        Date venc = rs.getDate("vencimento");
        return Livro.reconstituir(rs.getString("id"), rs.getString("titulo"),
                Situacao.valueOf(rs.getString("situacao")), venc == null ? null : venc.toLocalDate(),
                rs.getInt("num_renovacoes"), rs.getString("cpf_reservante"));
    }
}
