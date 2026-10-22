package com.pfc.auth.exception;

public class UsuarioJaExisteException extends RuntimeException {

    private final String campo;

    public UsuarioJaExisteException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
