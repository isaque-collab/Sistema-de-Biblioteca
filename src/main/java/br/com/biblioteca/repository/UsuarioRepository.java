package br.com.biblioteca.repository;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {
    public void salvar(Usuario usuario) throws SQLException{
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            salvar(usuario, conn);
        }
    }

    public void salvar(Usuario usuario, Connection conn) throws SQLException{
        String sql = "INSERT INTO usuario (nome, cpf, email) VALUES (?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()){
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }

            }

        }
    }

    public Optional<Usuario> buscarPorId(int id) throws SQLException{
        try(Connection conn = ConexaoFactory.getInstance().getConexao()){
            return buscarPorId(id, conn);
        }
    }

    public Optional<Usuario> buscarPorId(int id, Connection conn) throws SQLException{
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }

        }
    }

    public Optional<Usuario> buscarPorCpf(String cpf) throws SQLException{
        try(Connection conn = ConexaoFactory.getInstance().getConexao()){
            return buscarPorCpf(cpf, conn);
        }
    }
    public Optional<Usuario> buscarPorCpf(String cpf, Connection conn) throws SQLException{
        String sql = "SELECT * FROM usuario WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try(ResultSet rs = stmt.executeQuery()){
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) throws SQLException{
        try(Connection conn = ConexaoFactory.getInstance().getConexao()){
            return buscarPorEmail(email, conn);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email, Connection conn) throws SQLException{
        String sql = "SELECT * FROM usuario WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try(ResultSet rs = stmt.executeQuery()){
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Usuario> buscarTodos() throws SQLException{
        try(Connection conn = ConexaoFactory.getInstance().getConexao()){
            return buscarTodos(conn);
        }
    }

    public List<Usuario> buscarTodos(Connection conn) throws SQLException{
        String sql = "SELECT * FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    public void atualizar(Usuario usuario) throws SQLException{
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            atualizar(usuario, conn);
        }
    }

    public void atualizar(Usuario usuario, Connection conn) throws SQLException{
        String sql = "UPDATE  usuario SET nome = ?, cpf = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setInt(4, usuario.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException{
        try(Connection conn = ConexaoFactory.getInstance().getConexao()){
            deletar(id, conn);
        }
    }

    public void deletar(int id, Connection conn) throws SQLException{
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException{
        return Usuario.builder()
                .id(rs.getInt("id"))
                .nome(rs.getString("nome"))
                .cpf(rs.getString("cpf"))
                .email(rs.getString("email"))
                .build();
    }
}
