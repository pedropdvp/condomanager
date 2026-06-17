package com.condomanager.model.enums;

/**
 * Niveis de acesso de um perfil a uma funcionalidade.
 *  - SIM: acesso total (Criar, Editar, Apagar, Consultar)
 *  - CONSULTA: apenas Visualizar/Consultar
 *  - PARTICIPA: pode participar (votar), sem gerir a votacao
 *  - RECEBE: apenas recebe mensagens (nao pode enviar)
 *  - NAO: sem acesso
 */
public enum NivelAcesso {
    SIM("Criar, Editar, Apagar e Consultar"),
    CONSULTA("Apenas consultar"),
    PARTICIPA("Participar (votar)"),
    RECEBE("Apenas receber"),
    NAO("Sem acesso");

    private final String descricao;

    NivelAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
