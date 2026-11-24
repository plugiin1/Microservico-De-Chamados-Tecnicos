package com.pfc.thindesk.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AbrirChamadoForm {

    @NotBlank(message = "Informe o título do chamado.")
    @Size(min = 5, max = 120, message = "O título deve ter de 5 a 120 caracteres.")
    private String titulo;

    @NotBlank(message = "Descreva o problema.")
    @Size(min = 10, max = 2000, message = "A descrição deve ter de 10 a 2000 caracteres.")
    private String descricao;
}
