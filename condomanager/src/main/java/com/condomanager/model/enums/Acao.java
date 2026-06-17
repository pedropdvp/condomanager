package com.condomanager.model.enums;

/**
 * Acoes elementares sobre uma funcionalidade (RNF de seguranca).
 * Cada par (Funcionalidade, Acao) pode ser ligado/desligado por utilizador.
 */
public enum Acao {
    CRIAR("Criar"),
    EDITAR("Editar"),
    APAGAR("Apagar"),
    CONSULTAR("Consultar");

    private final String etiqueta;

    Acao(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
