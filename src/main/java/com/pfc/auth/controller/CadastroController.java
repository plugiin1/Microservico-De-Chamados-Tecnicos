package com.pfc.auth.controller;

import com.pfc.auth.dto.CadastroForm;
import com.pfc.auth.exception.UsuarioJaExisteException;
import com.pfc.auth.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cadastro")
@RequiredArgsConstructor
public class CadastroController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String formulario(Model model) {
        model.addAttribute("form", new CadastroForm());
        return "auth/cadastro";
    }

    @PostMapping
    public String cadastrar(@Valid @ModelAttribute("form") CadastroForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "auth/cadastro";
        }
        try {
            usuarioService.cadastrar(form);
        } catch (UsuarioJaExisteException e) {
            result.rejectValue(e.getCampo(), "duplicado", e.getMessage());
            return "auth/cadastro";
        }
        return "redirect:/login?cadastrado";
    }
}
