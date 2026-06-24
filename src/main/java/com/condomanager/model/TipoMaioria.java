package com.condomanager.model;

/**
 * Tipo de maioria exigida para aprovar uma deliberação
 * (ver {@code docs/LEGAL_RULES.md} §6).
 */
public enum TipoMaioria {
    /** Mais de 50% do capital presente. */
    MAIORIA_SIMPLES,
    /** Maioria do capital presente e zero votos contra. */
    SEM_OPOSICAO,
    /** Pelo menos 2/3 do capital total do edifício. */
    DOIS_TERCOS,
    /** 100% do capital total. */
    UNANIMIDADE
}
