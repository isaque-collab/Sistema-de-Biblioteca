package br.com.biblioteca.exception;

public class EmprestimoNaoEncontradoException extends RegistroNaoEncontradoException {
    public EmprestimoNaoEncontradoException(int id) {
        super("Empréstimo não encontrado, id: " + id);
    }
}
