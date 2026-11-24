package com.pfc.thindesk.chamados.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "app.chamados-api.keep-alive", havingValue = "true")
public class ChamadosApiKeepAlive {

    private static final Logger log = LoggerFactory.getLogger(ChamadosApiKeepAlive.class);

    private final RestClient restClient;

    public ChamadosApiKeepAlive(RestClient.Builder builder, ChamadosApiProperties propriedades) {
        this.restClient = builder.baseUrl(propriedades.getUrl()).build();
    }

    @Scheduled(initialDelayString = "PT1M", fixedDelayString = "PT10M")
    public void manterAcordado() {
        try {
            restClient.get().uri("/api/info").retrieve().toBodilessEntity();
        } catch (RuntimeException e) {
            log.debug("Ping no microserviço de chamados falhou: {}", e.getMessage());
        }
    }
}
