package br.com.biblioteca.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(int id) {
        super("Usuário não encontrado, id: " + id);
    }
}
