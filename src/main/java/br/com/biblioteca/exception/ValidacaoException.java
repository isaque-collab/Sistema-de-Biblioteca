package br.com.biblioteca.exception;

public abstract class ValidacaoException extends RuntimeException {

    protected ValidacaoException(String mensagem) {
        super(mensagem);
    }

    protected ValidacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}