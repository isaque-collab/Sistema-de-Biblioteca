package br.com.biblioteca.service;

import br.com.biblioteca.dto.relatorios.CategoriaEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.LivroEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.UsuarioEmprestimoResumo;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;
import br.com.biblioteca.util.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RelatorioServiceIT {

    private final AutorRepository autorRepository = new AutorRepository();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();
    private final LivroRepository livroRepository = new LivroRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EmprestimoRepository emprestimoRepository = new EmprestimoRepository();
    private final RelatorioRepository relatorioRepository = new RelatorioRepository();

    private final AutorService autorService = new AutorService(autorRepository);
    private final CategoriaService categoriaService = new CategoriaService(categoriaRepository);
    private final LivroService livroService = new LivroService(livroRepository);
    private final UsuarioService usuarioService = new UsuarioService(usuarioRepository);
    private final EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository, livroRepository, usuarioRepository,
            new MultaComCarenciaStrategy(new BigDecimal("2.00"), 3));
    private final RelatorioService relatorioService = new RelatorioService(relatorioRepository);

    private final TestDataFactory testDataFactory =
            new TestDataFactory(autorService, categoriaService, livroService, usuarioService);

    @Test
    void livroComEmprestimosApareceComQuantidadeExataNaCategoriaCorreta(){
        Livro livro = testDataFactory.criarLivroComEstoque(3);
        Usuario u1 = testDataFactory.criarUsuario();
        Usuario u2 = testDataFactory.criarUsuario();
        Usuario u3 = testDataFactory.criarUsuario();

        emprestimoService.emprestar(u1.getId(), livro.getId(), LocalDate.now().plusDays(7));
        emprestimoService.emprestar(u2.getId(), livro.getId(), LocalDate.now().plusDays(7));
        emprestimoService.emprestar(u3.getId(), livro.getId(), LocalDate.now().plusDays(7));

        List<LivroEmprestimoResumo> livros = relatorioService.livrosMaisEmprestados();
        Optional<LivroEmprestimoResumo> resumo = livros.stream()
                .filter(r -> r.livroId() == livro.getId())
                .findFirst();

        assertTrue(resumo.isPresent(), "livro com empréstimos deveria aparecer no relatório");
        assertEquals(3, resumo.get().quantidadeEmprestimos());

        List<CategoriaEmprestimoResumo> categorias = relatorioService.emprestimosPorCategoria();
        Optional<CategoriaEmprestimoResumo> categoriaResumo = categorias.stream()
                .filter(r -> r.categoriaId() == livro.getCategoriaId())
                .findFirst();

        assertTrue(categoriaResumo.isPresent(), "categoria do livro deveria aparecer no relatório");
        assertEquals(3, categoriaResumo.get().quantidadeEmprestimos());
    }

    @Test
    void livroSemEmprestimoNaoApareceNoRelatorioDeLivros(){
        Livro livro = testDataFactory.criarLivroComEstoque(1);

        List<LivroEmprestimoResumo> livros = relatorioService.livrosMaisEmprestados();

        assertTrue(livros.stream().noneMatch(r -> r.livroId() == livro.getId()),
                "livro sem empréstimos não deveria aparecer no relatório (INNER JOIN)");
    }

    @Test
    void categoriaSemNenhumLivroApareceComQuantidadeZero(){
        Categoria categoria = categoriaService.cadastrar(
                Categoria.builder().nome("Categoria Vazia Teste " + System.nanoTime()).build());

        List<CategoriaEmprestimoResumo> categorias = relatorioService.emprestimosPorCategoria();
        Optional<CategoriaEmprestimoResumo> resumo = categorias.stream()
                .filter(r -> r.categoriaId() == categoria.getId())
                .findFirst();

        assertTrue(resumo.isPresent(), "categoria sem livro deveria continuar aparecendo no relatório (LEFT JOIN)");
        assertEquals(0, resumo.get().quantidadeEmprestimos());
    }

    @Test
    void usuarioComMultiplosEmprestimosApareceComQuantidadeExata(){
        Usuario usuario = testDataFactory.criarUsuario();
        Livro livroA = testDataFactory.criarLivroComEstoque(1);
        Livro livroB = testDataFactory.criarLivroComEstoque(1);

        Emprestimo primeiro = emprestimoService.emprestar(usuario.getId(), livroA.getId(), LocalDate.now().plusDays(7));
        emprestimoService.devolver(primeiro.getId());
        emprestimoService.emprestar(usuario.getId(), livroB.getId(), LocalDate.now().plusDays(7));

        List<UsuarioEmprestimoResumo> usuarios = relatorioService.usuariosComMaisEmprestimos();
        Optional<UsuarioEmprestimoResumo> resumo = usuarios.stream()
                .filter(r -> r.usuarioId() == usuario.getId())
                .findFirst();

        assertTrue(resumo.isPresent());
        assertEquals(2, resumo.get().quantidadeEmprestimos(),
                "total deveria contar todos os status (ATIVO + DEVOLVIDO), não só o histórico aberto");
    }

    @Test
    void usuarioSemEmprestimoNaoApareceNoRelatorioDeUsuarios(){
        Usuario usuario = testDataFactory.criarUsuario();

        List<UsuarioEmprestimoResumo> usuarios = relatorioService.usuariosComMaisEmprestimos();

        assertTrue(usuarios.stream().noneMatch(r -> r.usuarioId() == usuario.getId()));
    }

    @Test
    void totalDeEmprestimosRefleteODeltaDosEmprestimosCriadosNoTeste(){
        long totalAntes = relatorioService.totalDeEmprestimos();

        Usuario usuario = testDataFactory.criarUsuario();
        Livro livro =  testDataFactory.criarLivroComEstoque(1);
        emprestimoService.emprestar(usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

        long totalDepois = relatorioService.totalDeEmprestimos();

        assertEquals(1, totalDepois - totalAntes,
                "total deveria crescer exatamente 1 com o novo empréstimo, independente do que já existia na base");
    }
}
