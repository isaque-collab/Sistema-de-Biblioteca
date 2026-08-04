package br.com.biblioteca.app;

import br.com.biblioteca.repository.*;
import br.com.biblioteca.service.*;
import br.com.biblioteca.strategy.MultaComCarenciaStrategy;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AutorRepository autorRepository = new AutorRepository();
        CategoriaRepository categoriaRepository = new CategoriaRepository();
        LivroRepository livroRepository = new LivroRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        EmprestimoRepository emprestimoRepository = new EmprestimoRepository();
        RelatorioRepository relatorioRepository = new RelatorioRepository();

        AutorService autorService = new AutorService(autorRepository);
        CategoriaService categoriaService = new CategoriaService(categoriaRepository);
        LivroService livroService = new LivroService(livroRepository);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        EmprestimoService emprestimoService = new EmprestimoService(emprestimoRepository, livroRepository,
                usuarioRepository, new MultaComCarenciaStrategy(new BigDecimal("2.00"), 3));
        RelatorioService relatorioService = new RelatorioService(relatorioRepository);

        UsuarioMenu usuarioMenu = new UsuarioMenu(usuarioService, scanner);
        AutorCategoriaMenu autorCategoriaMenu = new AutorCategoriaMenu(autorService, categoriaService, scanner);
        LivroMenu livroMenu = new LivroMenu(livroService, autorService, categoriaService, scanner);
        EmprestimoMenu emprestimoMenu = new EmprestimoMenu(emprestimoService, livroService, usuarioService, scanner);
        RelatorioMenu relatorioMenu = new RelatorioMenu(relatorioService, emprestimoService, scanner);

        MenuPrincipal menuPrincipal = new MenuPrincipal(usuarioMenu, autorCategoriaMenu, livroMenu,
                emprestimoMenu, relatorioMenu, scanner);

        menuPrincipal.executar();
    }
}
