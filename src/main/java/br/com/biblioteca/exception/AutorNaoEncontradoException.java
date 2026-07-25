package br.com.biblioteca.exception;

public class AutorNaoEncontradoException extends RegistroNaoEncontradoException {
    public AutorNaoEncontradoException(int id) {
        super("Autor não encontrado, id: "+ id);
    }
}
