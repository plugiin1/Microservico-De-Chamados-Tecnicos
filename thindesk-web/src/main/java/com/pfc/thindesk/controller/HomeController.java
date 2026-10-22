package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.ClienteService;
import com.pfc.thindesk.service.HorarioAtendimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ChamadoService chamadoService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private HorarioAtendimentoService horarioAtendimentoService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/chamados")
    public String chamados(Model model) {
        model.addAttribute("chamados", chamadoService.listarChamados());
        return "chamados";
    }

    @GetMapping("/clientes")
    public String clientes(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        return "clientes";
    }

    @GetMapping("/ajustes-horarios")
    public String horarios(Model model) {
        model.addAttribute("horarios", horarioAtendimentoService.listarTodos());
        model.addAttribute("novoHorario", new HorarioAtendimento());
        return "ajustes-horarios";
    }
}
