package br.com.biblioteca.service;

import br.com.biblioteca.model.*;
import br.com.biblioteca.repository.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;
import br.com.biblioteca.util.ConexaoFactory;
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

    @Test
    void usuarioAtrasadoDentroDaCarenciaApareceNoAtrasoMasNaoNaMulta() throws SQLException{
        Usuario usuario = criarUsuario();
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
        Usuario usuario = criarUsuario();
        Emprestimo emprestimo = criarEmprestimoPara(usuario);

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(20), LocalDate.now().minusDays(10));

        List<Usuario> atrasados = emprestimoService.usuariosComEmprestimosEmAtraso(LocalDate.now());
        Map<Usuario,BigDecimal> multas = emprestimoService.calcularMultasProjetadasPorUsuario(LocalDate.now());

        assertTrue(atrasados.contains(usuario));
        assertEquals(new BigDecimal("14.00"), multas.get(usuario));
    }

    @Test
    void usuarioSemAtrasoNaoApareceEmNenhumRelatorio(){
        Usuario usuario = criarUsuario();
        criarEmprestimoPara(usuario);

        List<Usuario> atrasados = emprestimoService.usuariosComEmprestimosEmAtraso(LocalDate.now());
        Map<Usuario, BigDecimal> multas = emprestimoService.calcularMultasProjetadasPorUsuario(LocalDate.now());

        assertFalse(atrasados.contains(usuario));
        assertFalse(multas.containsKey(usuario));
    }

    @Test
    void valorTotalMultasProjetadasReflenteAMultaDoUsuarioAtrasado() throws SQLException{
        BigDecimal totalAntes = emprestimoService.valorTotalMultasProjetadas(LocalDate.now());

        Usuario usuario = criarUsuario();
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
        Livro livro = criarLivroComEstoque(1);
        return emprestimoService.emprestar(usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));
    }

    private Livro criarLivroComEstoque(int quantidade) {
        Autor autor = autorService.cadastrar(
                Autor.builder().nome("Autor IT Relatorios " + System.nanoTime()).build());
        Categoria categoria = categoriaService.cadastrar(
                Categoria.builder().nome("Categoria IT Relatorios " + System.nanoTime()).build());
        return livroService.cadastrar(
                Livro.builder()
                        .titulo("Livro IT Relatorios")
                        .isbn(isbnAleatorioValido())
                        .autorId(autor.getId())
                        .categoriaId(categoria.getId())
                        .quantidadeTotal(quantidade)
                        .build());
    }


    private Usuario criarUsuario() {
        return usuarioService.cadastrar(
                Usuario.builder()
                        .nome("Usuario IT Relatorios")
                        .cpf(cpfValidoAleatorio())
                        .email("usuario.it.relatorios." + System.nanoTime() + "@teste.com")
                        .build());
    }

    private static String cpfValidoAleatorio() {
        int[] base = new int[9];
        Random r = new Random();
        for (int i = 0; i < 9; i++) base[i] = r.nextInt(10);
        int d1 = digitoVerificador(base, 10);
        int[] comD1 = Arrays.copyOf(base, 10);
        comD1[9] = d1;
        int d2 = digitoVerificador(comD1, 11);
        StringBuilder sb = new StringBuilder();
        for (int d : base) sb.append(d);
        sb.append(d1).append(d2);
        return sb.toString();
    }

    private static int digitoVerificador(int[] digitos, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int digito : digitos) {
            soma += digito * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    private static String isbnAleatorioValido() {
        Random r = new Random();
        StringBuilder base = new StringBuilder("978");
        for (int i = 0; i < 9; i++) base.append(r.nextInt(10));
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            int d = base.charAt(i) - '0';
            soma += d * (i % 2 == 0 ? 1 : 3);
        }
        int check = (10 - (soma % 10)) % 10;
        return base.append(check).toString();
    }
}
