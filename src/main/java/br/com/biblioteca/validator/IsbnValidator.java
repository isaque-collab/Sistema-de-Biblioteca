package br.com.biblioteca.validator;

import br.com.biblioteca.exception.IsbnInvalidoException;

public final class IsbnValidator {

    private static final int TAMANHO_ISBN = 13;

    private IsbnValidator() {

    }

    public static String validar(String isbn) {
        String limpo = limpar(isbn);

        if (limpo.length() != TAMANHO_ISBN || limpo.matches("\\d+")) {
            throw new IsbnInvalidoException(isbn);
        }

        if (!digitoVerificadorValido(limpo)){
            throw new IsbnInvalidoException(isbn);
        }

        return limpo;
    }

    private static String limpar(String isbn) {
        if (isbn == null){
            return "";
        }

        return isbn.replaceAll("\\D", "");
    }

    private static boolean digitoVerificadorValido(String isbn) {
        int soma = 0;

        for (int i = 0; i<12; i++){
            int digito = isbn.charAt(i) - '0';

            if (i%2 == 0){
                soma += digito;
            }else {
                soma += digito*3;
            }
        }
        int resto = soma % 10;
        int digitoEsperado = (resto == 0) ? 0:10 - resto;

        int digitoInformado = isbn.charAt(12) -  '0';
        return digitoEsperado == digitoInformado;
    }
}
