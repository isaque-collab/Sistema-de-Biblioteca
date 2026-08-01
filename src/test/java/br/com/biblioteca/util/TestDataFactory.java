package br.com.biblioteca.util;

import br.com.biblioteca.model.Autor;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.service.AutorService;
import br.com.biblioteca.service.CategoriaService;
import br.com.biblioteca.service.LivroService;
import br.com.biblioteca.service.UsuarioService;

import java.util.Arrays;
import java.util.Random;

public final class TestDataFactory {

    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private final LivroService livroService;
    private final UsuarioService usuarioService;

    public TestDataFactory(AutorService autorService, CategoriaService categoriaService,
                           LivroService livroService, UsuarioService usuarioService) {
        this.autorService = autorService;
        this.categoriaService = categoriaService;
        this.livroService = livroService;
        this.usuarioService = usuarioService;
    }

    public Livro criarLivroComEstoque(int quantidade){
        Autor autor = autorService.cadastrar(
                Autor.builder().nome("Autor Test " + System.nanoTime()).build());
        Categoria categoria = categoriaService.cadastrar(
                Categoria.builder().nome("Categoria Test " + System.nanoTime()).build());
        return livroService.cadastrar(
                Livro.builder()
                        .titulo("Livro Teste")
                        .isbn(isbnAleatorioValido())
                        .autorId(autor.getId())
                        .categoriaId(categoria.getId())
                        .quantidadeTotal(quantidade)
                        .build());
    }

    public Usuario criarUsuario(){
        return usuarioService.cadastrar(
                Usuario.builder()
                        .nome("Usuário Teste")
                        .cpf(cpfValidoAleatorio())
                        .email("usuario.teste." + System.nanoTime() + "@teste.com")
                        .build());
    }

    public static String cpfValidoAleatorio(){
        int[] base = new int[9];
        Random r = new Random();
        for (int i = 0; i < 9; i++) base[i] = r.nextInt(10);
        int d1 = digitoVerificador(base, 10);
        int[] comD1 = Arrays.copyOf(base, 10);
        comD1[9] = d1;
        int d2 = digitoVerificador(comD1, 11);
        StringBuilder sb = new StringBuilder();
        for (int d : base) sb.append(d);
        sb.append(d1).append(d2);
        return sb.toString();

    }

    private static int digitoVerificador(int[] digitos, int pesoInicial){
        int soma =0;
        int peso = pesoInicial;
        for (int digito : digitos) {
            soma += digito * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11-resto;
    }

    public static String isbnAleatorioValido(){
        Random r = new Random();
        StringBuilder base = new StringBuilder("978");
        for(int i = 0; i < 9; i++) base.append(r.nextInt(10));
        int soma = 0;
        for (int i = 0; i < 12; i++){
            int d = base.charAt(i) - '0';
            soma += d * (i % 2 == 0 ? 1 : 3);
        }
        int check = (10 - (soma % 10)) % 10;
        return base.append(check).toString();
    }
}
