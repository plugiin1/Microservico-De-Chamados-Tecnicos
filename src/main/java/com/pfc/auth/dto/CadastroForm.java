package com.pfc.auth.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroForm {

    @NotBlank(message = "Informe seu nome.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe um nome de usuário.")
    @Size(min = 3, max = 30, message = "O usuário deve ter entre 3 e 30 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]*$", message = "Use apenas letras, números, ponto, hífen ou sublinhado.")
    private String username;

    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres.")
    private String email;

    @NotBlank(message = "Informe uma senha.")
    @Size(min = 8, max = 64, message = "A senha deve ter entre 8 e 64 caracteres.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
             message = "A senha deve conter letra maiúscula, letra minúscula e número.")
    private String senha;

    @NotBlank(message = "Confirme sua senha.")
    private String confirmacaoSenha;

    @AssertTrue(message = "As senhas não conferem.")
    public boolean isSenhasConferem() {
        return senha == null || senha.equals(confirmacaoSenha);
    }
}
