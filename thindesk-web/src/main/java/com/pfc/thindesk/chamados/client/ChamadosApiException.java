package com.pfc.thindesk.chamados.client;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ChamadosApiException extends RuntimeException {

    private final HttpStatusCode status;

    public ChamadosApiException(HttpStatusCode status, String mensagem) {
        super(mensagem);
        this.status = status;
    }
}
