package com.pfc.ui;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class UiAdvice {

    private final TemaService temaService;

    @ModelAttribute("ui")
    public UiContext ui(HttpServletRequest request) {
        return temaService.contexto(request);
    }
}
