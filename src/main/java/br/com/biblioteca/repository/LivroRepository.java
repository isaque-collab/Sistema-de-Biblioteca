package br.com.biblioteca.repository;

import br.com.biblioteca.model.Livro;
import br.com.biblioteca.util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepository {

    public void salvar(Livro livro) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            salvar(livro, conn);
        }
    }

    public void salvar(Livro livro, Connection conn) throws SQLException {
        String sql = "INSERT INTO livro (titulo, isbn, autor_id, categoria_id, quantidade_total, quantidade_disponivel) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getIsbn());
            stmt.setInt(3, livro.getAutorId());
            stmt.setInt(4, livro.getCategoriaId());
            stmt.setInt(5, livro.getQuantidadeTotal());
            stmt.setInt(6, livro.getQuantidadeDisponivel());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    livro.setId(rs.getInt(1));
                }
            }
        }
    }

    public Optional<Livro> buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarPorId(id, conn);
        }
    }

    public Optional<Livro> buscarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM livro WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Livro> buscarPorIsbn(String isbn) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarPorIsbn(isbn, conn);
        }
    }

    public Optional<Livro> buscarPorIsbn(String isbn, Connection conn) throws SQLException {
        String sql = "SELECT * FROM livro WHERE isbn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Livro> buscarTodos() throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarTodos(conn);
        }
    }

    public List<Livro> buscarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM livro";
        List<Livro> livros = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                livros.add(mapear(rs));
            }
        }
        return livros;
    }

    public void atualizar(Livro livro) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            atualizar(livro, conn);
        }
    }

    public void atualizar(Livro livro, Connection conn) throws SQLException {
        String sql = "UPDATE livro SET titulo = ?, isbn = ?, autor_id = ?, categoria_id = ?, " +
                "quantidade_total = ?, quantidade_disponivel = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getIsbn());
            stmt.setInt(3, livro.getAutorId());
            stmt.setInt(4, livro.getCategoriaId());
            stmt.setInt(5, livro.getQuantidadeTotal());
            stmt.setInt(6, livro.getQuantidadeDisponivel());
            stmt.setInt(7, livro.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            deletar(id, conn);
        }
    }

    public void deletar(int id, Connection conn) throws SQLException {
        String sql = "DELETE FROM livro WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Livro mapear(ResultSet rs) throws SQLException {
        return Livro.builder()
                .id(rs.getInt("id"))
                .titulo(rs.getString("titulo"))
                .isbn(rs.getString("isbn"))
                .autorId(rs.getInt("autor_id"))
                .categoriaId(rs.getInt("categoria_id"))
                .quantidadeTotal(rs.getInt("quantidade_total"))
                .quantidadeDisponivel(rs.getInt("quantidade_disponivel"))
                .build();
    }


}
