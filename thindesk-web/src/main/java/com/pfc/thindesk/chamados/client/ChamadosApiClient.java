package com.pfc.thindesk.chamados.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfc.thindesk.chamados.dto.AbrirChamadoForm;
import com.pfc.thindesk.chamados.dto.ChamadoDto;
import com.pfc.thindesk.chamados.dto.ResolverChamadoForm;
import com.pfc.thindesk.chamados.dto.UsuarioApiDto;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Component
public class ChamadosApiClient {

    private static final ParameterizedTypeReference<List<ChamadoDto>> LISTA_CHAMADOS =
            new ParameterizedTypeReference<>() {
            };

    private static final int TENTATIVAS_LEITURA = 10;
    private static final Duration ESPERA_ENTRE_TENTATIVAS = Duration.ofSeconds(8);

    private final RestClient restClient;
    private final CredenciaisApi credenciaisApi;
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    public ChamadosApiClient(RestClient.Builder builder, ChamadosApiProperties propriedades,
                             CredenciaisApi credenciaisApi, HttpServletRequest request,
                             ObjectMapper objectMapper) {
        this.restClient = builder
                .baseUrl(propriedades.getUrl())
                .defaultStatusHandler(status -> status.isError(), (req, resposta) -> {
                    throw new ChamadosApiException(resposta.getStatusCode(), mensagemDoErro(resposta.getBody()));
                })
                .build();
        this.credenciaisApi = credenciaisApi;
        this.request = request;
        this.objectMapper = objectMapper;
    }

    public UsuarioApiDto me() {
        return executar(() -> restClient.get()
                .uri("/api/me")
                .header(HttpHeaders.AUTHORIZATION, credenciaisApi.obter(request))
                .retrieve()
                .body(UsuarioApiDto.class), TENTATIVAS_LEITURA);
    }

    public List<ChamadoDto> listarTodos(String status) {
        return executar(() -> restClient.get()
                .uri(uri -> comStatus(uri.path("/api/chamados"), status).build())
                .header(HttpHeaders.AUTHORIZATION, credenciaisApi.obter(request))
                .retrieve()
                .body(LISTA_CHAMADOS), TENTATIVAS_LEITURA);
    }

    public List<ChamadoDto> listarPorUsuario(String usuarioId, String status) {
        return executar(() -> restClient.get()
                .uri(uri -> comStatus(uri.path("/api/chamados/usuario/{id}"), status).build(usuarioId))
                .header(HttpHeaders.AUTHORIZATION, credenciaisApi.obter(request))
                .retrieve()
                .body(LISTA_CHAMADOS), TENTATIVAS_LEITURA);
    }

    public ChamadoDto buscar(String id) {
        return executar(() -> restClient.get()
                .uri("/api/chamados/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, credenciaisApi.obter(request))
                .retrieve()
                .body(ChamadoDto.class), TENTATIVAS_LEITURA);
    }

    public ChamadoDto abrir(AbrirChamadoForm form) {
        return executar(() -> restClient.post()
                .uri("/api/chamados")
                .header(HttpHeaders.AUTHORIZATION, credenciaisApi.obter(request))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("titulo", form.getTitulo(), "descricao", form.getDescricao()))
                .retrieve()
                .body(ChamadoDto.class));
    }

    public ChamadoDto resolver(String id, ResolverChamadoForm form) {
        return executar(() -> restClient.put()
                .uri("/api/chamados/{id}/resolver", id)
                .header(HttpHeaders.AUTHORIZATION, credenciaisApi.obter(request))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("solucao", form.getSolucao()))
                .retrieve()
                .body(ChamadoDto.class));
    }

    private UriBuilder comStatus(UriBuilder uri, String status) {
        return status == null || status.isBlank() ? uri : uri.queryParam("status", status);
    }

    private <T> T executar(java.util.function.Supplier<T> chamada) {
        return executar(chamada, 1);
    }

    private <T> T executar(java.util.function.Supplier<T> chamada, int tentativas) {
        ChamadosApiException ultimoErro = null;
        for (int tentativa = 1; tentativa <= tentativas; tentativa++) {
            try {
                return chamada.get();
            } catch (ResourceAccessException e) {
                ultimoErro = new ChamadosApiException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Não foi possível falar com o microserviço de chamados.");
            } catch (ChamadosApiException e) {
                if (!estaAcordando(e.getStatus())) {
                    throw e;
                }
                ultimoErro = e;
            }
            if (tentativa < tentativas) {
                esperar();
            }
        }
        throw ultimoErro;
    }

    private boolean estaAcordando(HttpStatusCode status) {
        int codigo = status.value();
        return codigo == 502 || codigo == 503 || codigo == 504;
    }

    private void esperar() {
        try {
            Thread.sleep(ESPERA_ENTRE_TENTATIVAS.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ChamadosApiException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A consulta ao microserviço de chamados foi interrompida.");
        }
    }

    private String mensagemDoErro(java.io.InputStream corpo) {
        try {
            byte[] bytes = corpo.readAllBytes();
            if (bytes.length == 0) {
                return "O microserviço de chamados respondeu com erro.";
            }
            JsonNode json = objectMapper.readTree(bytes);
            JsonNode mensagem = json.get("mensagem");
            if (mensagem != null && !mensagem.asText().isBlank()) {
                JsonNode campos = json.get("campos");
                if (campos != null && campos.isObject() && !campos.isEmpty()) {
                    StringBuilder detalhe = new StringBuilder(mensagem.asText());
                    campos.fields().forEachRemaining(campo ->
                            detalhe.append(" ").append(campo.getValue().asText()));
                    return detalhe.toString();
                }
                return mensagem.asText();
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "O microserviço de chamados respondeu com erro.";
        }
    }
}
