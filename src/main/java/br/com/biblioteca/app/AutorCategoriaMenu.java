package br.com.biblioteca.app;

import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.service.AutorService;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.util.ConsoleUtil;

import java.util.List;
import java.util.Scanner;

public class AutorCategoriaMenu {

    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private final Scanner scanner;

    public AutorCategoriaMenu(AutorService autorService, CategoriaService categoriaService, Scanner scanner) {
        this.autorService = autorService;
        this.categoriaService = categoriaService;
        this.scanner = scanner;
    }

    public void executar(){
        boolean continuar = true;
        while(continuar){
            ConsoleUtil.exibirTitulo("Autor / Categoria");
            System.out.println("1 - Gerenciar Autor");
            System.out.println("2 - Gerenciar Categoria");
            System.out.println("0 - Voltar");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 2);
            switch (opcao){
                case 1 -> menuAutor();
                case 2 -> menuCategoria();
                case 0 -> continuar = false;
            }
        }
    }

    private void menuAutor(){
        boolean continuar = true;
        while(continuar){
            ConsoleUtil.exibirTitulo("Autor");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Buscar por ID");
            System.out.println("3 - Buscar por nome");
            System.out.println("4 - Listar todos");
            System.out.println("5 - Atualizar");
            System.out.println("6 - Deletar");
            System.out.println("0 - Voltar");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 6);
            switch (opcao){
                case 1 -> ConsoleUtil.executarAcao(this::cadastrarAutor);
                case 2 -> ConsoleUtil.executarAcao(this::buscarAutorPorId);
                case 3 -> ConsoleUtil.executarAcao(this::buscarAutorPorNome);
                case 4 -> ConsoleUtil.executarAcao(this::listarAutores);
                case 5 -> ConsoleUtil.executarAcao(this::atualizarAutor);
                case 6 -> ConsoleUtil.executarAcao(this::deletarAutor);
                case 0 -> continuar = false;
            }
        }
    }

    private void menuCategoria(){
        boolean continuar = true;
        while(continuar){
            ConsoleUtil.exibirTitulo("Categoria");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Buscar por ID");
            System.out.println("3  Listar todas");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Deletar");
            System.out.println("0 - Voltar");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 5);
            switch (opcao){
                case 1 -> ConsoleUtil.executarAcao(this::cadastrarCategoria);
                case 2 -> ConsoleUtil.executarAcao(this::buscarCategoriaPorId);
                case 3 -> ConsoleUtil.executarAcao(this::listarCategorias);
                case 4 -> ConsoleUtil.executarAcao(this::atualizarCategoria);
                case 5 -> ConsoleUtil.executarAcao(this::deletarCategoria);
                case 0 -> continuar = false;
            }
        }
    }

    private void cadastrarAutor(){
        String nome = ConsoleUtil.lerTexto(scanner, "Nome");
        String nacionalidade = ConsoleUtil.lerTexto(scanner, "Nacionalidade");
        Autor salvo = autorService.cadastrar(Autor.builder().nome(nome).nacionalidade(nacionalidade).build());
        ConsoleUtil.exibirSucesso("Autor cadastrado com id " + salvo.getId());
    }

    private void buscarAutorPorId(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID");
        imprimir(autorService.buscarPorId(id));
    }

    private void buscarAutorPorNome(){
        String nome = ConsoleUtil.lerTexto(scanner, "Nome (ou parte do nome)");
        List<Autor> autores = autorService.buscarPorNome(nome);
        if(autores.isEmpty()){
            System.out.println("Nenhum autor encontrado.");
        }
        autores.forEach(this::imprimir);
    }

    private void listarAutores(){
        List<Autor> autores = autorService.buscarTodos();
        if (autores.isEmpty()){
            System.out.println("Nenhum autor cadastrado.");
        }
        autores.forEach(this::imprimir);
    }

    private void atualizarAutor(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID do autor a atualizar");
        Autor existente =  autorService.buscarPorId(id);

        System.out.println("Deixe em branco para manter o valor atual.");
        String nome = ConsoleUtil.lerTexto(scanner, "Nome (" + existente.getNome() + ")");
        String nacionalidade = ConsoleUtil.lerTexto(scanner, "Nacionalidade (" + existente.getNacionalidade() + ")");

        Autor atualizado = Autor.builder()
                .id(existente.getId())
                .nome(nome.isBlank() ? existente.getNome() : nome)
                .nacionalidade(nacionalidade.isBlank() ? existente.getNacionalidade() : nacionalidade)
                .build();

        autorService.atualizar(atualizado);
        ConsoleUtil.exibirSucesso("Autor atualizado.");
    }

    private void deletarAutor(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID do autor a deletar");
        autorService.deletar(id);
        ConsoleUtil.exibirSucesso("Autor removido.");
    }

    private void imprimir(Autor autor){
        System.out.println(autor.getId() + " | " + autor.getNome() + " | " + autor.getNacionalidade());
    }

    private void cadastrarCategoria(){
        String nome = ConsoleUtil.lerTexto(scanner, "Nome");
        Categoria salva = categoriaService.cadastrar(Categoria.builder().nome(nome).build());
        ConsoleUtil.exibirSucesso("Categoria cadastrada com id " + salva.getId());
    }

    private void buscarCategoriaPorId(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID");
        imprimir(categoriaService.buscarPorId(id));
    }

    private void listarCategorias(){
        List<Categoria> categorias = categoriaService.buscarTodas();
        if (categorias.isEmpty()){
            System.out.println("Nenhuma categoria cadastrada.");
        }
        categorias.forEach(this::imprimir);
    }

    private void atualizarCategoria(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID da categoria a atualizar");
        Categoria existente = categoriaService.buscarPorId(id);

        String nome = ConsoleUtil.lerTexto(scanner, "Nome (" + existente.getNome() + "deixe em branco para manter)");

        Categoria atualizada = Categoria.builder()
                .id(existente.getId())
                .nome(nome.isBlank() ? existente.getNome() : nome)
                .build();

        categoriaService.atualizar(atualizada);
        ConsoleUtil.exibirSucesso("Categoria atualizada.");
    }

    private void deletarCategoria(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID da categoria a deletar");
        categoriaService.deletar(id);
        ConsoleUtil.exibirSucesso("Categoria removida.");
    }

    private void imprimir(Categoria categoria){
        System.out.println(categoria.getId() + " | " + categoria.getNome());
    }
}
