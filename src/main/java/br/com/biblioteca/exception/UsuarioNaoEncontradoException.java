package br.com.biblioteca.exception;

public class UsuarioNaoEncontradoException extends RegistroNaoEncontradoException {
    public UsuarioNaoEncontradoException(int id) {
        super("Usuário não encontrado, id: " + id);
    }
}
