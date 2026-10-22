package com.pfc.chamados.dto;

import java.time.Instant;
import java.util.Map;

public record ErroResposta(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        Map<String, String> campos) {

    public static ErroResposta of(int status, String erro, String mensagem, String caminho) {
        return new ErroResposta(Instant.now(), status, erro, mensagem, caminho, null);
    }

    public static ErroResposta of(int status, String erro, String mensagem, String caminho, Map<String, String> campos) {
        return new ErroResposta(Instant.now(), status, erro, mensagem, caminho, campos);
    }
}
