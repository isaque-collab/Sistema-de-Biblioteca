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
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class EmprestimoServiceRelatoriosIT {

    private final AutorRepository autorRepository = new AutorRepository();
    private final CategoriaRepository categoriaRepository = new CategoriaRepository();
    private final LivroRepository livroRepository = new LivroRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EmprestimoRepository emprestimoRepository = new EmprestimoRepository();

    private final AutorService autorService = new AutorService(autorRepository);
    private final CategoriaService categoriaService = new CategoriaService(categoriaRepository);
    private final LivroService livroService = new LivroService(livroRepository);
    private final UsuarioService usuarioService = new UsuarioService(usuarioRepository);
    private final EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository, livroRepository, usuarioRepository,
            new MultaComCarenciaStrategy(new BigDecimal("2.00"), 3));

    private final TestDataFactory testDataFactory =
            new TestDataFactory(autorService,categoriaService,livroService,usuarioService);

    @Test
    void usuarioAtrasadoDentroDaCarenciaApareceNoAtrasoMasNaoNaMulta() throws SQLException{
        Usuario usuario = testDataFactory.criarUsuario();
        Emprestimo emprestimo = criarEmprestimoPara(usuario);

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(12), LocalDate.now().minusDays(2));

        List<Usuario> atrasados = emprestimoService.usuariosComEmprestimosEmAtraso(LocalDate.now());

        Map<Usuario, BigDecimal> multas = emprestimoService.calcularMultasProjetadasPorUsuario(LocalDate.now());

        assertTrue(atrasados.contains(usuario),
        "usuário com atraso dentro da carência deveria aparecer no relatório de atraso");
        assertFalse(multas.containsKey(usuario),
                "usuário com multa zerada (carência) não deveria aparecer no relatório de mulltas");
    }

    @Test
    void usuarioAtrasadoAlemDaCarenciaApareceNosDoisRelatoriosComValorCorreto() throws SQLException{
        Usuario usuario = testDataFactory.criarUsuario();
        Emprestimo emprestimo = criarEmprestimoPara(usuario);

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(20), LocalDate.now().minusDays(10));

        List<Usuario> atrasados = emprestimoService.usuariosComEmprestimosEmAtraso(LocalDate.now());
        Map<Usuario,BigDecimal> multas = emprestimoService.calcularMultasProjetadasPorUsuario(LocalDate.now());

        assertTrue(atrasados.contains(usuario));
        assertEquals(new BigDecimal("14.00"), multas.get(usuario));
    }

    @Test
    void usuarioSemAtrasoNaoApareceEmNenhumRelatorio(){
        Usuario usuario = testDataFactory.criarUsuario();
        criarEmprestimoPara(usuario);

        List<Usuario> atrasados = emprestimoService.usuariosComEmprestimosEmAtraso(LocalDate.now());
        Map<Usuario, BigDecimal> multas = emprestimoService.calcularMultasProjetadasPorUsuario(LocalDate.now());

        assertFalse(atrasados.contains(usuario));
        assertFalse(multas.containsKey(usuario));
    }

    @Test
    void valorTotalMultasProjetadasReflenteAMultaDoUsuarioAtrasado() throws SQLException{
        BigDecimal totalAntes = emprestimoService.valorTotalMultasProjetadas(LocalDate.now());

        Usuario usuario = testDataFactory.criarUsuario();
        Emprestimo emprestimo = criarEmprestimoPara(usuario);

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(20), LocalDate.now().minusDays(10));

        BigDecimal totalDepois = emprestimoService.valorTotalMultasProjetadas(LocalDate.now());

        assertEquals(new BigDecimal("14.00"), totalDepois.subtract(totalAntes),
                "o total projetado deveria crescer exatamente o valor da multa do novo empréstimo atrasado");
    }

    @Test
    void usuariosComEmprestimosEmAtrasoRetornaListaVaziaSemUsuariosIdsQuandoNaoHaAtraso(){
        LocalDate referencia = LocalDate.now().minusYears(50);

        List<Usuario> atrasados =
                emprestimoService.usuariosComEmprestimosEmAtraso(referencia);

        Map<Usuario, BigDecimal> multas =
                emprestimoService.calcularMultasProjetadasPorUsuario(referencia);

        BigDecimal total =
                emprestimoService.valorTotalMultasProjetadas(referencia);


        assertTrue(atrasados.isEmpty());
        assertTrue(multas.isEmpty());
        assertEquals(BigDecimal.ZERO, total);
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

    private Emprestimo criarEmprestimoPara(Usuario usuario) {
        Livro livro = testDataFactory.criarLivroComEstoque(1);
        return emprestimoService.emprestar(usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));
    }
}
