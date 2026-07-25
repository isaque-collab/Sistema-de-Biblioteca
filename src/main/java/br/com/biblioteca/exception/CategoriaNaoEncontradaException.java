package br.com.biblioteca.exception;

public class CategoriaNaoEncontradaException extends RuntimeException {
  public CategoriaNaoEncontradaException(String message) {
    super(message);
  }
}
