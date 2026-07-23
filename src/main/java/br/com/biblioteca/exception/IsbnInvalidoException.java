package br.com.biblioteca.exception;

public class IsbnInvalidoException extends RuntimeException {
    public IsbnInvalidoException(String isbn) {
        super("ISBN inálido: " + isbn);
    }
}
