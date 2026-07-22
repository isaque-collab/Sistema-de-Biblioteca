package br.com.biblioteca.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Livro {
    private int id;
    private String titulo;
    private String isbn;
    private int autorId;
    private int categoriaId;
    private Integer quantidadeTotal;
    private Integer quantidadeDisponivel;

}
