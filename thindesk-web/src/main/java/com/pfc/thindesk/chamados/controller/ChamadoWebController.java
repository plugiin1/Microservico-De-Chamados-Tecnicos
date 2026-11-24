package com.pfc.thindesk.chamados.controller;

import com.pfc.thindesk.chamados.client.ChamadosApiClient;
import com.pfc.thindesk.chamados.dto.AbrirChamadoForm;
import com.pfc.thindesk.chamados.dto.ChamadoDto;
import com.pfc.thindesk.chamados.dto.ResolverChamadoForm;
import com.pfc.thindesk.chamados.dto.UsuarioApiDto;
import com.pfc.thindesk.chamados.service.DashboardService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ChamadoWebController {

    private final ChamadosApiClient api;
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String status, Model model) {
        UsuarioApiDto usuario = api.me();
        List<ChamadoDto> chamados = carregar(usuario, status);

        model.addAttribute("usuarioApi", usuario);
        model.addAttribute("equipe", isEquipe(usuario));
        model.addAttribute("resumo", dashboardService.resumir(chamados));
        return "chamados/dashboard";
    }

    @GetMapping("/chamados")
    public String listar(@RequestParam(required = false) String status, Model model) {
        UsuarioApiDto usuario = api.me();

        model.addAttribute("usuarioApi", usuario);
        model.addAttribute("equipe", isEquipe(usuario));
        model.addAttribute("statusFiltro", status);
        model.addAttribute("chamados", carregar(usuario, status));
        return "chamados/lista";
    }

    @GetMapping("/chamados/novo")
    public String formularioNovo(@ModelAttribute("form") AbrirChamadoForm form) {
        return "chamados/novo";
    }

    @PostMapping("/chamados")
    public String abrir(@Valid @ModelAttribute("form") AbrirChamadoForm form, BindingResult resultado,
                        RedirectAttributes atributos) {
        if (resultado.hasErrors()) {
            return "chamados/novo";
        }
        ChamadoDto chamado = api.abrir(form);
        atributos.addFlashAttribute("sucesso", "Chamado aberto com sucesso.");
        return "redirect:/chamados/" + chamado.id();
    }

    @GetMapping("/chamados/{id}")
    public String detalhar(@PathVariable String id, @ModelAttribute("form") ResolverChamadoForm form, Model model) {
        UsuarioApiDto usuario = api.me();

        model.addAttribute("usuarioApi", usuario);
        model.addAttribute("equipe", isEquipe(usuario));
        model.addAttribute("chamado", api.buscar(id));
        return "chamados/detalhe";
    }

    @PostMapping("/chamados/{id}/resolver")
    public String resolver(@PathVariable String id, @Valid @ModelAttribute("form") ResolverChamadoForm form,
                           BindingResult resultado, Model model, RedirectAttributes atributos) {
        if (resultado.hasErrors()) {
            UsuarioApiDto usuario = api.me();
            model.addAttribute("usuarioApi", usuario);
            model.addAttribute("equipe", isEquipe(usuario));
            model.addAttribute("chamado", api.buscar(id));
            return "chamados/detalhe";
        }
        api.resolver(id, form);
        atributos.addFlashAttribute("sucesso", "Chamado resolvido.");
        return "redirect:/chamados/" + id;
    }

    private List<ChamadoDto> carregar(UsuarioApiDto usuario, String status) {
        return isEquipe(usuario)
                ? api.listarTodos(status)
                : api.listarPorUsuario(usuario.id(), status);
    }

    private boolean isEquipe(UsuarioApiDto usuario) {
        return "ADMIN".equals(usuario.role()) || "TECNICO".equals(usuario.role());
    }
}
