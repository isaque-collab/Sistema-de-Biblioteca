package br.com.biblioteca.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Autor {
    private int id;
    private String nome;
    private String nacionalidade;
}
