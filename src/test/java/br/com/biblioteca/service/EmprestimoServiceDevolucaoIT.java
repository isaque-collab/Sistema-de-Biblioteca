package br.com.biblioteca.service;

import br.com.biblioteca.enums.StatusEmprestimo;
import br.com.biblioteca.exception.EmprestimoJaDevolvidoException;
import br.com.biblioteca.exception.EmprestimoNaoEncontradoException;
import br.com.biblioteca.model.*;
import br.com.biblioteca.repository.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;
import br.com.biblioteca.util.ConexaoFactory;
import br.com.biblioteca.util.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmprestimoServiceDevolucaoIT {

    private final AutorRepository autorRepository = new AutorRepository();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();
    private final LivroRepository livroRepository = new LivroRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EmprestimoRepository emprestimoRepository = new EmprestimoRepository();

    private final AutorService autorService = new AutorService(autorRepository);
    private final CategoriaService categoriaService = new CategoriaService(categoriaRepository);
    private final LivroService livroService = new LivroService(livroRepository);
    private final UsuarioService usuarioService = new UsuarioService(usuarioRepository);
    private final EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository, livroRepository,
            usuarioRepository, new MultaComCarenciaStrategy(new BigDecimal("2.00"), 3));

    private final TestDataFactory testDataFactory =
            new TestDataFactory(autorService, categoriaService,livroService,usuarioService);

    @Test
    void devolverComSucessoAtualizaStatusDataDevolucaoEAumentaEstoque() throws SQLException {
        Livro livro = testDataFactory.criarLivroComEstoque(1);
        Usuario usuario = testDataFactory.criarUsuario();
        Emprestimo emprestimo = emprestimoService.emprestar(
                usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

        Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());

        assertEquals(StatusEmprestimo.DEVOLVIDO, devolvido.getStatus());
        assertEquals(LocalDate.now(), devolvido.getDataDevolucao());
        assertEquals(1, quantidadeDisponivelNoBanco(livro.getId()));
    }

    @Test
    void devolverComEmprestimoInexistenteLancaEmprestimoNaoEncontradoException(){
        int idInexistente = Integer.MAX_VALUE;

        assertThrows(EmprestimoNaoEncontradoException.class, () -> emprestimoService.devolver(idInexistente));
    }

    @Test
    void devolverDuasVezesSequencialmenteSegundaChamadaLancaEmprestimoJaDevolvidoException() throws SQLException {
        Livro livro = testDataFactory.criarLivroComEstoque(1);
        Usuario usuario = testDataFactory.criarUsuario();
        Emprestimo emprestimo = emprestimoService.emprestar(
                usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

                emprestimoService.devolver(emprestimo.getId());

                assertThrows(EmprestimoJaDevolvidoException.class,
                        () -> emprestimoService.devolver(emprestimo.getId()));

                assertEquals(1, quantidadeDisponivelNoBanco(livro.getId()));
    }

    private int quantidadeDisponivelNoBanco(int livroId) throws SQLException {
        try (Connection conn = ConexaoFactory.getInstance().getConexao();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT quantidade_disponivel FROM livro WHERE id = ?")){
            stmt.setInt(1, livroId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }

        }
    }
}
