package br.com.biblioteca.service;

import br.com.biblioteca.exception.*;
import br.com.biblioteca.model.Categoria;
import br.com.biblioteca.repository.CategoriaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

public class CategoriaService {

    private static final Logger log = LogManager.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria cadastrar(Categoria categoria) {
        validarCategoria(categoria);

        if (buscarCategoriaPorNome(categoria.getNome()).isPresent()) {
            throw new CategoriaJaCadastradaException(categoria.getNome());
        }

        try {
            categoriaRepository.salvar(categoria);
            return categoria;
        }catch (SQLIntegrityConstraintViolationException e){
            throw new CategoriaJaCadastradaException(categoria.getNome());
        }catch (SQLException e){
            log.error("Erro ao salvar categoria", e);
            throw new PersistenciaException("Erro ao salvar categoria", e);
        }
    }

    public Categoria buscarPorId(int id) {
        try {
            return categoriaRepository.buscarPorId(id)
                    .orElseThrow(() -> new CategoriaNaoEncontradaException(id));
        }catch (SQLException e){
            log.error("Erro ao buscar categoria por id", e);
            throw new PersistenciaException("Erro ao buscar categoria por id" + id, e);
        }
    }

    public List<Categoria> buscarTodas() {
        try {
            return categoriaRepository.buscarTodas();
        }catch (SQLException e){
            log.error("Erro ao listar categorias", e);
            throw new PersistenciaException("Erro ao listar categorias", e);
        }
    }

    public Categoria atualizar(Categoria categoria) {
        buscarPorId(categoria.getId());

        validarCategoria(categoria);

        Optional<Categoria> dona = buscarCategoriaPorNome(categoria.getNome());
        if (dona.isPresent() && dona.get().getId() != categoria.getId()) {
            throw new CategoriaJaCadastradaException(categoria.getNome());
        }

        try {
            categoriaRepository.atualizar(categoria);
            return categoria;
        }catch (SQLIntegrityConstraintViolationException e){
            throw new CategoriaJaCadastradaException(categoria.getNome());
        }catch (SQLException e){
            log.error("Erro ao atualizar categoria", e);
            throw new PersistenciaException("Erro ao atualizar categoria", e);
        }
    }

    public void deletar(int id) {
        buscarPorId(id);

        try {
            categoriaRepository.deletar(id);
        }catch (SQLIntegrityConstraintViolationException e){
            throw new RegistroVinculadoException("Não é possível excluir a categoria: há livros vinculados a ela.");
        }catch (SQLException e){
            log.error("Erro ao deletar categoria", e);
            throw new PersistenciaException("Erro ao deletar categoria", e);
        }
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria.getNome() == null || categoria.getNome().isBlank()){
            throw new CampoObrigatorioException("nome da categoria");
        }
        categoria.setNome(categoria.getNome().trim());
    }

    private Optional<Categoria> buscarCategoriaPorNome(String nome) {
        try {
            return categoriaRepository.buscarPorNomeExato(nome);
        }catch (SQLException e){
            log.error("Erro ao checar duplicidade de categoria", e);
            throw new PersistenciaException("Erro ao checar duplicidade de categoria", e);
        }
    }
}
