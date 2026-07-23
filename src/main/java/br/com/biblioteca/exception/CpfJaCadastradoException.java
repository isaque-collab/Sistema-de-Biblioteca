package br.com.biblioteca.exception;

public class CpfJaCadastradoException extends RegistroDuplicadoException {
    public CpfJaCadastradoException(String cpf) {
        super("CPF já cadastrado: " + cpf);
    }
}
