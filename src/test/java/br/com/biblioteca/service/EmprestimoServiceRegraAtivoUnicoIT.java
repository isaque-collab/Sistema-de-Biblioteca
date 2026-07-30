package br.com.biblioteca.service;

import br.com.biblioteca.exception.EmprestimoAtivoExistenteException;
import br.com.biblioteca.model.*;
import br.com.biblioteca.repository.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;
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

    private final LocalDate dataPrevista = LocalDate.now().plusDays(7);

    @Test
    void segundoEmprestimoDoMesmoLivroPeloMesmoUsuarioLancaEmprestimoAtivoExistenteException(){
        Livro livro = criarLivroComEstoque(2);
        Usuario usuario = criarUsuario();

        emprestimoService.emprestar(usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

        assertThrows(EmprestimoAtivoExistenteException.class, () -> emprestimoService.emprestar(usuario.getId(),
                livro.getId(), dataPrevista));
    }

    @Test
    void aposDevolverUsuarioPodeEmprestarOMesmoLivroDeNovo(){
        Livro livro = criarLivroComEstoque(1);
        Usuario usuario = criarUsuario();

        Emprestimo primeiro = emprestimoService.emprestar(usuario.getId(), livro.getId(), dataPrevista);
        emprestimoService.devolver(primeiro.getId());

        Emprestimo segundo = assertDoesNotThrow(() -> emprestimoService.emprestar(usuario.getId(), livro.getId(), dataPrevista));

        assertNotEquals(primeiro.getId(), segundo.getId());
    }

    @Test
    void emprestimoAtivoDeUmLivroNaoImpedeEmprestimoDeOutroLivro(){
        Livro livroA = criarLivroComEstoque(1);
        Livro livroB = criarLivroComEstoque(1);
        Usuario usuario = criarUsuario();

        emprestimoService.emprestar(usuario.getId(), livroA.getId(), LocalDate.now().plusDays(7));

        assertDoesNotThrow(() -> emprestimoService.emprestar(usuario.getId(), livroB.getId(), dataPrevista));
    }

    @Test
    void usuariosDiferentesPodemEmprestarOMesmoLivro(){
        Livro livro = criarLivroComEstoque(2);

        Usuario usuario1 = criarUsuario();
        Usuario usuario2 = criarUsuario();

        assertDoesNotThrow(() ->
                emprestimoService.emprestar(usuario1.getId(), livro.getId(), LocalDate.now().plusDays(7)));

        assertDoesNotThrow(() ->
                emprestimoService.emprestar(usuario2.getId(), livro.getId(), LocalDate.now().plusDays(7)));
    }


    private Livro criarLivroComEstoque(int quantidade){
        Autor autor = autorService.cadastrar(
                Autor.builder().nome("Autor IT RegraAtivo " + System.nanoTime()).build());
        Categoria categoria = categoriaService.cadastrar(
                Categoria.builder().nome("Categoria IT RegraAtivo " + System.nanoTime()).build());

        return livroService.cadastrar(
                Livro.builder()
                        .titulo("Livro IT RegraAtivo")
                        .isbn(isbnAleatorioValido())
                        .autorId(autor.getId())
                        .categoriaId(categoria.getId())
                        .quantidadeTotal(quantidade)
                        .build());
    }

    private Usuario criarUsuario(){
        return usuarioService.cadastrar(
                Usuario.builder()
                        .nome("Usuario IT RegraAtivo")
                        .cpf(cpfValidoAleatorio())
                        .email("usuario.it.regraativo." + System.nanoTime() + "@teste.com")
                        .build());
    }

    private static String cpfValidoAleatorio(){
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

    private static int digitoVerificador(int[] digitos, int pesoInicial){
        int soma = 0;
        int peso = pesoInicial;
        for (int digito : digitos) {
            soma += digito * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }

    private static String isbnAleatorioValido(){
        Random r = new Random();
        StringBuilder base  = new StringBuilder("978");
        for (int i = 0; i < 9; i++) base.append(r.nextInt(10));
        int soma = 0;
        for (int i = 0; i < 12; i++){
            int d = base.charAt(i) - '0';
            soma += d * (i % 2 == 0 ? 1:3);
        }
        int check = (10 - (soma % 10)) % 10;
        return base.append(check).toString();
    }
}
