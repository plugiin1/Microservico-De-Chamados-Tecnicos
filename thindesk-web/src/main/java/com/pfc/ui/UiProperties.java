package com.pfc.ui;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.ui")
public class UiProperties {

    private String nome = "Thindesk";

    private String logo = "/images/logo.png";

    private String temaPadrao = "padrao";

    private Map<String, Tema> temas = new LinkedHashMap<>();

    @Getter
    @Setter
    public static class Tema {

        private String descricao;

        private String modo = "light";
    }
}
