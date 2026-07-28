package br.com.biblioteca.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estratégia que calcula a multa de forma linear,
 * multiplicando o valor diário pela quantidade de dias de atraso.
 */

public class MultaLinearStrategy implements CalculadoraMulta{

    private final BigDecimal valorDiario;

    public MultaLinearStrategy(BigDecimal valorDiario) {
        if (valorDiario == null || valorDiario.signum() <= 0){
            throw new IllegalArgumentException("Valor diário não pode ser nulo ou negativo.\"");
        }
        this.valorDiario = valorDiario;
    }

    /**
     * Calcula a multa considerando uma cobrança fixa por dia de atraso.
     *
     * @param diasAtraso quantidade de dias em atraso
     * @return valor da multa
     */
    @Override
    public BigDecimal calcular(long diasAtraso) {
        if (diasAtraso <= 0){
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valorDiario
                .multiply(BigDecimal.valueOf(diasAtraso))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
