package com.pfc.chamados.dto;

import com.pfc.auth.entity.Role;
import com.pfc.auth.entity.Usuario;

public record UsuarioResposta(String id, String nome, String username, String email, Role role) {

    public static UsuarioResposta de(Usuario usuario) {
        return new UsuarioResposta(usuario.getId(), usuario.getNome(), usuario.getUsername(),
                usuario.getEmail(), usuario.getRole());
    }
}
