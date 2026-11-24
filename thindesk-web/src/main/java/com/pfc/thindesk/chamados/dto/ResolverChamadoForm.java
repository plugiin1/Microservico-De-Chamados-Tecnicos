package com.pfc.thindesk.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResolverChamadoForm {

    @NotBlank(message = "Descreva a solução aplicada.")
    @Size(min = 10, max = 2000, message = "A solução deve ter de 10 a 2000 caracteres.")
    private String solucao;
}
