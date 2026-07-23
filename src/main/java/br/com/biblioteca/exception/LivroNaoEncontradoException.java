package br.com.biblioteca.exception;

public class LivroNaoEncontradoException extends RegistroNaoEncontradoException {
    public LivroNaoEncontradoException(int id) {
        super("Livro não encontrado, id: " + id);
    }
}
