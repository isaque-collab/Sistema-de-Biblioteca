package br.com.biblioteca.exception;

public class IsbnJaCadastradoException extends PersistenciaException {
    public IsbnJaCadastradoException(String isbn) {
        super("ISBN já cadastrado: " + isbn);
    }
}
