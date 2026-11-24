package com.pfc.thindesk.chamados.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class CredenciaisApi {

    public static final String ATRIBUTO_SESSAO = "CHAMADOS_API_BASIC";

    public static String cabecalhoBasic(String username, String senha) {
        String credenciais = username + ":" + senha;
        return "Basic " + Base64.getEncoder().encodeToString(credenciais.getBytes(StandardCharsets.UTF_8));
    }

    public String obter(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        Object valor = sessao == null ? null : sessao.getAttribute(ATRIBUTO_SESSAO);
        if (valor == null) {
            throw new CredenciaisIndisponiveisException();
        }
        return valor.toString();
    }

    public static class CredenciaisIndisponiveisException extends RuntimeException {
        public CredenciaisIndisponiveisException() {
            super("Sessão sem credenciais para a API de chamados. Faça login novamente.");
        }
    }
}
