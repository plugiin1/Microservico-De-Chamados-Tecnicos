package com.pfc.chamados.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfc.auth.entity.Role;
import com.pfc.chamados.dto.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/chamados/*/resolver")
                    .hasAnyRole(Role.TECNICO.name(), Role.ADMIN.name())
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    response.setHeader("WWW-Authenticate", "Basic realm=\"chamados-service\"");
                    escreverErro(request, response, HttpStatus.UNAUTHORIZED, "Autenticação necessária ou credenciais inválidas.");
                })
                .accessDeniedHandler((request, response, e) ->
                    escreverErro(request, response, HttpStatus.FORBIDDEN, "Você não tem permissão para esta operação."))
            );
        return http.build();
    }

    private void escreverErro(HttpServletRequest request, HttpServletResponse response,
                              HttpStatus status, String mensagem) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErroResposta erro = ErroResposta.of(status.value(), status.getReasonPhrase(), mensagem, request.getRequestURI());
        objectMapper.writeValue(response.getOutputStream(), erro);
    }
}
