package br.com.biblioteca.exception;

public class CpfInvalidoException extends ValidacaoException {
    public CpfInvalidoException(String cpf) {
        super("CPF inválido: " + cpf);
    }
}
