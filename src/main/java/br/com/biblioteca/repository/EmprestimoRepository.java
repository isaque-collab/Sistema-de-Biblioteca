package br.com.biblioteca.repository;

import br.com.biblioteca.enums.StatusEmprestimo;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.util.ConexaoFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmprestimoRepository {

    public void salvar(Emprestimo emprestimo) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            salvar(emprestimo, conn);
        }
    }

    public void salvar(Emprestimo emprestimo, Connection conn) throws SQLException {
        String sql = "INSERT INTO emprestimo (usuario_id, livro_id, data_emprestimo, " +
                "data_prevista_devolucao, data_devolucao, status) VALUES (?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1, emprestimo.getUsuarioId());
            stmt.setInt(2, emprestimo.getLivroId());
            stmt.setDate(3, Date.valueOf(emprestimo.getDataEmprestimo()));
            stmt.setDate(4, Date.valueOf(emprestimo.getDataPrevistaDevolucao()));
            stmt.setDate(5, emprestimo.getDataDevolucao() != null ? Date.valueOf(emprestimo.getDataDevolucao()) : null);
            stmt.setString(6, emprestimo.getStatus().name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()) {
                    emprestimo.setId(rs.getInt(1));
                }
            }
        }
    }

    public Optional<Emprestimo> buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            return buscarPorId(id, conn);
        }
    }

    public Optional<Emprestimo> buscarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM emprestimo WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Emprestimo> buscarTodos() throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            return buscarTodos(conn);
        }
    }

    public List<Emprestimo> buscarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM emprestimo ORDER BY data_emprestimo DESC";
        List<Emprestimo> emprestimos = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while (rs.next()) {
                emprestimos.add(mapear(rs));
            }
        }
        return emprestimos;
    }

    public List<Emprestimo> buscarPorStatus(StatusEmprestimo statusEmprestimo) throws SQLException {
        try(Connection conn = ConexaoFactory.getInstance().getConexao()){
            return buscarPorStatus(statusEmprestimo, conn);
        }
    }

    public List<Emprestimo> buscarPorStatus(StatusEmprestimo statusEmprestimo, Connection conn) throws SQLException{
        String sql = "SELECT * FROM emprestimo WHERE status = ?";
        List<Emprestimo> emprestimos = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, statusEmprestimo.name());

            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    emprestimos.add(mapear(rs));
                }
            }
        }
        return emprestimos;
    }

    public boolean registrarDevolucao(int emprestimoId, LocalDate dataDevolucao, Connection conn) throws SQLException {
        String sql = "UPDATE emprestimo SET status = 'DEVOLVIDO', data_devolucao = ? " +
                "WHERE id = ? AND status = 'ATIVO'";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, Date.valueOf(dataDevolucao));
            stmt.setInt(2, emprestimoId);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        }
    }

    public boolean existeEmprestimoAtivo(int usuarioId, int livroId) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            return existeEmprestimoAtivo(usuarioId, livroId, conn);
        }
    }

    public boolean existeEmprestimoAtivo(int usuarioId, int livroId, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM emprestimo WHERE usuario_id = ? AND livro_id = ? AND status = ? LIMIT 1";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, livroId);
            stmt.setString(3, StatusEmprestimo.ATIVO.name());
            try (ResultSet rs = stmt.executeQuery()){
                return rs.next();
            }
        }
    }


    private Emprestimo mapear(ResultSet rs) throws SQLException {
        Date dataDevolucao = rs.getDate("data_devolucao");
        return Emprestimo.builder()
                .id(rs.getInt("id"))
                .usuarioId(rs.getInt("usuario_id"))
                .livroId(rs.getInt("livro_id"))
                .dataEmprestimo(rs.getDate("data_emprestimo").toLocalDate())
                .dataPrevistaDevolucao(rs.getDate("data_prevista_devolucao").toLocalDate())
                .dataDevolucao(dataDevolucao != null ? dataDevolucao.toLocalDate() : null)
                .status(StatusEmprestimo.valueOf(rs.getString("status")))
                .build();
    }
}
