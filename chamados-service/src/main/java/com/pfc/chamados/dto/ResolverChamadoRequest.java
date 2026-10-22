package com.pfc.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResolverChamadoRequest(

        @NotBlank(message = "Informe a solução aplicada.")
        @Size(min = 10, max = 2000, message = "A solução deve ter entre 10 e 2000 caracteres.")
        String solucao) {
}
