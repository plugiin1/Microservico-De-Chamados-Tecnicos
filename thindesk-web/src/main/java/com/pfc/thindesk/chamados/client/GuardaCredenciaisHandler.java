package com.pfc.thindesk.chamados.client;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class GuardaCredenciaisHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public GuardaCredenciaisHandler() {
        setDefaultTargetUrl("/dashboard");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String senha = request.getParameter("password");
        if (senha != null) {
            request.getSession().setAttribute(CredenciaisApi.ATRIBUTO_SESSAO,
                    CredenciaisApi.cabecalhoBasic(authentication.getName(), senha));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
