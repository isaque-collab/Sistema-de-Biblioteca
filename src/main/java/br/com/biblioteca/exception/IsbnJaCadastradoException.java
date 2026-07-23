package br.com.biblioteca.exception;

public class IsbnJaCadastradoException extends RegistroDuplicadoException {
    public IsbnJaCadastradoException(String isbn) {
        super("ISBN já cadastrado: " + isbn);
    }
}
