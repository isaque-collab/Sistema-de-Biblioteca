package br.com.biblioteca.exception;

public abstract class RegistroNaoEncontradoException extends RuntimeException {
    protected RegistroNaoEncontradoException(String message) {
        super(message);
    }
}
