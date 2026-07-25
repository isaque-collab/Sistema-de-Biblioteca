package br.com.biblioteca.repository;

import br.com.biblioteca.model.Autor;
import br.com.biblioteca.util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutorRepository {

    public void salvar(Autor autor) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            salvar(autor, conn);
        }
    }

    public void salvar(Autor autor, Connection conn) throws SQLException {
        String sql = "INSERT INTO autor (nome, nacionalidade) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, autor.getNome());
            stmt.setString(2, autor.getNacionalidade());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    autor.setId(rs.getInt(1));
                }
            }
        }
    }

    public Optional<Autor> buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarPorId(id, conn);
        }
    }

    public Optional<Autor> buscarPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM autor WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Autor> buscarPorNome(String nome) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarPorNome(nome, conn);
        }
    }

    public List<Autor> buscarPorNome(String nome, Connection conn) throws SQLException {
        String sql = "SELECT * FROM autor WHERE nome LIKE ?";
        List<Autor> autores = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) autores.add(mapear(rs));
            }
        }
        return autores;
    }

    public List<Autor> buscarTodos() throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            return buscarTodos(conn);
        }
    }

    public List<Autor> buscarTodos(Connection conn) throws SQLException {
        String sql = "SELECT * FROM autor";
        List<Autor> autores = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                autores.add(mapear(rs));
            }
        }
        return autores;
    }

    public void atualizar(Autor autor) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            atualizar(autor, conn);
        }
    }

    public void atualizar(Autor autor, Connection conn) throws SQLException {
        String sql = "UPDATE autor SET nome = ?, nacionalidade = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, autor.getNome());
            stmt.setString(2, autor.getNacionalidade());
            stmt.setInt(3, autor.getId());
            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new RuntimeException("Autor não encontrado.");
            }
        }
    }

    public void deletar(int id) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao()) {
            deletar(id, conn);
        }
    }

    public void deletar(int id, Connection conn) throws SQLException {
        String sql = "DELETE FROM autor WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new RuntimeException("Autor não encontrado.");
            }
        }
    }

    private Autor mapear(ResultSet rs) throws SQLException {
        return Autor.builder()
                .id(rs.getInt("id"))
                .nome(rs.getString("nome"))
                .nacionalidade(rs.getString("nacionalidade"))
                .build();
    }
}
