package br.com.biblioteca.service;

import br.com.biblioteca.exception.EmprestimoAtivoExistenteException;
import br.com.biblioteca.model.*;
import br.com.biblioteca.repository.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;
import br.com.biblioteca.util.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoServiceRegraAtivoUnicoIT {

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
            new TestDataFactory(autorService,categoriaService,livroService,usuarioService);

    private final LocalDate dataPrevista = LocalDate.now().plusDays(7);

    @Test
    void segundoEmprestimoDoMesmoLivroPeloMesmoUsuarioLancaEmprestimoAtivoExistenteException(){
        Livro livro = testDataFactory.criarLivroComEstoque(2);
        Usuario usuario = testDataFactory.criarUsuario();

        emprestimoService.emprestar(usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

        assertThrows(EmprestimoAtivoExistenteException.class, () -> emprestimoService.emprestar(usuario.getId(),
                livro.getId(), dataPrevista));
    }

    @Test
    void aposDevolverUsuarioPodeEmprestarOMesmoLivroDeNovo(){
        Livro livro = testDataFactory.criarLivroComEstoque(1);
        Usuario usuario = testDataFactory.criarUsuario();

        Emprestimo primeiro = emprestimoService.emprestar(usuario.getId(), livro.getId(), dataPrevista);
        emprestimoService.devolver(primeiro.getId());

        Emprestimo segundo = assertDoesNotThrow(() -> emprestimoService.emprestar(usuario.getId(), livro.getId(), dataPrevista));

        assertNotEquals(primeiro.getId(), segundo.getId());
    }

    @Test
    void emprestimoAtivoDeUmLivroNaoImpedeEmprestimoDeOutroLivro(){
        Livro livroA = testDataFactory.criarLivroComEstoque(1);
        Livro livroB = testDataFactory.criarLivroComEstoque(1);
        Usuario usuario = testDataFactory.criarUsuario();

        emprestimoService.emprestar(usuario.getId(), livroA.getId(), LocalDate.now().plusDays(7));

        assertDoesNotThrow(() -> emprestimoService.emprestar(usuario.getId(), livroB.getId(), dataPrevista));
    }

    @Test
    void usuariosDiferentesPodemEmprestarOMesmoLivro(){
        Livro livro = testDataFactory.criarLivroComEstoque(2);

        Usuario usuario1 = testDataFactory.criarUsuario();
        Usuario usuario2 = testDataFactory.criarUsuario();

        assertDoesNotThrow(() ->
                emprestimoService.emprestar(usuario1.getId(), livro.getId(), LocalDate.now().plusDays(7)));

        assertDoesNotThrow(() ->
                emprestimoService.emprestar(usuario2.getId(), livro.getId(), LocalDate.now().plusDays(7)));
    }
}
