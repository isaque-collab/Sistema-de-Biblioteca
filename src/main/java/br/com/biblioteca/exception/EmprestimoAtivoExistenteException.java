package br.com.biblioteca.exception;

public class EmprestimoAtivoExistenteException extends RuntimeException {
    public EmprestimoAtivoExistenteException(int usuarioId, int livroId) {
        super("Usuário id: " + usuarioId + " já possui empréstimo ativo do livro id: " + livroId + ".");
    }
}
