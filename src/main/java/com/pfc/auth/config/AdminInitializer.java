package com.pfc.auth.config;

import com.pfc.auth.entity.Role;
import com.pfc.auth.entity.Usuario;
import com.pfc.auth.repository.UsuarioRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String username;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        if (password == null || password.isBlank()) {
            log.warn("Nenhum administrador cadastrado e ADMIN_PASSWORD não definido. Administrador inicial não foi criado.");
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setUsername(username.trim().toLowerCase(Locale.ROOT));
        admin.setEmail(email.trim().toLowerCase(Locale.ROOT));
        admin.setSenha(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        usuarioRepository.save(admin);

        log.info("Administrador inicial '{}' criado.", admin.getUsername());
    }
}
