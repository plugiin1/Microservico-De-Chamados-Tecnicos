package com.pfc.thindesk.chamados.dto;

import java.util.List;

public record ResumoDashboard(
        long total,
        long abertos,
        long resolvidos,
        int percentualResolvidos,
        String tempoMedioResolucao,
        List<ChamadoDto> recentes,
        List<ContagemPorDia> ultimosDias,
        List<ContagemPorTecnico> porTecnico) {

    public record ContagemPorDia(String dia, long abertos, long resolvidos) {
    }

    public record ContagemPorTecnico(String tecnico, long resolvidos) {
    }
}
