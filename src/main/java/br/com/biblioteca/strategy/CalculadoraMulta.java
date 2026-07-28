package br.com.biblioteca.strategy;

import java.math.BigDecimal;

public interface CalculadoraMulta {
    BigDecimal calcular(long diasAtraso);
}
