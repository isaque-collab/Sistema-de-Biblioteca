package br.com.biblioteca.exception;

public class LivroNaoEncontradoException extends PersistenciaException {
    public LivroNaoEncontradoException(int id) {
        super("Livro não encontrado, id: " + id);
    }
}
