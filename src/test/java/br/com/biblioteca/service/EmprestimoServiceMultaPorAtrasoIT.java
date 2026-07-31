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

    @Test
    void devolucaoComDezDiasDeAtrasoCalculaMultaDescontandoACarencia() throws SQLException {
        Emprestimo emprestimo = criarEmprestimo();

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(20),LocalDate.now().minusDays(10));

        Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());
        BigDecimal multa = emprestimoService.calcularMulta(devolvido, devolvido.getDataDevolucao());

        assertEquals(new BigDecimal("14.00"), multa);
    }

    @Test
    void devoluvaoDentroDaCarenciaNaoGeraMulta() throws SQLException {
        Emprestimo emprestimo = criarEmprestimo();

        backdatarEmprestimo(emprestimo.getId(), LocalDate.now().minusDays(12),LocalDate.now().minusDays(2));

        Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());
        BigDecimal multa = emprestimoService.calcularMulta(devolvido, devolvido.getDataDevolucao());

        assertEquals(new BigDecimal("0.00"), multa);
    }

    @Test
    void devolucaoNoPrazoNaoGeraMulta(){
        Livro livro = criarLivroComEstoque(1);
        Usuario usuario = criarUsuario();
        Emprestimo emprestimo = emprestimoService.emprestar(
                usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

        Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());
        BigDecimal multa = emprestimoService.calcularMulta(devolvido, devolvido.getDataDevolucao());

        assertEquals(new BigDecimal("0.00"), multa);
    }

    private void backdatarEmprestimo(int emprestimoId, LocalDate novaDataEmprestimo, LocalDate novaDataPrevistaDevolucao) throws SQLException {
        String sql = "UPDATE emprestimo SET data_emprestimo = ?, data_prevista_devolucao = ? WHERE id = ?";
        try(Connection conn = ConexaoFactory.getInstance().getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setDate(1, Date.valueOf(novaDataEmprestimo));
            stmt.setDate(2, Date.valueOf(novaDataPrevistaDevolucao));
            stmt.setInt(3, emprestimoId);
            stmt.executeUpdate();
        }
    }

    private Emprestimo criarEmprestimo() {
        Livro livro = criarLivroComEstoque(1);
        Usuario usuario = criarUsuario();
        return emprestimoService.emprestar(usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));
    }

    private Livro criarLivroComEstoque(int quantidade) {
        Autor autor = autorService.cadastrar(
                Autor.builder().nome("Autor IT MultaAtraso " + System.nanoTime()).build());
        Categoria categoria = categoriaService.cadastrar(
                Categoria.builder().nome("Categoria IT MultaAtraso " + System.nanoTime()).build());
        return livroService.cadastrar(
                Livro.builder()
                        .titulo("Livro IT MultaAtraso")
                        .isbn(isbnAleatorioValido())
                        .autorId(autor.getId())
                        .categoriaId(categoria.getId())
                        .quantidadeTotal(quantidade)
                        .build());
    }

    private Usuario criarUsuario() {
        return usuarioService.cadastrar(
                Usuario.builder()
                        .nome("Usuario IT MultaAtraso")
                        .cpf(cpfValidoAleatorio())
                        .email("usuario.it.multaatraso." + System.nanoTime() + "@teste.com")
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
