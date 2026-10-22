package com.pfc.auth.controller;

import com.pfc.auth.entity.Role;
import com.pfc.auth.entity.Usuario;
import com.pfc.auth.exception.OperacaoNaoPermitidaException;
import com.pfc.auth.service.AdminUsuarioService;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private static final String REDIRECT_LISTA = "redirect:/admin/usuarios";

    private final AdminUsuarioService adminUsuarioService;

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        model.addAttribute("usuarios", adminUsuarioService.listar());
        model.addAttribute("roles", Role.values());
        model.addAttribute("usuarioLogado", authentication.getName());
        return "admin/usuarios";
    }

    @PostMapping("/{id}/perfil")
    public String alterarPerfil(@PathVariable String id, @RequestParam Role role,
                                Authentication authentication, RedirectAttributes redirect) {
        return executar(redirect, () -> {
            Usuario usuario = adminUsuarioService.alterarRole(id, role, authentication.getName());
            return "Perfil de " + usuario.getUsername() + " alterado para " + role.getDescricao() + ".";
        });
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable String id, @RequestParam boolean ativo,
                                Authentication authentication, RedirectAttributes redirect) {
        return executar(redirect, () -> {
            Usuario usuario = adminUsuarioService.alterarStatus(id, ativo, authentication.getName());
            return "Conta de " + usuario.getUsername() + (ativo ? " ativada." : " desativada.");
        });
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable String id, Authentication authentication, RedirectAttributes redirect) {
        return executar(redirect, () -> {
            Usuario usuario = adminUsuarioService.excluir(id, authentication.getName());
            return "Usuário " + usuario.getUsername() + " excluído.";
        });
    }

    private String executar(RedirectAttributes redirect, Supplier<String> acao) {
        try {
            redirect.addFlashAttribute("sucesso", acao.get());
        } catch (OperacaoNaoPermitidaException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return REDIRECT_LISTA;
    }
}
