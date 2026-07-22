package br.com.biblioteca.model;
import br.com.biblioteca.enums.StatusEmprestimo;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
public class Emprestimo {
    private int id;
    private int usuarioId;
    private int livroId;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private StatusEmprestimo status;

}
