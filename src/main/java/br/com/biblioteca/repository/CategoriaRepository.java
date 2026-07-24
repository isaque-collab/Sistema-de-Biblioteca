package br.com.biblioteca.repository;

import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaRepository {

    public void salvar(Categoria categoria) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            salvar(categoria, conn);
        }
    }

    public void salvar(Categoria categoria, Connection conn) throws SQLException {
        String sql = "INSERT INTO categoria (nome) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNome());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
            }
        }
    }

    public Optional<Categoria> buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarPorId(id, conn);
        }
    }

    public Optional<Categoria> buscarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM categoria WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Categoria> buscarPorNome(String nome) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarPorNome(nome, conn);
        }
    }

    public Optional<Categoria> buscarPorNome(String nome, Connection conn) throws SQLException {
        String sql = "SELECT * FROM categoria WHERE nome = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Categoria> buscarTodas() throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarTodas(conn);
        }
    }

    public List<Categoria> buscarTodas(Connection conn) throws SQLException {
        String sql = "SELECT * FROM categoria ORDER BY nome";
        List<Categoria> categorias = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categorias.add(mapear(rs));
            }
        }
        return categorias;
    }

    public void atualizar(Categoria categoria) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            atualizar(categoria, conn);
        }
    }

    public void atualizar(Categoria categoria, Connection conn) throws SQLException {
        String sql = "UPDATE categoria SET nome = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNome());
            stmt.setInt(2, categoria.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            deletar(id, conn);
        }
    }

    public void deletar(int id, Connection conn) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return Categoria.builder()
                .id(rs.getInt("id"))
                .nome(rs.getString("nome"))
                .build();
    }


}
