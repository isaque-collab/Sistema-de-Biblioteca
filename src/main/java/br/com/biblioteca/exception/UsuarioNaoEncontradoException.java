package br.com.biblioteca.exception;

public class UsuarioNaoEncontradoException extends PersistenciaException {
    public UsuarioNaoEncontradoException(int id) {
        super("Usuário não encontrado, id: " + id);
    }
}
