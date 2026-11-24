package com.pfc.thindesk.chamados.service;

import com.pfc.thindesk.chamados.dto.ChamadoDto;
import com.pfc.thindesk.chamados.dto.ResumoDashboard;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");
    private static final int DIAS_NO_GRAFICO = 7;
    private static final int MAX_RECENTES = 5;
    private static final int MAX_TECNICOS = 5;

    public ResumoDashboard resumir(List<ChamadoDto> chamados) {
        long total = chamados.size();
        long resolvidos = chamados.stream().filter(ChamadoDto::isResolvido).count();
        long abertos = total - resolvidos;
        int percentual = total == 0 ? 0 : (int) Math.round(resolvidos * 100.0 / total);

        return new ResumoDashboard(
                total,
                abertos,
                resolvidos,
                percentual,
                tempoMedioResolucao(chamados),
                chamados.stream().limit(MAX_RECENTES).toList(),
                contarPorDia(chamados),
                contarPorTecnico(chamados));
    }

    private String tempoMedioResolucao(List<ChamadoDto> chamados) {
        List<Long> minutos = chamados.stream()
                .filter(chamado -> chamado.dataResolucao() != null && chamado.dataAbertura() != null)
                .map(chamado -> Duration.between(chamado.dataAbertura(), chamado.dataResolucao()).toMinutes())
                .toList();
        if (minutos.isEmpty()) {
            return "—";
        }
        long media = Math.round(minutos.stream().mapToLong(Long::longValue).average().orElse(0));
        if (media < 60) {
            return media + " min";
        }
        if (media < 60 * 24) {
            return media / 60 + "h " + media % 60 + "min";
        }
        return media / (60 * 24) + "d " + (media % (60 * 24)) / 60 + "h";
    }

    private List<ResumoDashboard.ContagemPorDia> contarPorDia(List<ChamadoDto> chamados) {
        LocalDate hoje = LocalDate.now(FUSO);
        Map<LocalDate, long[]> contagem = new LinkedHashMap<>();
        for (int i = DIAS_NO_GRAFICO - 1; i >= 0; i--) {
            contagem.put(hoje.minusDays(i), new long[2]);
        }

        for (ChamadoDto chamado : chamados) {
            if (chamado.dataAbertura() != null) {
                long[] dia = contagem.get(chamado.dataAbertura().atZone(FUSO).toLocalDate());
                if (dia != null) {
                    dia[0]++;
                }
            }
            if (chamado.dataResolucao() != null) {
                long[] dia = contagem.get(chamado.dataResolucao().atZone(FUSO).toLocalDate());
                if (dia != null) {
                    dia[1]++;
                }
            }
        }

        List<ResumoDashboard.ContagemPorDia> resultado = new ArrayList<>();
        contagem.forEach((data, valores) ->
                resultado.add(new ResumoDashboard.ContagemPorDia(DIA.format(data), valores[0], valores[1])));
        return resultado;
    }

    private List<ResumoDashboard.ContagemPorTecnico> contarPorTecnico(List<ChamadoDto> chamados) {
        Map<String, Long> porTecnico = new LinkedHashMap<>();
        chamados.stream()
                .filter(chamado -> chamado.tecnico() != null)
                .forEach(chamado -> porTecnico.merge(chamado.tecnico().username(), 1L, Long::sum));

        return porTecnico.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(MAX_TECNICOS)
                .map(entrada -> new ResumoDashboard.ContagemPorTecnico(entrada.getKey(), entrada.getValue()))
                .sorted(Comparator.comparingLong(ResumoDashboard.ContagemPorTecnico::resolvidos).reversed())
                .toList();
    }
}
