package br.com.biblioteca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoFactory {
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "biblioteca";
    private static final String PASSWORD = "biblioteca123";

    private static ConexaoFactory instance;

    private ConexaoFactory(){

    }

    public static ConexaoFactory getInstance(){
        if(instance == null){
            instance = new ConexaoFactory();
        }
        return instance;
    }

    public Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}
