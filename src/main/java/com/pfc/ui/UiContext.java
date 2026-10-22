package com.pfc.ui;

import java.util.Map;

public record UiContext(
        String nome,
        String logo,
        String tema,
        String modo,
        Map<String, UiProperties.Tema> temas) {
}
