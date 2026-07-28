package br.com.biblioteca.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MultaLinearStrategyTest {

    private final CalculadoraMulta calculadora = new MultaLinearStrategy(new BigDecimal("2.0"));

    @Test
    void diasAtrasoZeroRetornaZero(){
        assertEquals(new BigDecimal("0.00"), calculadora.calcular(0));
    }

    @Test
    void diasAtrasoNegativoRetornaZero(){
        assertEquals(new BigDecimal("0.00"), calculadora.calcular(-5));
    }

    @Test
    void cobraDesdeOPrimeiroDia(){
        assertEquals(new BigDecimal("2.00"), calculadora.calcular(1));
    }

    @Test
    void multiplicaDiasPeloValorDiario(){
        assertEquals(new BigDecimal("10.00"), calculadora.calcular(5));
    }

    @Test
    void valorDiarioNuloLancaIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new MultaLinearStrategy(null));
    }

    @Test
    void valorDiarioNegativoLancaIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new MultaLinearStrategy(new BigDecimal("-1.00")));
    }
}
