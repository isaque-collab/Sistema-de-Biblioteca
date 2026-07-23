package br.com.biblioteca.exception;

public class IsbnJaCadastradoException extends RuntimeException {
    public IsbnJaCadastradoException(String isbn) {
        super("ISBN já cadastrado: " + isbn);
    }
}
