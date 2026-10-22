package com.pfc.chamados.exception;

public class ChamadoJaResolvidoException extends RuntimeException {

    public ChamadoJaResolvidoException() {
        super("Este chamado já foi resolvido e não pode ser alterado.");
    }
}
