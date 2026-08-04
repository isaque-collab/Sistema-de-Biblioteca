package br.com.biblioteca.app;

import br.com.biblioteca.dto.relatorios.CategoriaEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.LivroEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.UsuarioEmprestimoResumo;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.service.EmprestimoService;
import br.com.biblioteca.service.RelatorioService;
import br.com.biblioteca.util.ConsoleUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RelatorioMenu {

    private final RelatorioService relatorioService;
    private final EmprestimoService emprestimoService;
    private final Scanner scanner;

    public RelatorioMenu(RelatorioService relatorioService, EmprestimoService emprestimoService, Scanner scanner) {
        this.relatorioService = relatorioService;
        this.emprestimoService = emprestimoService;
        this.scanner = scanner;
    }

    public void executar(){
        boolean continuar = true;
        while(continuar){
            ConsoleUtil.exibirTitulo("Relatórios");
            System.out.println("1 - Livros mais emprestados");
            System.out.println("2 - Usuários com mais empréstimos");
            System.out.println("3 - Empréstimos por categoria");
            System.out.println("4 - Total de empréstimos");
            System.out.println("5 - Usuários com empréstimos em atraso (hoje)");
            System.out.println("6 - Multas projetadas por usuário (hoje)");
            System.out.println("7 - Valor total de multas projetadas (hoje)");
            System.out.println("0 - Voltar");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 7);
            switch(opcao){
                case 1 -> ConsoleUtil.executarAcao(this::livrosMaisEmprestados);
                case 2 -> ConsoleUtil.executarAcao(this::usuariosComMaisEmprestimos);
                case 3 -> ConsoleUtil.executarAcao(this::emprestimosPorCategoria);
                case 4 -> ConsoleUtil.executarAcao(this::totalDeEmprestimos);
                case 5 -> ConsoleUtil.executarAcao(this::usuariosComEmprestimosEmAtraso);
                case 6 -> ConsoleUtil.executarAcao(this::multasProjetadasPorUsuario);
                case 7 -> ConsoleUtil.executarAcao(this::valorTotalMultasProjetadas);
                case 0 -> continuar = false;
            }
        }
    }

    private void livrosMaisEmprestados(){
        List<LivroEmprestimoResumo> resumo = relatorioService.livrosMaisEmprestados();
        if (resumo.isEmpty()){
            System.out.println("Nenhum livro com empréstimos registrado.");
        }
        resumo.forEach(r -> System.out.println(r.titulo() + " - " + r.quantidadeEmprestimos() + " empréstimo(s)"));
    }

    private void usuariosComMaisEmprestimos(){
        List<UsuarioEmprestimoResumo> resumo = relatorioService.usuariosComMaisEmprestimos();
        if (resumo.isEmpty()){
            System.out.println("Nenhum usuário com empréstimo registrado.");
        }
        resumo.forEach(r -> System.out.println(r.nome() + " - " + r.quantidadeEmprestimos() + " empréstimo(s)"));
    }

    private void emprestimosPorCategoria(){
        List<CategoriaEmprestimoResumo> resumo = relatorioService.emprestimosPorCategoria();
        if (resumo.isEmpty()){
            System.out.println("Nenhuma categoria cadastrada.");
        }
        resumo.forEach(r -> System.out.println(r.nome() + " - " + r.quantidadeEmprestimos() + " empréstimo(s)"));
    }

    private void totalDeEmprestimos(){
        System.out.println("Total de empréstimos (todos os status): " + relatorioService.totalDeEmprestimos());
    }

    private void usuariosComEmprestimosEmAtraso(){
        List<Usuario> usuarios = emprestimoService.usuariosComEmprestimosEmAtraso(LocalDate.now());
        if (usuarios.isEmpty()){
            System.out.println("Nenhum usuário com empréstimo em atraso hoje.");
        }
        usuarios.forEach(u -> System.out.println(u.getId() + " - " + u.getNome()));
    }

    private void multasProjetadasPorUsuario(){
        Map<Usuario, BigDecimal> multas = emprestimoService.calcularMultasProjetadasPorUsuario(LocalDate.now());
        if (multas.isEmpty()){
            System.out.println("Nenhuma multa projetada hoje.");
        }
        multas.forEach((usuario, valor) -> System.out.println(usuario.getNome() + " - R$ " + valor));
    }

    private void valorTotalMultasProjetadas(){
        BigDecimal total = emprestimoService.valorTotalMultasProjetadas(LocalDate.now());
        System.out.println("Valor total de multas projetadas hoje: R$ " + total);
    }
}
