package br.com.biblioteca.manual;

import br.com.biblioteca.exception.EmprestimoJaDevolvidoException;
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
                        .titulo("Livro Para Devolver")
                        .isbn(isbnAleatorioValido())
                        .autorId(autor.getId())
                        .categoriaId(categoria.getId())
                        .quantidadeTotal(1)
                        .build());

        Usuario usuario = usuarioService.cadastrar(
                Usuario.builder()
                        .nome("Usuario Devolução")
                        .cpf(cpfValidoAleatorio())
                        .email("usuario.devolucao" + System.nanoTime() + "@teste.com")
                        .build());

        Emprestimo emprestimo = emprestimoService.emprestar(
                usuario.getId(), livro.getId(), LocalDate.now().plusDays(7));

        System.out.println("Empréstimo criado id=" + emprestimo.getId()
        + " | quantidade_disponivel após empréstimo deve ser 0");

        CountDownLatch largada = new CountDownLatch(1);
        CountDownLatch chegada = new CountDownLatch(2);

        AtomicReference<String> resultado1  = new AtomicReference<>();
        AtomicReference<String> resultado2 = new AtomicReference<>();

        Thread t1 = new Thread(() -> {
            try {
                largada.await();
                Emprestimo devolvido = emprestimoService.devolver(emprestimo.getId());
                resultado1.set("SUCESSO - status=" + devolvido.getStatus()
                + " dataDevolucao=" + devolvido.getDataDevolucao());
            }catch (EmprestimoJaDevolvidoException e){
                resultado1.set("BLOQUEADO - EmprestimoJaDevolvidoException: " + e.getMessage());
            }catch (Exception e){
                resultado1.set("ERRO INESPERADO - " + e);
            } finally {
                chegada.countDown();
            }
        }, "thread-devolucao-1");

        Thread t2 = new Thread(() -> {
            try {
                largada.await();
                Emprestimo devolvido =  emprestimoService.devolver(emprestimo.getId());
                resultado2.set("SUCESSO - status=" + devolvido.getStatus()
                + " dataDevolucao=" + devolvido.getDataDevolucao());
            }catch (EmprestimoJaDevolvidoException e){
                resultado2.set("BLOQUEADO - EmprestimoJaDevolvidoException: " + e.getMessage());
            }catch (Exception e){
                resultado2.set("ERRO INESPERADO - " + e);
            }finally {
                chegada.countDown();
            }
        }, "thread-devolucao-2");

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

        boolean exatamenteUmSucesso = (resultado1.get().startsWith("SUCESSO")) != (resultado2.get().startsWith("SUCESSO"));
        System.out.println(exatamenteUmSucesso ? "OK - exatamente uma thread teve sucesso."
                : "SUSPEITO - as duas tiveram o mesmo desfecho (deveria ser 1 sucesso + 1 bloqueio).");

        try(Connection conn = ConexaoFactory.getInstance().getConexao();
            PreparedStatement stmt = conn.prepareStatement("SELECT quantidade_disponivel FROM livro WHERE id = ?")){
            stmt.setInt(1, livro.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                int quantidadeFinal = rs.getInt(1);
                System.out.println("quantidade_disponivel final no banco: " + quantidadeFinal);
                if (quantidadeFinal == 1){
                    System.out.println("OK - estoque devolvido exatamente uma vez (voltou a 1).");
                }else {
                    System.out.println("SUSPEITO - esperado 1, veio " + quantidadeFinal);
                }
            }
        }

        try (Connection conn = ConexaoFactory.getInstance().getConexao();
        PreparedStatement stmt = conn.prepareStatement("SELECT emprestimo.status, emprestimo.data_devolucao FROM emprestimo WHERE id = ?")) {
            stmt.setInt(1, emprestimo.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                String status = rs.getString("status");
                java.sql.Date dataDevolucao = rs.getDate("data_devolucao");
                System.out.println("status final no banco: " + status + " | data_devolucao: " + dataDevolucao);
                if ("DEVOLVIDO".equals(status) && dataDevolucao != null) {
                    System.out.println("OK - empréstimo marcado como devolvido, com data gravada.");
                }else {
                    System.out.println("SUSPEITO - status/data inconsistentes.");
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
