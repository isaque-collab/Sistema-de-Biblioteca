package br.com.biblioteca.app;

import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.service.UsuarioService;
import br.com.biblioteca.util.ConsoleUtil;

import java.util.List;
import java.util.Scanner;

public class UsuarioMenu {

    private final UsuarioService usuarioService;
    private final Scanner scanner;

    public UsuarioMenu(UsuarioService usuarioService, Scanner scanner) {
        this.usuarioService = usuarioService;
        this.scanner = scanner;
    }

    public void executar(){
        boolean continuar = true;
        while(continuar){
            ConsoleUtil.exibirTitulo("Usuários");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Buscar por ID");
            System.out.println("3 - Buscar por nome");
            System.out.println("4 - Listar todos");
            System.out.println("5 - Atualizar");
            System.out.println("6 - Deletar");
            System.out.println("0 - Voltar");

            int opcao = ConsoleUtil.lerOpcao(scanner, 0, 6);
            switch(opcao){
                case 1 -> ConsoleUtil.executarAcao(this::cadastrar);
                case 2 -> ConsoleUtil.executarAcao(this::buscarPorId);
                case 3 -> ConsoleUtil.executarAcao(this::buscarPorNome);
                case 4 -> ConsoleUtil.executarAcao(this::listarTodos);
                case 5 -> ConsoleUtil.executarAcao(this::atualizar);
                case 6 -> ConsoleUtil.executarAcao(this::deletar);
                case 0 -> continuar = false;
            }
        }
    }

    private void cadastrar(){
        String nome = ConsoleUtil.lerTexto(scanner, "Nome");
        String email = ConsoleUtil.lerTexto(scanner, "Email");
        String cpf = ConsoleUtil.lerTexto(scanner, "CPF");

        Usuario usuario = Usuario.builder().nome(nome).email(email).cpf(cpf).build();
        Usuario salvo = usuarioService.cadastrar(usuario);
        ConsoleUtil.exibirSucesso("Usuário cadastrado com id " + salvo.getId());
    }

    private void buscarPorId(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID");
        Usuario usuario = usuarioService.buscarPorId(id);
        imprimir(usuario);
    }

    private void buscarPorNome(){
        String nome = ConsoleUtil.lerTexto(scanner, "Nome (ou parte do nome)");
        List<Usuario> usuarios = usuarioService.buscarPorNome(nome);
        if (usuarios.isEmpty()){
            System.out.println("Nenhum usuário encontrado");
        }
        usuarios.forEach(this::imprimir);
    }

    private void listarTodos(){
        List<Usuario> usuarios = usuarioService.buscarTodos();
        if (usuarios.isEmpty()){
            System.out.println("Nenhum usuário encontrado");
        }
        usuarios.forEach(this::imprimir);
    }

    private void atualizar(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID do usuário a atualizar");
        Usuario existente = usuarioService.buscarPorId(id);

        System.out.println("Deixe em branco para manter o valor atual.");
        String nome = ConsoleUtil.lerTexto(scanner, "Nome (" + existente.getNome() + ")");
        String email = ConsoleUtil.lerTexto(scanner, "Email (" + existente.getEmail() + ")");
        String cpf =  ConsoleUtil.lerTexto(scanner, "CPF (" + existente.getCpf() + ")");

        Usuario atualizado = Usuario.builder()
                .id(existente.getId())
                .nome(nome.isBlank() ? existente.getNome() : nome)
                .email(email.isBlank() ? existente.getEmail() : email)
                .cpf(cpf.isBlank() ? existente.getCpf() : cpf)
                .build();

        usuarioService.atualizar(atualizado);
        ConsoleUtil.exibirSucesso("Usuário atualizado.");
    }

    private void deletar(){
        int id = ConsoleUtil.lerInteiro(scanner, "ID do usuário a deletar");
        usuarioService.deletar(id);
        ConsoleUtil.exibirSucesso("Usuário removido.");
    }

    private void imprimir(Usuario usuario){
        System.out.println(usuario.getId() + " | " + usuario.getNome() + " | " + usuario.getEmail() + " | " + usuario.getCpf());
    }
}
