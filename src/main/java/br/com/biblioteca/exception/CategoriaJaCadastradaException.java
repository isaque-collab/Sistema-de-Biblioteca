package br.com.biblioteca.exception;

public class CategoriaJaCadastradaException extends RuntimeException {
  public CategoriaJaCadastradaException(String message) {
    super(message);
  }
}
