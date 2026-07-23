package br.com.biblioteca.exception;

public class CpfJaCadastradoException extends PersistenciaException {
    public CpfJaCadastradoException(String cpf) {
        super("CPF já cadastrado: " + cpf);
    }
}
