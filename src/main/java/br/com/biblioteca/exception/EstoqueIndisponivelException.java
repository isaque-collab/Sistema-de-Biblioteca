package br.com.biblioteca.exception;

public class EstoqueIndisponivelException extends RuntimeException {
  public EstoqueIndisponivelException(String message) {
    super(message);
  }
}
