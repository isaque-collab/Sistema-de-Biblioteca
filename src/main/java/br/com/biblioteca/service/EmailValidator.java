package br.com.biblioteca.service;

import br.com.biblioteca.exception.EmailInvalidoException;

import java.util.regex.Pattern;

public final class EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final int TAMANHO_MAXIMO_TOTAL = 254;
    private static final int TAMANHO_MAXIMO_LOCAL = 64;

    private EmailValidator() {

    }

    public static String validar(String email) {
        if (email == null) {
            throw new EmailInvalidoException(email);
        }

        String normalizado = email.trim().toLowerCase();
        if (normalizado.isEmpty()
                || normalizado.length() > TAMANHO_MAXIMO_TOTAL
                || !EMAIL_PATTERN.matcher(normalizado).matches()) {

            throw new EmailInvalidoException(email);
        }

        String parteLocal = normalizado.substring(0, normalizado.indexOf('@'));
        if (parteLocal.length() > TAMANHO_MAXIMO_LOCAL) {
            throw new EmailInvalidoException(email);
        }
        return normalizado;
    }
}
