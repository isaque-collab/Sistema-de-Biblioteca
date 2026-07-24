package br.com.biblioteca.validator;

import br.com.biblioteca.exception.IsbnInvalidoException;

public final class IsbnValidator {
    private static final int TAMANHO_ISBN10 = 10;
    private static final int TAMANHO_ISBN13 = 13;

    private IsbnValidator() {

    }

    public static String validar(String isbn) {
        String limpo = limpar(isbn);

        if (limpo.length() == TAMANHO_ISBN10) {
            if (!isbn10Valido(limpo)){
                throw new IsbnInvalidoException(isbn);
            }
            return limpo.substring(0,9) + Character.toUpperCase(limpo.charAt(9));
        }

        if (limpo.length() == TAMANHO_ISBN13) {
            if (!isbn13Valido(limpo)){
                throw new IsbnInvalidoException(isbn);
            }
            return limpo;
        }
        throw new IsbnInvalidoException(isbn);
    }

    private static String limpar(String isbn) {
        if (isbn == null){
            return "";
        }

        return isbn.trim().replaceAll("[^0-9Xx]", "");
    }

    private static boolean isbn10Valido(String isbn) {
        int soma = 0;
        for (int i = 0; i < 9; i++){
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)){
                return false;
            }
            soma += (c - '0') * (10 - i);
        }

        char ultimo = Character.toUpperCase(isbn.charAt(9));
        if (ultimo == 'X'){
            soma += 10;
        } else if (Character.isDigit(ultimo)) {
            soma += (ultimo - '0');
        }else {
            return false;
        }
        return soma % 11 == 0;
    }

    private static boolean isbn13Valido(String isbn) {
        if (!isbn.matches("\\d{13}")){
            return false;
        }
        int soma = 0;
        for (int i = 0; i < 13; i++){
            int peso = (i % 2 == 0) ? 1: 3;
            soma += (isbn.charAt(i) - '0') * peso;
        }
        return soma % 10 == 0;
    }
}
