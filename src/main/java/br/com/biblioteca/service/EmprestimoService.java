package br.com.biblioteca.service;

import br.com.biblioteca.enums.SituacaoEmprestimo;
import br.com.biblioteca.enums.StatusEmprestimo;
import br.com.biblioteca.exception.*;
import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.repository.EmprestimoRepository;
import br.com.biblioteca.repository.LivroRepository;
import br.com.biblioteca.repository.UsuarioRepository;
import br.com.biblioteca.util.ConexaoFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EmprestimoService {

    private static final Logger log =  LogManager.getLogger(EmprestimoService.class);

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository,
                             UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Emprestimo emprestar(int usuarioId, int livroId, LocalDate dataPrevistaDevolucao){
        try(Connection conn = ConexaoFactory.getInstance().getConexao()){
            conn.setAutoCommit(false);

            try {
                usuarioRepository.buscarPorId(usuarioId, conn)
                        .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

                livroRepository.buscarPorId(livroId, conn)
                        .orElseThrow(() -> new LivroNaoEncontradoException(livroId));

                boolean linhasAfetada = livroRepository.diminuirEstoque(livroId, conn);
                if (!linhasAfetada){
                    throw new EstoqueIndisponivelException(livroId);
                }

                Emprestimo emprestimo = Emprestimo.builder()
                        .usuarioId(usuarioId)
                        .livroId(livroId)
                        .dataEmprestimo(LocalDate.now())
                        .dataPrevistaDevolucao(dataPrevistaDevolucao)
                        .status(StatusEmprestimo.ATIVO)
                        .build();

                emprestimoRepository.salvar(emprestimo, conn);

                conn.commit();
                return emprestimo;

            }catch (RuntimeException | SQLException e){
                conn.rollback();
                if (e instanceof RuntimeException){
                    throw (RuntimeException) e;
                }
                log.error("Erro ao registrar empréstimo", e);
                throw new PersistenciaException("Erro ao registrar empréstimo", e);
            }

        }catch (SQLException e){
            log.error("Erro de conexão ao registrar empréstimo", e);
            throw new PersistenciaException("Erro de conexão ao registrar empréstimo", e);
        }
    }

    public Emprestimo buscarPorId(int id){
        try {
            return emprestimoRepository.buscarPorId(id)
                    .orElseThrow(() -> new EmprestimoNaoEncontradoException(id));
        }catch (SQLException e){
            log.error("Erro ao buscar empréstimo por id", e);
            throw new PersistenciaException("Erro ao buscar empréstimo por id: " + id, e);
        }
    }

    public List<Emprestimo> buscarTodos(){
        try {
            return emprestimoRepository.buscarTodos();
        }catch (SQLException e){
            log.error("Erro ao listar empréstimos", e);
            throw new PersistenciaException("Erro ao listar empréstimos", e);
        }
    }

    /**
     * Determina a situação atual de um empréstimo.
     *
     * A precedência das regras é:
     * 1. Empréstimos devolvidos sempre possuem situação DEVOLVIDO.
     * 2. Empréstimos ativos cuja data prevista já passou possuem situação ATRASADO.
     * 3. Caso contrário, a situação é ATIVO.
     *
     * @param emprestimo empréstimo analisado
     * @param dataReferencia data utilizada para a verificação
     * @return situação calculada do empréstimo
     */

    public SituacaoEmprestimo determinarSituacao(Emprestimo emprestimo, LocalDate dataReferencia){
        if (emprestimo == null){
            throw new IllegalArgumentException("emprestimo não pode ser nulo.");
        }
        if (dataReferencia == null){
            throw new IllegalArgumentException("dataReferencia não pode ser nula.");
        }
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO){
            return SituacaoEmprestimo.DEVOLVIDO;
        }
        if (dataReferencia.isAfter(emprestimo.getDataPrevistaDevolucao())){
            return SituacaoEmprestimo.ATRASADO;
        }
        return SituacaoEmprestimo.ATIVO;
    }
}
