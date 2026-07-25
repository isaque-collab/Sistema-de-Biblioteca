package br.com.biblioteca.service;

import br.com.biblioteca.exception.AutorNaoEncontradoException;
import br.com.biblioteca.exception.CampoObrigatorioException;
import br.com.biblioteca.exception.PersistenciaException;
import br.com.biblioteca.exception.RegistroVinculadoException;
import br.com.biblioteca.model.Autor;
import br.com.biblioteca.repository.AutorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class AutorService {

    private static final Logger log = LogManager.getLogger(AutorService.class);

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository){
        this.autorRepository = autorRepository;
    }

    public Autor cadastrar(Autor autor){
        validarAutor(autor);

        try {
            autorRepository.salvar(autor);
            return autor;
        }catch (SQLException e){
            log.error("Erro ao salvar Autor", e);
            throw new PersistenciaException("Erro ao salvar Autor", e);
        }
    }

    public Autor buscarPorId(int id){
        try {
            return autorRepository.buscarPorId(id)
                    .orElseThrow(() -> new AutorNaoEncontradoException(id));
        }catch (SQLException e){
            log.error("Erro ao buscar autor por id", e);
            throw new PersistenciaException("Erro ao buscar autor por id" + id, e);
        }
    }

    public List<Autor> buscarTodos(){
        try {
            return autorRepository.buscarTodos();
        }catch (SQLException e){
            log.error("Erro ao listar autores", e);
            throw new PersistenciaException("Erro ao listar autores", e);
        }
    }

    public List<Autor> buscarPorNome(String nome){
        try {
            return autorRepository.buscarPorNome(nome);
        }catch (SQLException e){
            log.error("Erro ao listar autores por nome", e);
            throw new PersistenciaException("Erro ao listar autores por nome" + nome, e);
        }
    }

    public Autor atualizar(Autor autor){
        buscarPorId(autor.getId());

        validarAutor(autor);

        try {
            autorRepository.atualizar(autor);
            return autor;
        }catch (SQLException e){
            log.error("Erro ao atualizar autor", e);
            throw new PersistenciaException("Erro ao atualizar autor", e);
        }
    }

    public void deletar(int id){
        buscarPorId(id);

        try {
            autorRepository.deletar(id);
        }catch (SQLIntegrityConstraintViolationException e){
            throw new RegistroVinculadoException("Não é possível excluir o autor: há livros vinculados a ele.");
        }catch (SQLException e){
            log.error("Erro ao deletar autor", e);
            throw new PersistenciaException("Erro ao excluir autor", e);
        }
    }


    private void validarAutor(Autor autor){
        if (autor.getNome() == null || autor.getNome().isBlank()){
            throw new CampoObrigatorioException("nome do autor");
        }
        autor.setNome(autor.getNome().trim());
    }
}
