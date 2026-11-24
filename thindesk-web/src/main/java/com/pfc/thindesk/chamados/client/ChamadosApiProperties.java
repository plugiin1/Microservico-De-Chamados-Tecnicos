package com.pfc.thindesk.chamados.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.chamados-api")
@Getter
@Setter
public class ChamadosApiProperties {

    private String url = "http://localhost:8081";

    private boolean keepAlive = false;
}
