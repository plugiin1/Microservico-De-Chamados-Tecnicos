package com.pfc.chamados.controller;

import com.pfc.chamados.dto.AbrirChamadoRequest;
import com.pfc.chamados.dto.ChamadoResposta;
import com.pfc.chamados.entity.Chamado;
import com.pfc.chamados.entity.StatusChamado;
import com.pfc.chamados.service.ChamadoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/chamados")
@RequiredArgsConstructor
public class ChamadoController {

    private final ChamadoService chamadoService;

    @PostMapping
    public ResponseEntity<ChamadoResposta> abrir(@Valid @RequestBody AbrirChamadoRequest request,
                                                 Authentication authentication) {
        Chamado chamado = chamadoService.abrir(request, authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(chamado.getId())
                .toUri();
        return ResponseEntity.created(location).body(ChamadoResposta.de(chamado));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ChamadoResposta> listarPorUsuario(@PathVariable String usuarioId,
                                                  @RequestParam(required = false) StatusChamado status,
                                                  Authentication authentication) {
        return chamadoService.listarPorUsuario(usuarioId, status, authentication.getName())
                .stream()
                .map(ChamadoResposta::de)
                .toList();
    }

    @GetMapping("/{id}")
    public ChamadoResposta buscar(@PathVariable String id, Authentication authentication) {
        return ChamadoResposta.de(chamadoService.buscar(id, authentication.getName()));
    }
}
