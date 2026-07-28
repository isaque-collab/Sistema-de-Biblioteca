package br.com.biblioteca.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultaComCarenciaStrategyTest {

    private final CalculadoraMulta calculadora = new MultaComCarenciaStrategy(new BigDecimal("2.00"), 3);

    @Test
    void diasAtrasoZeroRetornaZero(){
        assertEquals(new BigDecimal("0.00"), calculadora.calcular(0));
    }

    @Test
    void dentroDaCarenciaNaoCobra(){
        assertEquals(new BigDecimal("0.00"), calculadora.calcular(3));
    }

    @Test
    void exatamenteUmDiaAposCarenciaCobraUmDia(){
        assertEquals(new BigDecimal("2.00"), calculadora.calcular(4));
    }

    @Test
    void carenciaZeroEquivaleALinear(){
        CalculadoraMulta semCarencia = new MultaComCarenciaStrategy(new BigDecimal("2.00"), 0);
        assertEquals(new BigDecimal("2.00"), semCarencia.calcular(1));
    }

    @Test
    void diasCarenciaNegativoLancaIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new MultaComCarenciaStrategy(new BigDecimal("2.00"), -1));
    }

    @Test
    void valorDiarioNuloLancaIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> new MultaComCarenciaStrategy(null, 3));
    }
}
