package br.com.biblioteca.exception;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String messagem, Throwable causa) {
        super(messagem, causa);
    }
}
