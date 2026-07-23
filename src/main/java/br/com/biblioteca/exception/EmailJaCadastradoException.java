package br.com.biblioteca.exception;

public class EmailJaCadastradoException extends PersistenciaException {
    public EmailJaCadastradoException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}
