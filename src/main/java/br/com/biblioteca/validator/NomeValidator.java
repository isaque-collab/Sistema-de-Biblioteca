package br.com.biblioteca.validator;

import br.com.biblioteca.exception.NomeInvalidoException;

public final class NomeValidator {

    private NomeValidator() {

    }

    public static void validar(String nome, int tamanhoMaximo) {
        if (nome == null) {
            throw new NomeInvalidoException("O nome não pode ser nulo.");
        }

        nome = nome.trim();

        if (nome.isEmpty()){
            throw new NomeInvalidoException("O nome não pode estar vazio.");
        }

        if (nome.length() > tamanhoMaximo) {
            throw new NomeInvalidoException("O nome deve ter no máximo "+ tamanhoMaximo + " caracteres.");
        }
    }
}
