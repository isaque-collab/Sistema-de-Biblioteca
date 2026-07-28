package br.com.biblioteca.exception;

public class EmprestimoJaDevolvidoException extends RuntimeException {
    public EmprestimoJaDevolvidoException(int emprestimoId) {
        super("Empréstimo id " + emprestimoId + " já foi devolvido.");
    }
}
