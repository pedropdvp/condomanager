package com.condomanager.model.enums;

/**
 * Planos de subscricao SaaS. O limite controla o numero maximo de fracoes.
 */
public enum Plano {
    STARTER(50),
    BUSINESS(300),
    ENTERPRISE(Integer.MAX_VALUE);

    private final int limiteFracoes;

    Plano(int limiteFracoes) {
        this.limiteFracoes = limiteFracoes;
    }

    public int getLimiteFracoes() {
        return limiteFracoes;
    }
}
