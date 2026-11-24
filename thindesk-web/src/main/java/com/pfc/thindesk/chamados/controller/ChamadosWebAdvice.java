package com.pfc.thindesk.chamados.controller;

import com.pfc.thindesk.chamados.client.ChamadosApiException;
import com.pfc.thindesk.chamados.client.CredenciaisApi;
import com.pfc.ui.TemaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = ChamadoWebController.class)
@RequiredArgsConstructor
public class ChamadosWebAdvice {

    private final TemaService temaService;

    @ExceptionHandler(ChamadosApiException.class)
    public String erroDaApi(ChamadosApiException excecao, HttpServletRequest request, Model model) {
        model.addAttribute("ui", temaService.contexto(request));
        model.addAttribute("status", excecao.getStatus().value());
        model.addAttribute("mensagem", excecao.getMessage());
        return "chamados/erro";
    }

    @ExceptionHandler(CredenciaisApi.CredenciaisIndisponiveisException.class)
    public String semCredenciais() {
        return "redirect:/sessao-expirada";
    }
}
