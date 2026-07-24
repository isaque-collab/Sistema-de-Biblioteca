package br.com.biblioteca.service;

import br.com.biblioteca.exception.*;
import br.com.biblioteca.model.Usuario;
import br.com.biblioteca.repository.UsuarioRepository;
import br.com.biblioteca.validator.CpfValidator;
import br.com.biblioteca.validator.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private static final Logger log = LogManager.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrar(Usuario usuario){
        validarUsuario(usuario);

        if (buscarUsuarioPorCpf(usuario.getCpf()).isPresent()){
            throw new CpfJaCadastradoException(usuario.getCpf());
        }

        if (buscarUsuarioPorEmail(usuario.getEmail()).isPresent()){
            throw new EmailJaCadastradoException(usuario.getEmail());
        }

        try {
            usuarioRepository.salvar(usuario);
            return usuario;
        }catch (SQLIntegrityConstraintViolationException e){
            throw traduzirViolacaoUnique(e,usuario.getCpf(), usuario.getEmail());
        }catch (SQLException e){
            log.error("Erro ao salvar usuário", e);
            throw new PersistenciaException("Erro ao salvar usuário", e);
        }
    }

    public Usuario buscarPorId(int id){
        try {
            return usuarioRepository.buscarPorId(id)
                    .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        }catch (SQLException e){
            log.error("Erro ao buscar usuário por id", e);
            throw new PersistenciaException("Erro ao buscar usuário por id: "+id, e);
        }
    }

    public List<Usuario> buscarTodos(){
        try {
            return usuarioRepository.buscarTodos();
        }catch (SQLException e){
            log.error("Erro ao listar usuários", e);
            throw new PersistenciaException("Erro ao listar usuários", e);
        }
    }

    public List<Usuario> buscarPorNome(String nome){
        try {
            return usuarioRepository.buscarPorNome(nome);
        }catch (SQLException e){
            log.error("Erro ao buscar usuários por nome", e);
            throw new PersistenciaException("Erro ao buscar usuários por nome: "+nome, e);
        }
    }

    public Usuario atualizar(Usuario usuario){
        buscarPorId(usuario.getId());

        validarUsuario(usuario);

        Optional<Usuario> donoDoCpf = buscarUsuarioPorCpf(usuario.getCpf());
        if (donoDoCpf.isPresent() && donoDoCpf.get().getId() != usuario.getId()){
            throw new CpfJaCadastradoException(usuario.getCpf());
        }
        Optional<Usuario> donoDoEmail = buscarUsuarioPorEmail(usuario.getEmail());
        if (donoDoEmail.isPresent() && donoDoEmail.get().getId() != usuario.getId()){
            throw new EmailJaCadastradoException(usuario.getEmail());
        }

        try {
            usuarioRepository.atualizar(usuario);
            return usuario;
        }catch (SQLIntegrityConstraintViolationException e){
            throw traduzirViolacaoUnique(e, usuario.getCpf(), usuario.getEmail());
        }catch (SQLException e){
            log.error("Erro ao atualizar usuário", e);
            throw new PersistenciaException("Erro ao atualizar usuário", e);
        }
    }

    public void deletar(int id){
        buscarPorId(id);

        try {
            usuarioRepository.deletar(id);
        }catch (SQLIntegrityConstraintViolationException e){
            throw new RegistroVinculadoException("Não é possível excluir o usuário: há empréstimos vinculados a ele.");
        }catch (SQLException e){
            log.error("Erro ao deletar usuário", e);
            throw new PersistenciaException("Erro ao excluir usuário", e);
        }
    }

    private void validarUsuario(Usuario usuario){
        usuario.setCpf(CpfValidator.validar(usuario.getCpf()));
        usuario.setEmail(EmailValidator.validar(usuario.getEmail()));
    }

    private RuntimeException traduzirViolacaoUnique(SQLIntegrityConstraintViolationException e, String cpf, String email){
        String mensagem = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        if (mensagem.contains("cpf")){
            return new CpfJaCadastradoException(cpf);
        }
        if (mensagem.contains("email")){
            return new EmailJaCadastradoException(email);
        }

        log.error("Violação de UNIQUE não reconhecida pelo parsing: {}", e.getMessage());
        return new PersistenciaException("Violação de restrição de unicidade não identificada", e);
    }

    private Optional<Usuario> buscarUsuarioPorCpf(String cpf){
        try {
            return usuarioRepository.buscarPorCpf(cpf);
        }catch (SQLException e){
            log.error("Erro ao checar duplicidade de cpf", e);
            throw new PersistenciaException("Erro ao checar duplicidade", e);
        }
    }

    private Optional<Usuario> buscarUsuarioPorEmail(String email){
        try {
            return usuarioRepository.buscarPorEmail(email);
        }catch (SQLException e){
            log.error("Erro ao checar duplicidade de e-mail", e);
            throw new PersistenciaException("Erro ao checar duplicidade do e-mail", e);
        }
    }
}
