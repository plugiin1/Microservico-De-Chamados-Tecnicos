package com.pfc.thindesk.chamados.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChamadoDto(
        String id,
        String titulo,
        String descricao,
        String status,
        Pessoa solicitante,
        Instant dataAbertura,
        String solucao,
        Pessoa tecnico,
        Instant dataResolucao) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Pessoa(String id, String username) {
    }

    public boolean isResolvido() {
        return "RESOLVIDO".equals(status);
    }
}
