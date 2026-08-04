package br.com.biblioteca.app;

import br.com.biblioteca.util.ConsoleUtil;

public class MenuPrincipal {

    private final UsuarioMenu usuarioMenu;
    private final AutorCategoriaMenu autorCategoriaMenu;
    private final LivroMenu livroMenu;
    private final EmprestimoMenu emprestimoMenu;
    private final RelatorioMenu relatorioMenu;

    private final java.util.Scanner scanner;
    public MenuPrincipal(UsuarioMenu usuarioMenu, AutorCategoriaMenu autorCategoriaMenu, LivroMenu livroMenu,
                         EmprestimoMenu emprestimoMenu, RelatorioMenu relatorioMenu, java.util.Scanner scanner) {
        this.usuarioMenu = usuarioMenu;
        this.autorCategoriaMenu = autorCategoriaMenu;
        this.livroMenu = livroMenu;
        this.emprestimoMenu = emprestimoMenu;
        this.relatorioMenu = relatorioMenu;
        this.scanner = scanner;
    }

    public void executar(){
        boolean continuar = true;
        while(continuar){
            ConsoleUtil.exibirTitulo("Sistema de Gerenciamento de Biblioteca");
            System.out.println("1 - Usuários");
            System.out.println("2 - Autor / Categoria");
            System.out.println("3 - Livros");
            System.out.println("4 - Empréstimos");
            System.out.println("5 - Relatórios");
            System.out.println("0 - Sair");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 5);
            switch (opcao){
                case 1 -> usuarioMenu.executar();
                case 2 -> autorCategoriaMenu.executar();
                case 3 -> livroMenu.executar();
                case 4 -> emprestimoMenu.executar();
                case 5 -> relatorioMenu.executar();
                case 0 -> {
                    continuar = false;
                    System.out.println("Encerrado. Até mais!");
                }
            }
        }
    }
}
