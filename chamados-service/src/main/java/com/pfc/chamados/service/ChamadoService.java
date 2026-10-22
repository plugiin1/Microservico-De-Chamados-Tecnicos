package com.pfc.chamados.service;

import com.pfc.auth.entity.Role;
import com.pfc.auth.entity.Usuario;
import com.pfc.auth.repository.UsuarioRepository;
import com.pfc.chamados.dto.AbrirChamadoRequest;
import com.pfc.chamados.entity.Chamado;
import com.pfc.chamados.entity.StatusChamado;
import com.pfc.chamados.exception.RecursoNaoEncontradoException;
import com.pfc.chamados.repository.ChamadoRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;

    public Chamado abrir(AbrirChamadoRequest request, String username) {
        Usuario solicitante = buscarUsuarioLogado(username);

        Chamado chamado = new Chamado();
        chamado.setTitulo(request.titulo().trim());
        chamado.setDescricao(request.descricao().trim());
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setSolicitanteId(solicitante.getId());
        chamado.setSolicitanteUsername(solicitante.getUsername());
        chamado.setDataAbertura(Instant.now());
        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarPorUsuario(String usuarioId, StatusChamado status, String username) {
        Usuario logado = buscarUsuarioLogado(username);
        if (!logado.getId().equals(usuarioId) && !isEquipe(logado)) {
            throw new AccessDeniedException("Você só pode listar os seus próprios chamados.");
        }
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado.");
        }

        return status == null
                ? chamadoRepository.findBySolicitanteIdOrderByDataAberturaDesc(usuarioId)
                : chamadoRepository.findBySolicitanteIdAndStatusOrderByDataAberturaDesc(usuarioId, status);
    }

    public Chamado buscar(String id, String username) {
        Usuario logado = buscarUsuarioLogado(username);
        Chamado chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado."));
        if (!chamado.getSolicitanteId().equals(logado.getId()) && !isEquipe(logado)) {
            throw new AccessDeniedException("Você só pode consultar os seus próprios chamados.");
        }
        return chamado;
    }

    private boolean isEquipe(Usuario usuario) {
        return usuario.getRole() == Role.TECNICO || usuario.getRole() == Role.ADMIN;
    }

    private Usuario buscarUsuarioLogado(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado."));
    }
}
