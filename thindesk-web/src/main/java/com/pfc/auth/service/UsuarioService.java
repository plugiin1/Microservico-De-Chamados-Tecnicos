package com.pfc.auth.service;

import com.pfc.auth.dto.CadastroForm;
import com.pfc.auth.entity.Role;
import com.pfc.auth.entity.Usuario;
import com.pfc.auth.exception.UsuarioJaExisteException;
import com.pfc.auth.repository.UsuarioRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario cadastrar(CadastroForm form) {
        String username = normalizar(form.getUsername());
        String email = normalizar(form.getEmail());

        if (usuarioRepository.existsByUsername(username)) {
            throw new UsuarioJaExisteException("username", "Este nome de usuário já está em uso.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new UsuarioJaExisteException("email", "Este e-mail já está cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(form.getNome().trim());
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        // todo cadastro normal começa como CLIENTE
        usuario.setRole(Role.CLIENTE);

        // índice único do MongoDB impede dois cadastros iguais enviados ao mesmo tempo.
        try {
            return usuarioRepository.save(usuario);
        } catch (DuplicateKeyException e) {
            throw new UsuarioJaExisteException("username", "Usuário ou e-mail já cadastrado.");
        }
    }

    private String normalizar(String valor) {
        return valor.trim().toLowerCase(Locale.ROOT);
    }
}
