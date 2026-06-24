package com.condomanager.model;

/**
 * Estado de uma ocorrência (ver {@code docs/DOMAIN_MODEL.md}).
 */
public enum EstadoOcorrencia {
    ABERTA,
    EM_ANALISE,
    EM_EXECUCAO,
    CONCLUIDA,
    CANCELADA;

    public boolean isTerminal() {
        return this == CONCLUIDA || this == CANCELADA;
    }
}
