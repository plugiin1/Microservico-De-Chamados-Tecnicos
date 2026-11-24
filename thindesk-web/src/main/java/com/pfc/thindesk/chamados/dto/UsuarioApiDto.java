package com.pfc.thindesk.chamados.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioApiDto(String id, String nome, String username, String email, String role) {
}
