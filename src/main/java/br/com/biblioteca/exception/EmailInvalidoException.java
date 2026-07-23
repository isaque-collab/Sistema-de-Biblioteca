package br.com.biblioteca.exception;

public class EmailInvalidoException extends ValidacaoException {
    public EmailInvalidoException(String email) {
        super("E-mail inválido: " + email);
    }
}
