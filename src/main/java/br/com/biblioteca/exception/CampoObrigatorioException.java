package br.com.biblioteca.exception;

public class CampoObrigatorioException extends ValidacaoException {
    public CampoObrigatorioException(String campo) {
        super("Campo obrigatório não informado: " + campo);
    }
}
