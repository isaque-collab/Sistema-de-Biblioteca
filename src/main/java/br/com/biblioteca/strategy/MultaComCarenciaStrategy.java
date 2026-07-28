package br.com.biblioteca.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MultaComCarenciaStrategy implements CalculadoraMulta {

    private final BigDecimal valorDiario;
    private final int diasCarencia;

    public MultaComCarenciaStrategy(BigDecimal valorDiario, int diasCarencia) {
        if (valorDiario == null || valorDiario.signum() <= 0){
            throw new IllegalArgumentException("Valor diário não pode ser nulo, negativo ou igual a 0");
        }
        if (diasCarencia < 0){
            throw new IllegalArgumentException("Dias de carência não podem ser negativos");
        }
        this.valorDiario = valorDiario;
        this.diasCarencia = diasCarencia;
    }


    @Override
    public BigDecimal calcular(long diasAtraso) {
        long diasCobraveis = diasAtraso - diasCarencia;
        if (diasCobraveis <= 0){
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valorDiario
                .multiply(BigDecimal.valueOf(diasCobraveis))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
