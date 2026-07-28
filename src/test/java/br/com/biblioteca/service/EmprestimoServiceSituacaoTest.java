package br.com.biblioteca.service;

import br.com.biblioteca.enums.SituacaoEmprestimo;
import br.com.biblioteca.enums.StatusEmprestimo;
import br.com.biblioteca.model.Emprestimo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmprestimoServiceSituacaoTest {

    private final EmprestimoService service = new EmprestimoService(null,null,null, null);

    private Emprestimo criarEmprestimo(StatusEmprestimo status, LocalDate dataPrevistaDevolucao){
        return Emprestimo.builder()
                .id(1)
                .usuarioId(1)
                .livroId(1)
                .dataEmprestimo(LocalDate.of(2026, 1, 1))
                .dataPrevistaDevolucao(dataPrevistaDevolucao)
                .status(status)
                .build();
    }

    @Test
    void devolvidoRetornaSituacaoDevolvidoIndependenteDaData(){
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.DEVOLVIDO, LocalDate.of(2020, 1, 1));

        SituacaoEmprestimo situacao = service.determinarSituacao(emprestimo, LocalDate.of(2026,7,27));

        assertEquals(SituacaoEmprestimo.DEVOLVIDO, situacao);
    }

    @Test
    void ativoComDataFuturaRetornaAtivo(){
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.ATIVO, LocalDate.of(2026, 8, 1));

        SituacaoEmprestimo situacao = service.determinarSituacao(emprestimo, LocalDate.of(2026,7,27));

        assertEquals(SituacaoEmprestimo.ATIVO, situacao);
    }

    @Test
    void ativoComDataDeVencimentoIgualAHojeRetornaAtivo(){
        LocalDate hoje = LocalDate.of(2026, 7, 27);
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.ATIVO, hoje);

        SituacaoEmprestimo situacao = service.determinarSituacao(emprestimo, hoje);

        assertEquals(SituacaoEmprestimo.ATIVO, situacao);
    }

    @Test
    void ativoComDataPassadaRetornaAtrasado(){
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.ATIVO, LocalDate.of(2026, 7, 2));

        SituacaoEmprestimo situacao = service.determinarSituacao(emprestimo, LocalDate.of(2026,7,27));

        assertEquals(SituacaoEmprestimo.ATRASADO, situacao);
    }

    @Test
    void emprestimoNuloLancaIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> service.determinarSituacao(null,LocalDate.of(2026, 7, 27)));
    }

    @Test
    void dataReferenciaNulaLancaIllegalArgumentException(){
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.ATIVO, LocalDate.of(2026, 8, 1));

        assertThrows(IllegalArgumentException.class, () -> service.determinarSituacao(emprestimo,null));
    }

    private static class CalculadoraMultaFake implements br.com.biblioteca.strategy.CalculadoraMulta{
        long diasRecebidos;

        @Override
        public java.math.BigDecimal calcular(long diasAtraso) {
            this.diasRecebidos = diasAtraso;
            return java.math.BigDecimal.valueOf(diasAtraso);
        }
    }

    @Test
    void calcularMultaPassaDiasAtrasoCorretosParaEstrategia(){
        CalculadoraMultaFake fake = new CalculadoraMultaFake();
        EmprestimoService serviceComFake = new EmprestimoService(null, null, null, fake);
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.ATIVO, LocalDate.of(2026, 7, 20));

        serviceComFake.calcularMulta(emprestimo, LocalDate.of(2026, 7, 27));

        assertEquals(7, fake.diasRecebidos);
    }

    @Test
    void calcularMultaComEmprestimoNuloLancaIllegalArgumentException(){
        EmprestimoService serviceComFake = new EmprestimoService(null, null, null, new CalculadoraMultaFake());

        assertThrows(IllegalArgumentException.class, () -> serviceComFake.calcularMulta(null, LocalDate.of(2026, 7, 27)));
    }

    @Test
    void calcularMultaComDataReferenciaNulaLancaIllegalArgumentException(){
        EmprestimoService serviceComFake = new EmprestimoService(null, null, null, new CalculadoraMultaFake());
        Emprestimo emprestimo = criarEmprestimo(StatusEmprestimo.ATIVO, LocalDate.of(2026, 7, 20));

        assertThrows(IllegalArgumentException.class, () -> serviceComFake.calcularMulta(emprestimo, null));
    }
}
