package br.com.biblioteca.app;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.service.EmprestimoService;
import br.com.biblioteca.service.LivroService;
import br.com.biblioteca.service.UsuarioService;
import br.com.biblioteca.util.ConsoleUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class EmprestimoMenu {

    private final EmprestimoService emprestimoService;
    private final LivroService livroService;
    private final UsuarioService usuarioService;
    private final Scanner scanner;

    public EmprestimoMenu(EmprestimoService emprestimoService, LivroService livroService,
                          UsuarioService usuarioService, Scanner scanner) {
        this.emprestimoService = emprestimoService;
        this.livroService = livroService;
        this.usuarioService = usuarioService;
        this.scanner = scanner;
    }

    public void executar() {
        boolean continuar = true;
        while (continuar) {
            ConsoleUtil.exibirTitulo("Empréstimos");
            System.out.println("1 - Emprestar");
            System.out.println("2 - Devolver");
            System.out.println("3 - Buscar por ID");
            System.out.println("4 - Listar todos");
            System.out.println("0 - Voltar");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 4);
            switch (opcao) {
                case 1 -> ConsoleUtil.executarAcao(this::emprestar);
                case 2 -> ConsoleUtil.executarAcao(this::devolver);
                case 3 -> ConsoleUtil.executarAcao(this::buscarPorId);
                case 4 -> ConsoleUtil.executarAcao(this::listarTodos);
                case 0 -> continuar = false;
            }
        }
    }

    private void emprestar(){
        listarUsuarios();
        int usuarioId = ConsoleUtil.lerInteiro(scanner, "ID do usuário");

        listarLivros();
        int livroId = ConsoleUtil.lerInteiro(scanner, "ID do livro");

        LocalDate dataPrevistaDevolucao = ConsoleUtil.lerData(scanner, "Data prevista de devolução");

        Emprestimo emprestimo = emprestimoService.emprestar(usuarioId, livroId, dataPrevistaDevolucao);
        ConsoleUtil.exibirSucesso("Empréstimo registrado com id " + emprestimo.getId());
    }

    private void devolver(){
        int emprestimoId = ConsoleUtil.lerInteiro(scanner, "ID do empréstimo a devolver");
        Emprestimo emprestimo = emprestimoService.devolver(emprestimoId);
        ConsoleUtil.exibirSucesso("Empréstimo " + emprestimo.getId() + " devolvido.");
    }

    private void buscarPorId(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID");
        imprimir(emprestimoService.buscarPorId(id));
    }

    private void listarTodos(){
        List<Emprestimo> emprestimos = emprestimoService.buscarTodos();
        if (emprestimos.isEmpty()){
            System.out.println("Nenhum empréstimo registrado.");
        }
        emprestimos.forEach(this::imprimir);
    }

    private void listarUsuarios(){
        List<Usuario> usuarios = usuarioService.buscarTodos();
        System.out.println("Usuários cadastrados:");
        if (usuarios.isEmpty()){
            System.out.println("  (nenhum - cadastre um usuário antes de continuar)");
        }
        usuarios.forEach(u -> System.out.println("  " + u.getId() + " - " + u.getNome()));
    }

    private void listarLivros(){
        List<Livro> livros = livroService.buscarTodos();
        System.out.println("Livros cadastrados:");
        if (livros.isEmpty()){
            System.out.println("  (nenhum - cadastre um livro antes de continuar)");
        }
        livros.forEach(l -> System.out.println(" " + l.getId() + " - " + l.getTitulo()
        + " (disponível=" + l.getQuantidadeDisponivel() + ")"));
    }

    private void imprimir(Emprestimo emprestimo){
        System.out.println(emprestimo.getId() + " | ID do Usuário: " + emprestimo.getUsuarioId()
        + " | ID do Livro=" + emprestimo.getLivroId() + " | Situação: " + emprestimo.getStatus()
        + " | Previsto: " + emprestimo.getDataPrevistaDevolucao());
    }
}
