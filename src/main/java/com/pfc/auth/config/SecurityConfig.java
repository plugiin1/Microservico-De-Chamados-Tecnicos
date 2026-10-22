package com.pfc.auth.config;

import com.pfc.auth.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Acessíveis sem login: telas de autenticação, página de erro e arquivos estáticos.
    private static final String[] ROTAS_PUBLICAS = {
        "/login", "/cadastro", "/acesso-negado", "/error",
        "/css/**", "/js/**", "/images/**", "/themes/**", "/favicon.ico"
    };

    // Exclusivas do administrador: gestão de usuários e configurações do sistema.
    private static final String[] ROTAS_ADMIN = {
        "/admin/**",
        "/ajustes-horarios/**", "/api/ajustes-horarios/**",
        "/tecnicos/**", "/tipo-chamado/**", "/recursos/**"
    };

    // Liberadas para administradores e técnicos.
    private static final String[] ROTAS_EQUIPE = {
        "/clientes/**", "/api/clientes/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // A proteção CSRF fica ativa, padrão do Spring Security. Thymeleaf inclui o token nos formulários.
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(ROTAS_PUBLICAS).permitAll()
                .requestMatchers(ROTAS_ADMIN).hasRole(Role.ADMIN.name())
                .requestMatchers(ROTAS_EQUIPE).hasAnyRole(Role.ADMIN.name(), Role.TECNICO.name())
                // Qualquer outra rota exige estar logado, independente do perfil.
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            // Logout com POST: encerra a sessão no MongoDB e remove os cookies do navegador.
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "SESSION")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acesso-negado")
            )
            // Gera um novo ID de sessão após o login.
            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.changeSessionId())
            );
        return http.build();
    }
}
