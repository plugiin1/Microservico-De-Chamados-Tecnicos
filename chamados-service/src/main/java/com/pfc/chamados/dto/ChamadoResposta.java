package com.pfc.chamados.dto;

import com.pfc.chamados.entity.Chamado;
import com.pfc.chamados.entity.StatusChamado;
import java.time.Instant;

public record ChamadoResposta(
        String id,
        String titulo,
        String descricao,
        StatusChamado status,
        Pessoa solicitante,
        Instant dataAbertura,
        String solucao,
        Pessoa tecnico,
        Instant dataResolucao) {

    public record Pessoa(String id, String username) {
    }

    public static ChamadoResposta de(Chamado chamado) {
        Pessoa tecnico = chamado.getTecnicoId() == null
                ? null
                : new Pessoa(chamado.getTecnicoId(), chamado.getTecnicoUsername());
        return new ChamadoResposta(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getStatus(),
                new Pessoa(chamado.getSolicitanteId(), chamado.getSolicitanteUsername()),
                chamado.getDataAbertura(),
                chamado.getSolucao(),
                tecnico,
                chamado.getDataResolucao());
    }
}
