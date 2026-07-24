package br.com.biblioteca.service;

import br.com.biblioteca.exception.*;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.repository.LivroRepository;
import br.com.biblioteca.validator.IsbnValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

public class LivroService {

    private static final Logger log = LogManager.getLogger(LivroService.class);

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public Livro cadastrar(Livro livro) {
        livro.setIsbn(IsbnValidator.validar(livro.getIsbn()));
        validarQuantidade(livro);

        livro.setQuantidadeDisponivel(livro.getQuantidadeTotal());

        if (buscarPorIsbnInterno(livro.getIsbn()).isPresent()) {
            throw new IsbnJaCadastradoException(livro.getIsbn());
        }

        try {
            livroRepository.salvar(livro);
            return livro;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw traduzirViolacaoUnique(e, livro.getIsbn());
        } catch (SQLException e) {
            log.error("Erro ao salvar livro", e);
            throw new PersistenciaException("Erro ao salvar livro", e);
        }
    }

    public Livro buscarPorId(int id) {
        try {
            return livroRepository.buscarPorId(id)
                    .orElseThrow(() -> new LivroNaoEncontradoException(id));
        } catch (SQLException e) {
            log.error("Erro ao buscar livro por id", e);
            throw new PersistenciaException("Erro ao buscar livro por id: " + id, e);
        }
    }

    public List<Livro> buscarTodos() {
        try {
            return livroRepository.buscarTodos();
        } catch (SQLException e) {
            log.error("Erro ao listar livros", e);
            throw new PersistenciaException("Erro ao listar livros", e);
        }
    }

    public Livro atualizar(Livro livro) {
        buscarPorId(livro.getId());

        livro.setIsbn(IsbnValidator.validar(livro.getIsbn()));
        validarQuantidade(livro);

        Optional<Livro> donoDoIsbn = buscarPorIsbnInterno(livro.getIsbn());
        if (donoDoIsbn.isPresent() && donoDoIsbn.get().getId() != livro.getId()) {
            throw new IsbnJaCadastradoException(livro.getIsbn());
        }

        try {
            livroRepository.atualizar(livro);
            return livro;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw traduzirViolacaoUnique(e, livro.getIsbn());
        } catch (SQLException e) {
            log.error("Erro ao atualizar livro", e);
            throw new PersistenciaException("Erro ao atualizar livro", e);
        }
    }

    public void deletar(int id) {
        buscarPorId(id);

        try {
            livroRepository.deletar(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RegistroVinculadoException(
                    "Não é possível excluir o livro: há empréstimos vinculados a ele.");
        } catch (SQLException e) {
            log.error("Erro ao excluir livro", e);
            throw new PersistenciaException("Erro ao excluir livro", e);
        }
    }

    private void validarQuantidade(Livro livro) {
        if (livro.getQuantidadeTotal() == null || livro.getQuantidadeTotal() < 0) {
            throw new IllegalArgumentException("Quantidade total inválida: " + livro.getQuantidadeTotal());
        }

        if (livro.getQuantidadeDisponivel() != null && livro.getQuantidadeDisponivel() > livro.getQuantidadeTotal()) {
            throw new IllegalArgumentException("Quantidade disponível é maior que a quantidade total");
        }
    }


    private RuntimeException traduzirViolacaoUnique(SQLIntegrityConstraintViolationException e, String isbn) {
        String mensagem = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        if (mensagem.contains("isbn")) {
            return new IsbnJaCadastradoException(isbn);
        }

        log.error("Violação de UNIQUE não reconhecida pelo parsing: {}", e.getMessage());
        return new PersistenciaException("Violação de restrição de unicidade não identificada", e);
    }

    private Optional<Livro> buscarPorIsbnInterno(String isbn) {
        try {
            return livroRepository.buscarPorIsbn(isbn);
        } catch (SQLException e) {
            log.error("Erro ao checar duplicidade de isbn", e);
            throw new PersistenciaException("Erro ao checar duplicidade de isbn", e);
        }
    }
}