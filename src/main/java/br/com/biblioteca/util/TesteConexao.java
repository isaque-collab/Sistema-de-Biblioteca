package br.com.biblioteca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TesteConexao {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/biblioteca";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Conecxão feita com sucesso!");
        }catch (SQLException e){
            System.out.println("Erro ao conectar com o banco de dados! "+e.getMessage());
        }
    }
}
