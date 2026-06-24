package com.condomanager.model;

/**
 * Tipo de mensagem interna (ver {@code docs/DOMAIN_MODEL.md}).
 * <ul>
 *   <li>{@code INDIVIDUAL} — um único destinatário ({@code mensagem.destino}).</li>
 *   <li>{@code GRUPO} — vários destinatários explícitos (tabela {@code mensagem_destinatario}, V21).</li>
 *   <li>{@code BROADCAST} — difusão a todos os utilizadores do tenant.</li>
 * </ul>
 */
public enum TipoMensagem {
    INDIVIDUAL,
    GRUPO,
    BROADCAST
}
