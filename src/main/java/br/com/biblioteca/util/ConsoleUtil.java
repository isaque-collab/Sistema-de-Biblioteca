package br.com.biblioteca.util;

import br.com.biblioteca.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class ConsoleUtil {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Logger log = LogManager.getLogger(ConsoleUtil.class);

    private ConsoleUtil(){
    }

    public static void executarAcao(Runnable acao){
        try {
            acao.run();
        }catch (PersistenciaException e){
            log.error("Erro de persistência", e);
            exibirErro(e.getMessage());
        }catch (ValidacaoException | RegistroNaoEncontradoException | RegistroDuplicadoException
        | EstoqueIndisponivelException | EmprestimoAtivoExistenteException
        | EmprestimoJaDevolvidoException e){
            exibirErro(e.getMessage());
        }catch (RuntimeException e){
            log.error("Erro inesperado", e);
            exibirErro("Ocorreu um erro inesperado. Consulte o log para mais detalhes.");
        }
    }

    public static int lerOpcao(Scanner scanner, int min, int max){
        while (true){
            System.out.print("Opção: ");
            String entrada = scanner.nextLine().trim();
            try {
                int opcao = Integer.parseInt(entrada);
                if (opcao < min || opcao > max){
                    System.out.println("Opção fora do intervalo (" + min + " a " + max + "). Tente novamente.");
                    continue;
                }
                return opcao;
            }catch (NumberFormatException e){
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }

    public static String lerTexto(Scanner scanner, String rotulo){
        System.out.print(rotulo + ": ");
        return scanner.nextLine().trim();
    }

    public static int lerInteiro(Scanner scanner, String rotulo){
        while(true){
            System.out.print(rotulo + ": ");
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            }catch (NumberFormatException e){
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    public static LocalDate lerData(Scanner scanner, String rotulo){
        while(true){
            System.out.print(rotulo + " (dd/MM/aaaa): ");
            String entrada = scanner.nextLine().trim();
            try {
                return LocalDate.parse(entrada, FORMATO_DATA);
            }catch (DateTimeParseException e){
                System.out.println("Data inválida. Use o formato dd/MM/aaaa.");
            }
        }
    }

    public static void exibirErro(String mensagem){
        System.out.println("[ERRO] " + mensagem);
    }

    public static void exibirSucesso(String mensagem){
        System.out.println("[OK] " + mensagem);
    }

    public static void exibirTitulo(String titulo){
        System.out.println();
        System.out.println("== " + titulo + " ==");
    }

    public static void pausar(Scanner scanner){
        System.out.print("Pressione ENTER para continuar...");
        scanner.nextLine();
    }
}
