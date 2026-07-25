package br.com.biblioteca.exception;

public class CampoObrigatorioException extends RuntimeException {
  public CampoObrigatorioException(String message) {
    super(message);
  }
}
