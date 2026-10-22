package com.pfc.auth.service;

import com.pfc.auth.entity.Role;
import com.pfc.auth.entity.Usuario;
import com.pfc.auth.exception.OperacaoNaoPermitidaException;
import com.pfc.auth.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SessaoService sessaoService;

    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("nome").ascending());
    }

    public Usuario alterarRole(String id, Role novaRole, String usernameLogado) {
        Usuario usuario = buscar(id);
        if (usuario.getRole() == novaRole) {
            return usuario;
        }
        impedirAutoAlteracao(usuario, usernameLogado, "Você não pode alterar o seu próprio perfil.");
        if (usuario.getRole() == Role.ADMIN) {
            garantirOutroAdminAtivo(usuario);
        }

        usuario.setRole(novaRole);
        usuarioRepository.save(usuario);
        // Força um novo login pro usuário usar as permissões do novo perfil.
        sessaoService.encerrarSessoes(usuario.getUsername());
        return usuario;
    }

    public Usuario alterarStatus(String id, boolean ativo, String usernameLogado) {
        Usuario usuario = buscar(id);
        if (usuario.isAtivo() == ativo) {
            return usuario;
        }
        impedirAutoAlteracao(usuario, usernameLogado, "Você não pode desativar a sua própria conta.");
        if (!ativo && usuario.getRole() == Role.ADMIN) {
            garantirOutroAdminAtivo(usuario);
        }

        usuario.setAtivo(ativo);
        usuarioRepository.save(usuario);
        if (!ativo) {
            sessaoService.encerrarSessoes(usuario.getUsername());
        }
        return usuario;
    }

    public Usuario excluir(String id, String usernameLogado) {
        Usuario usuario = buscar(id);
        impedirAutoAlteracao(usuario, usernameLogado, "Você não pode excluir a sua própria conta.");
        if (usuario.getRole() == Role.ADMIN) {
            garantirOutroAdminAtivo(usuario);
        }

        usuarioRepository.delete(usuario);
        sessaoService.encerrarSessoes(usuario.getUsername());
        return usuario;
    }

    private Usuario buscar(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new OperacaoNaoPermitidaException("Usuário não encontrado."));
    }

    private void impedirAutoAlteracao(Usuario usuario, String usernameLogado, String mensagem) {
        if (usuario.getUsername().equals(usernameLogado)) {
            throw new OperacaoNaoPermitidaException(mensagem);
        }
    }

    // Impede ela de deixar o sistema sem administrador ativo.
    private void garantirOutroAdminAtivo(Usuario usuario) {
        long adminsAtivos = usuarioRepository.countByRoleAndAtivoTrue(Role.ADMIN);
        long restantes = usuario.isAtivo() ? adminsAtivos - 1 : adminsAtivos;
        if (restantes < 1) {
            throw new OperacaoNaoPermitidaException("O sistema precisa ter pelo menos um administrador ativo.");
        }
    }
}
