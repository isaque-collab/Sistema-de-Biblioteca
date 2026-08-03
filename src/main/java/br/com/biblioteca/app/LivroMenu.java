package br.com.biblioteca.app;

import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.service.AutorService;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.service.LivroService;
import br.com.biblioteca.util.ConsoleUtil;

import java.util.List;
import java.util.Scanner;

public class LivroMenu {

    private final LivroService livroService;
    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private final Scanner scanner;

    public LivroMenu(LivroService livroService, AutorService autorService, CategoriaService categoriaService, Scanner scanner) {
        this.livroService = livroService;
        this.autorService = autorService;
        this.categoriaService = categoriaService;
        this.scanner = scanner;
    }

    public void executar() {
        boolean continuar = true;
        while (continuar) {
            ConsoleUtil.exibirTitulo("Livros");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Buscar por ID");
            System.out.println("3 - Listar todos");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Deletar");
            System.out.println("0 - Voltar");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 5);
            switch (opcao) {
                case 1 -> ConsoleUtil.executarAcao(this::cadastrar);
                case 2 -> ConsoleUtil.executarAcao(this::buscarPorId);
                case 3 -> ConsoleUtil.executarAcao(this::listarTodos);
                case 4 -> ConsoleUtil.executarAcao(this::atualizar);
                case 5 -> ConsoleUtil.executarAcao(this::deletar);
                case 0 -> continuar = false;
            }
        }
    }

    private void cadastrar() {
        String titulo = ConsoleUtil.lerTexto(scanner, "Título");
        String isbn = ConsoleUtil.lerTexto(scanner, "ISBN");

        listarAutoresDisponiveis();
        int autorId = ConsoleUtil.lerInteiro(scanner, "ID do autor");

        listarCategoriasDisponiveis();
        int categoriaId = ConsoleUtil.lerInteiro(scanner, "ID da categoria");

        int quantidadeTotal = ConsoleUtil.lerInteiro(scanner, "Quantidade total de exemplares");

        Livro livro = Livro.builder()
                .titulo(titulo)
                .isbn(isbn)
                .autorId(autorId)
                .categoriaId(categoriaId)
                .quantidadeTotal(quantidadeTotal)
                .build();

        Livro salvo = livroService.cadastrar(livro);
        ConsoleUtil.exibirSucesso("Livro cadastrado com id " + salvo.getId());
    }

    private void buscarPorId() {
        int id = ConsoleUtil.lerInteiro(scanner, "ID");
        imprimir(livroService.buscarPorId(id));
    }

    private void listarTodos() {
        List<Livro> livros = livroService.buscarTodos();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        }
        livros.forEach(this::imprimir);
    }

    private void atualizar() {
        int id = ConsoleUtil.lerInteiro(scanner, "ID do livro a atualizar");
        Livro existente = livroService.buscarPorId(id);

        System.out.println("Deixe em branco para manter o valor atual.");
        String titulo = ConsoleUtil.lerTexto(scanner, "Título (" + existente.getTitulo() + ")");
        String isbn = ConsoleUtil.lerTexto(scanner, "ISBN (" + existente.getIsbn() + ")");

        Livro atualizado = Livro.builder()
                .id(existente.getId())
                .titulo(titulo.isBlank() ? existente.getTitulo() : titulo)
                .isbn(isbn.isBlank() ? existente.getIsbn() : isbn)
                .autorId(existente.getAutorId())
                .categoriaId(existente.getCategoriaId())
                .quantidadeTotal(existente.getQuantidadeTotal())
                .quantidadeDisponivel(existente.getQuantidadeDisponivel())
                .build();

        livroService.atualizar(atualizado);
        ConsoleUtil.exibirSucesso("Livro atualizado.");
    }

    private void deletar() {
        int id = ConsoleUtil.lerInteiro(scanner, "ID do livro a deletar");
        livroService.deletar(id);
        ConsoleUtil.exibirSucesso("Livro removido.");
    }

    private void listarAutoresDisponiveis() {
        List<Autor> autores = autorService.buscarTodos();
        System.out.println("Autores cadastrados:");
        if (autores.isEmpty()) {
            System.out.println("  (nenhum — cadastre um autor antes de continuar)");
        }
        autores.forEach(a -> System.out.println("  " + a.getId() + " - " + a.getNome()));
    }

    private void listarCategoriasDisponiveis() {
        List<Categoria> categorias = categoriaService.buscarTodas();
        System.out.println("Categorias cadastradas:");
        if (categorias.isEmpty()) {
            System.out.println("  (nenhuma — cadastre uma categoria antes de continuar)");
        }
        categorias.forEach(c -> System.out.println("  " + c.getId() + " - " + c.getNome()));
    }

    private void imprimir(Livro livro) {
        System.out.println(livro.getId() + " | " + livro.getTitulo() + " | ISBN " + livro.getIsbn()
                + " | autorId=" + livro.getAutorId() + " | categoriaId=" + livro.getCategoriaId()
                + " | disponível=" + livro.getQuantidadeDisponivel() + "/" + livro.getQuantidadeTotal());
    }
}
