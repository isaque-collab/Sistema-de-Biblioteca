package br.com.biblioteca.repository;

import br.com.biblioteca.dto.relatorios.CategoriaEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.LivroEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.UsuarioEmprestimoResumo;
import br.com.biblioteca.util.ConexaoFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatorioRepository {

    public List<LivroEmprestimoResumo> livrosMaisEmprestados() throws SQLException{
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            return livrosMaisEmprestados(conn);
        }
    }

    public List<LivroEmprestimoResumo> livrosMaisEmprestados(Connection conn) throws SQLException{
        String sql = "SELECT l.id, l.titulo, COUNT(e.id) AS quantidade_emprestimos " +
                "FROM livro l " +
                "INNER JOIN emprestimo e ON e.livro_id = l.id " +
                "GROUP BY l.id, l.titulo " +
                "ORDER BY quantidade_emprestimos DESC, l.id ASC";

        List<LivroEmprestimoResumo> resultado = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                resultado.add(new LivroEmprestimoResumo(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getLong("quantidade_emprestimos")));
            }
        }
        return resultado;
    }

    public List<UsuarioEmprestimoResumo> usuariosComMaisEmprestimos() throws SQLException{
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            return usuariosComMaisEmprestimos(conn);
        }
    }

    public List<UsuarioEmprestimoResumo> usuariosComMaisEmprestimos(Connection conn) throws SQLException{
        String sql = "SELECT u.id, u.nome, COUNT(e.id) AS quantidade_emprestimos " +
                "FROM usuario u " +
                "INNER JOIN emprestimo e ON e.usuario_id = u.id " +
                "GROUP BY u.id, u.nome " +
                "ORDER BY quantidade_emprestimos DESC, u.id ASC";

        List<UsuarioEmprestimoResumo> resultado = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                resultado.add(new UsuarioEmprestimoResumo(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getLong("quantidade_emprestimos")));
            }
        }
        return resultado;
    }

    public List<CategoriaEmprestimoResumo> emprestimosPorCategoria() throws SQLException{
        try (Connection conn = ConexaoFactory.getInstance().getConexao()){
            return emprestimosPorCategoria(conn);
        }
    }

    public List<CategoriaEmprestimoResumo> emprestimosPorCategoria(Connection conn) throws SQLException {
        String sql = "SELECT c.id, c.nome, COUNT(e.id) AS quantidade_emprestimos " +
                "FROM categoria c " +
                "LEFT JOIN livro l ON l.categoria_id = c.id " +
                "LEFT JOIN emprestimo e ON e.livro_id = l.id " +
                "GROUP BY c.id, c.nome " +
                "ORDER BY quantidade_emprestimos DESC, c.id ASC";

        List<CategoriaEmprestimoResumo> resultado = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                resultado.add(new CategoriaEmprestimoResumo(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getLong("quantidade_emprestimos")));
            }
        }
        return resultado;
    }

}
