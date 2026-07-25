package br.com.biblioteca.exception;

public class AutorNaoEncontradoException extends RuntimeException {
  public AutorNaoEncontradoException(String message) {
    super(message);
  }
}
