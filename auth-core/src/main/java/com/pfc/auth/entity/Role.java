package com.pfc.auth.entity;

public enum Role {

    ADMIN("Administrador"),
    TECNICO("Técnico"),
    CLIENTE("Cliente");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
