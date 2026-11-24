package com.pfc.chamados.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InfoController {

    @Value("${spring.application.name}")
    private String servico;

    @Value("${app.versao}")
    private String versao;

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of("servico", servico, "versao", versao, "status", "online");
    }
}
