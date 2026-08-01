package br.com.biblioteca.service;

import br.com.biblioteca.model.*;
import br.com.biblioteca.repository.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;
import br.com.biblioteca.util.ConexaoFactory;
import br.com.biblioteca.util.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmprestimoServiceMultaPorAtrasoIT {

    private final AutorRepository autorRepository = new AutorRepository();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();
    private final LivroRepository livroRepository = new LivroRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EmprestimoRepository emprestimoRepository = new EmprestimoRepository();

    private final AutorService autorService = new AutorService(autorRepository);
    private final CategoriaService categoriaService = new CategoriaService(categoriaRepository);
    private final LivroService livroService = new LivroService(livroRepository);
    private final UsuarioService usuarioService = new UsuarioService(usuarioRepository);
    private final EmprestimoService emprestimoService = new EmprestimoService(
            emprestimoRepository, livroRepository, usuarioRepository,
            new MultaComCarenciaStrategy(new BigDecimal("2.00"), 3));

    private final TestDataFactory testDataFactory =
            new TestDataFactory(autorService, categoriaService, livroService, usuarioService);

    @Test
    void devolucaoComDezDiasDeAtrasoCalculaMultaDescontandoACarencia() throws SQLException {
        Emprestimo emprestimo = criarEmprestimo();

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(20), LocalDate.now().minusDays(10));

        Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());
        BigDecimal multa = emprestimoService.calcularMulta(devolvido, devolvido.getDataDevolucao());

        assertEquals(new BigDecimal("14.00"), multa);
    }

    @Test
    void devoluvaoDentroDaCarenciaNaoGeraMulta() throws SQLException {
        Emprestimo emprestimo = criarEmprestimo();

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(12), LocalDate.now().minusDays(2));

        Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());
        BigDecimal multa = emprestimoService.calcularMulta(devolvido, devolvido.getDataDevolucao());

        assertEquals(new BigDecimal("0.00"), multa);
    }

    @Test
    void devolucaoNoPrazoNaoGeraMulta() {
        Livro livro = testDataFactory.criarLivroComEstoque(1);
        Usuario usuario = testDataFactory.criarUsuario();
        Emprestimo emprestimo = emprestimoService.emprestar(
                usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

        Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());
        BigDecimal multa = emprestimoService.calcularMulta(devolvido, devolvido.getDataDevolucao());

        assertEquals(new BigDecimal("0.00"), multa);
    }

    private void backdatarEmprestimo(int emprestimoId, LocalDate novaDataEmprestimo, LocalDate novaDataPrevistaDevolucao) throws SQLException {
        String sql = "UPDATE emprestimo SET data_emprestimo = ?, data_prevista_devolucao = ? WHERE id = ?";
        try (Connection conn = ConexaoFactory.getInstance().getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(novaDataEmprestimo));
            stmt.setDate(2, Date.valueOf(novaDataPrevistaDevolucao));
            stmt.setInt(3, emprestimoId);
            stmt.executeUpdate();
        }
    }

    private Emprestimo criarEmprestimo() {
        Livro livro = testDataFactory.criarLivroComEstoque(1);
        Usuario usuario = testDataFactory.criarUsuario();
        return emprestimoService.emprestar(usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));
    }
}