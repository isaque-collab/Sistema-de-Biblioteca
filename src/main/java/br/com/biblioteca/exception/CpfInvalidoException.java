package br.com.biblioteca.exception;

public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException(String cpf) {
        super("CPF inválido: " + cpf);
    }
}
