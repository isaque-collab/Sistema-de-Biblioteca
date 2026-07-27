package br.com.biblioteca.exception;

public class EstoqueIndisponivelException extends RuntimeException {
    public EstoqueIndisponivelException(int livroId) {
        super("Não há exemplares disponíveis para o livro id: " + livroId);
    }
}
