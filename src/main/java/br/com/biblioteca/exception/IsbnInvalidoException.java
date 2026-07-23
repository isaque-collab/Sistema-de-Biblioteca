package br.com.biblioteca.exception;

public class IsbnInvalidoException extends ValidacaoException {
    public IsbnInvalidoException(String isbn) {
        super("ISBN inválido: " + isbn);
    }
}
