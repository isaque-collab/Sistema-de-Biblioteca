package br.com.biblioteca.service;

import br.com.biblioteca.dto.relatorios.CategoriaEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.LivroEmprestimoResumo;
import br.com.biblioteca.dto.relatorios.UsuarioEmprestimoResumo;
import br.com.biblioteca.exception.PersistenciaException;
import br.com.biblioteca.repository.RelatorioRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

public class RelatorioService {

    private static final Logger log = LogManager.getLogger(RelatorioService.class);

    private final RelatorioRepository relatorioRepository;

    public RelatorioService(RelatorioRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    public List<LivroEmprestimoResumo> livrosMaisEmprestados(){
        try {
            return relatorioRepository.livrosMaisEmprestados();
        }catch (SQLException e){
            log.error("Erro ao gerar relatório de livros mais emprestados", e);
            throw new PersistenciaException("Erro ao gerar relatório de livros mais emprestados", e);
        }
    }

    public List<UsuarioEmprestimoResumo> usuariosComMaisEmprestimos(){
        try {
            return relatorioRepository.usuariosComMaisEmprestimos();
        }catch (SQLException e){
            log.error("Erro ao gerar relatório de usuários com mais empréstimos", e);
            throw new PersistenciaException("Erro ao gerar relatório de usuários com mais empréstimos", e);
        }
    }

    public List<CategoriaEmprestimoResumo> emprestimosPorCategoria(){
        try {
            return relatorioRepository.emprestimosPorCategoria();
        }catch (SQLException e){
            log.error("Erro ao gerar relatório de empréstimos por categoria", e);
            throw new PersistenciaException("Erro ao gerar relatório de empréstimos por categoria", e);
        }
    }

    public long totalDeEmprestimos(){
        try {
            return relatorioRepository.totalDeEmprestimos();
        }catch (SQLException e){
            log.error("Erro ao calcular total de empréstimos", e);
            throw new PersistenciaException("Erro ao calcular total de empréstimos", e);
        }
    }
}
