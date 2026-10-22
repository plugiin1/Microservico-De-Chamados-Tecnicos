package com.pfc.ui;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TemaController {

    private final TemaService temaService;

    @PostMapping("/tema")
    public String trocar(@RequestParam String tema,
                         @RequestHeader(value = HttpHeaders.REFERER, required = false) String referer,
                         HttpServletResponse response) {
        if (temaService.existe(tema)) {
            response.addHeader(HttpHeaders.SET_COOKIE, temaService.criarCookie(tema).toString());
        }
        return "redirect:" + caminhoLocal(referer);
    }

    private String caminhoLocal(String referer) {
        if (referer == null) {
            return "/";
        }
        try {
            URI uri = URI.create(referer);
            String caminho = uri.getRawPath();
            if (caminho == null || !caminho.startsWith("/") || caminho.startsWith("//")) {
                return "/";
            }
            return uri.getRawQuery() == null ? caminho : caminho + "?" + uri.getRawQuery();
        } catch (IllegalArgumentException e) {
            return "/";
        }
    }
}
