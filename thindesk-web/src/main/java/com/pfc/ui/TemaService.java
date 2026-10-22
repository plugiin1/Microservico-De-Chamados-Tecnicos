package com.pfc.ui;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(UiProperties.class)
public class TemaService {

    public static final String COOKIE_TEMA = "tema";

    private final UiProperties properties;

    public boolean existe(String tema) {
        return tema != null && properties.getTemas().containsKey(tema);
    }

    // Só aceita o valor do cookie se for um tema cadastrado.
    public String resolver(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_TEMA);
        if (cookie != null && existe(cookie.getValue())) {
            return cookie.getValue();
        }
        return properties.getTemaPadrao();
    }

    // nome, logo, tema e modo claro/escuro usados pelos layouts.
    public UiContext contexto(HttpServletRequest request) {
        String tema = resolver(request);
        UiProperties.Tema config = properties.getTemas().get(tema);
        String modo = config != null ? config.getModo() : "light";
        return new UiContext(properties.getNome(), properties.getLogo(), tema, modo, properties.getTemas());
    }

    public ResponseCookie criarCookie(String tema) {
        return ResponseCookie.from(COOKIE_TEMA, tema)
                .path("/")
                .maxAge(Duration.ofDays(365))
                .httpOnly(true)
                .sameSite("Lax")
                .build();
    }
}
