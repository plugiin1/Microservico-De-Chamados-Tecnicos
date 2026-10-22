package com.pfc.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AbrirChamadoRequest(

        @NotBlank(message = "Informe o título do chamado.")
        @Size(min = 5, max = 120, message = "O título deve ter entre 5 e 120 caracteres.")
        String titulo,

        @NotBlank(message = "Informe a descrição do chamado.")
        @Size(min = 10, max = 2000, message = "A descrição deve ter entre 10 e 2000 caracteres.")
        String descricao) {
}
