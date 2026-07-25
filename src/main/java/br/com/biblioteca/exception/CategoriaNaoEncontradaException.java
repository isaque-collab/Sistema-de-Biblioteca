package br.com.biblioteca.exception;

public class CategoriaNaoEncontradaException extends RegistroNaoEncontradoException {
    public CategoriaNaoEncontradaException(int id) {
        super("Categoria não encontrada, id: "+id);
    }
}
