package com.pfc.chamados.controller;

import com.pfc.auth.repository.UsuarioRepository;
import com.pfc.chamados.dto.UsuarioResposta;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResposta> me(Authentication authentication) {
        return usuarioRepository.findByUsername(authentication.getName())
                .map(UsuarioResposta::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
