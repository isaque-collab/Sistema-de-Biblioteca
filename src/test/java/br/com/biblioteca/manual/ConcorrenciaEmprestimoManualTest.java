package br.com.biblioteca.manual;

import br.com.biblioteca.exception.EstoqueIndisponivelException;
import br.com.biblioteca.model.*;
import br.com.biblioteca.repository.*;
import br.com.biblioteca.service.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;
import br.com.biblioteca.util.ConexaoFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ConcorrenciaEmprestimoManualTest {

    public static void main(String[] args) throws Exception {
        AutorRepository autorRepository = new AutorRepository();
        CategoriaRepository categoriaRepository = new CategoriaRepository();
        LivroRepository livroRepository = new LivroRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        EmprestimoRepository emprestimoRepository = new EmprestimoRepository();

        AutorService autorService = new AutorService(autorRepository);
        CategoriaService categoriaService = new CategoriaService(categoriaRepository);
        LivroService livroService = new LivroService(livroRepository);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        EmprestimoService emprestimoService =
                new EmprestimoService(emprestimoRepository, livroRepository, usuarioRepository, new MultaComCarenciaStrategy(
                        new BigDecimal("2.00"), 3));

        Autor autor = autorService.cadastrar(
                Autor.builder().nome("Autor Teste Concorrencia " + System.nanoTime()).build());

        Categoria categoria = categoriaService.cadastrar(
                Categoria.builder().nome("Categoria Teste" + System.nanoTime()).build());

        Livro livro = livroService.cadastrar(
                Livro.builder()
                        .titulo("Livro Disputado")
                        .isbn(isbnAleatorioValido())
                        .autorId(autor.getId())
                        .categoriaId(categoria.getId())
                        .quantidadeTotal(1)
                        .build());

        System.out.println("Livro criado id=" + livro.getId() + " quantidade_disponível=1");

        Usuario usuario1 = usuarioService.cadastrar(
                Usuario.builder()
                        .nome("Usuario UM")
                        .cpf(cpfValidoAleatorio())
                        .email("usuario1." + System.nanoTime() + "@teste.com")
                        .build());

        Usuario usuario2 = usuarioService.cadastrar(
                Usuario.builder()
                        .nome("Usuario Dois")
                        .cpf(cpfValidoAleatorio())
                        .email("usuario2." + System.nanoTime() + "@teste.com")
                        .build());

        System.out.println("Usuário1 id=" + usuario1.getId() + " | Usuário2 id=" + usuario2.getId());

        CountDownLatch largada = new CountDownLatch(1);
        CountDownLatch chegada = new CountDownLatch(2);

        AtomicReference<String> resultado1  = new AtomicReference<>();
        AtomicReference<String> resultado2 = new AtomicReference<>();

        LocalDate prazo = LocalDate.now().plusDays(7);

        Thread t1 = new Thread(() -> {
            try {
                largada.await();
                Emprestimo e = emprestimoService.emprestar(usuario1.getId(), livro.getId(), prazo);

                resultado1.set("SUCESSO - emprestimo id=" + e.getId());
            }catch (EstoqueIndisponivelException e){
                resultado1.set("BLOQUEADO - EstoqueIndisponivelException: " + e.getMessage());
            }catch (Exception e){
                resultado1.set("ERRO INESPERADO - " + e);
            } finally {
                chegada.countDown();
            }
        }, "thread-usuario-1");

        Thread t2 = new Thread(() -> {
            try {
                largada.await();
                Emprestimo e =  emprestimoService.emprestar(usuario2.getId(), livro.getId(), prazo);
                resultado2.set("SUCESSO - emprestimo id=" + e.getId());
            }catch (EstoqueIndisponivelException e){
                resultado2.set("BLOQUEADO - EstoqueIndisponivelException: " + e.getMessage());
            }catch (Exception e){
                resultado2.set("ERRO INESPERADO - " + e);
            }finally {
                chegada.countDown();
            }
        }, "thread-usuario-2");

        t1.start();
        t2.start();
        largada.countDown();

        boolean terminouNoPrazo = chegada.await(15, TimeUnit.SECONDS);

        System.out.println("--------------------------------------");
        System.out.println("Thread usuario1: " + resultado1.get());
        System.out.println("Thread usuario2: " + resultado2.get());
        if (!terminouNoPrazo) {
            System.out.println("ATENÇÃO: uma das threads não terminou em 15s - possível deadlock/lock esperando.");
        }

        try(Connection conn = ConexaoFactory.getInstance().getConexao();
            PreparedStatement stmt = conn.prepareStatement("SELECT quantidade_disponivel FROM livro WHERE id = ?")){
            stmt.setInt(1, livro.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int quantidadeFinal = rs.getInt(1);
                System.out.println("quantidade_disponivel final no banco: " + quantidadeFinal);
                if (quantidadeFinal == 0){
                    System.out.println("OK - exatamente 1 exemplar foi consumido.");
                }else {
                    System.out.println("SUSPEITO - esperado 0, veio " + quantidadeFinal);
                }
            }
        }

        try (Connection conn = ConexaoFactory.getInstance().getConexao();
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM emprestimo WHERE livro_id = ?")) {
            stmt.setInt(1, livro.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int totalEmprestimo = rs.getInt(1);
                System.out.println("Total de linhas em empréstimo para esse livro: " + totalEmprestimo);
                if (totalEmprestimo == 1){
                    System.out.println("OK - só 1 empréstimo foi de fato gravado.");
                }else {
                    System.out.println("SUSPEITO - esperado 1, veio " + totalEmprestimo);
                }
            }
        }
    }

    private static String cpfValidoAleatorio() {
        int[] base = new int[9];
        java.util.Random r = new java.util.Random();
        for (int i=0; i <9; i++) base[i] = r.nextInt(10);
        int d1 = digitoVerificador(base, 10);
        int[] comD1 = java.util.Arrays.copyOf(base, 10);
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
        for (int i = 0; i < pesoInicial - 1; i++) {
            soma += digitos[i] * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto <2) ? 0 : 11 - resto;
    }

    private static String isbnAleatorioValido() {
        java.util.Random r = new java.util.Random();
        StringBuilder base = new StringBuilder("978");
        for (int i = 0; i < 9; i++) base.append(r.nextInt(10));
        int soma =0;
        for (int i = 0; i < 12; i++){
            int d = base.charAt(i) - '0';
            soma += d * (i%2 == 0 ? 1 : 3);
        }
        int check = (10 - (soma %10)) %10;
        return base.append(check).toString();
    }
}
