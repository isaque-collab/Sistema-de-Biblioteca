package br.com.biblioteca.service;

import br.com.biblioteca.exception.CpfInvalidoException;

public final class CpfValidator {

    private static final int TAMANHO_CPF = 11;

    private CpfValidator() {

    }

    public static String validar(String cpf) {
        String limpo = limpar(cpf);

        if (limpo.length() != TAMANHO_CPF || !limpo.matches("\\d+")) {
            throw new CpfInvalidoException(cpf);
        }
        if (isSequenciaRepetida(limpo)){
            throw new CpfInvalidoException(cpf);
        }

        if (!digitosVerificadoresValidos(limpo)){
            throw new CpfInvalidoException(cpf);
        }
        return limpo;
    }

    private static String limpar(String cpf) {
        if (cpf == null){
            return "";
        }

        return cpf.replaceAll("\\D", "");
    }

    private static boolean isSequenciaRepetida(String cpf) {
        char primeiro = cpf.charAt(0);
        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }

    private static boolean digitosVerificadoresValidos(String cpf) {
        int[] digitos = new int[cpf.length()];
        for (int i = 0; i < cpf.length(); i++) {
            digitos[i] = cpf.charAt(i) - '0';
        }

        int primeiroDigito = calcularDigitoVerificador(digitos, 9, 10);
        if (primeiroDigito != digitos[9]) {
            return false;
        }

        int segundoDigito = calcularDigitoVerificador(digitos, 10, 11);
        return segundoDigito == digitos[10];
    }

    private static int calcularDigitoVerificador(int[] digitos, int quantidade, int pesoInicial){
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < quantidade; i++) {
            soma += digitos[i] * peso;
            peso--;
        }
        int resto = soma%11;
        return (resto<2) ? 0:11-resto;
    }



}
