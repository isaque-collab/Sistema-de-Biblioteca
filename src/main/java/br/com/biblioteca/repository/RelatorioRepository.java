package br.com.biblioteca.repository;

import br.com.biblioteca.dto.relatorios.LivroEmprestimoResumo;
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
        String sql = "SELECT l.id, l.titulo, COUNT(e.id) AS quantidade " +
                "FROM livro l " +
                "INNER JOIN emprestimo e ON e.livro_id = l.id " +
                "GROUP BY l.id, l.titulo " +
                "ORDER BY quantidade DESC, l.id ASC";

        List<LivroEmprestimoResumo> resultado = new ArrayList<>();
        try(PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                resultado.add(new LivroEmprestimoResumo(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getLong("quantidade")));
            }
        }
        return resultado;
    }

}
