package br.com.biblioteca.exception;

public class CategoriaJaCadastradaException extends RegistroDuplicadoException {
    public CategoriaJaCadastradaException(String nome) {
        super("Categoria já cadastrada: " + nome);
    }
}
