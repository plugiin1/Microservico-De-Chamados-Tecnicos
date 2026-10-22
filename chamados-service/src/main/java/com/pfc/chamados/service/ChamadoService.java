package com.pfc.chamados.service;

import com.pfc.auth.entity.Usuario;
import com.pfc.auth.repository.UsuarioRepository;
import com.pfc.chamados.dto.AbrirChamadoRequest;
import com.pfc.chamados.entity.Chamado;
import com.pfc.chamados.entity.StatusChamado;
import com.pfc.chamados.repository.ChamadoRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;

    public Chamado abrir(AbrirChamadoRequest request, String username) {
        Usuario solicitante = buscarUsuario(username);

        Chamado chamado = new Chamado();
        chamado.setTitulo(request.titulo().trim());
        chamado.setDescricao(request.descricao().trim());
        chamado.setStatus(StatusChamado.ABERTO);
        chamado.setSolicitanteId(solicitante.getId());
        chamado.setSolicitanteUsername(solicitante.getUsername());
        chamado.setDataAbertura(Instant.now());
        return chamadoRepository.save(chamado);
    }

    private Usuario buscarUsuario(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado."));
    }
}
