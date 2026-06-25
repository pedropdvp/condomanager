package com.condomanager.model;

/**
 * Funcionalidades (módulos) sujeitas a controlo de permissões granulares.
 * Correspondem às linhas da matriz de permissões (ver {@code PermissaoService}).
 */
public enum Funcionalidade {
    EMPRESAS,
    CONDOMINIOS,
    CONDOMINOS,
    UTILIZADORES,
    ATAS,
    PAGAMENTOS,
    REUNIOES,
    VOTACOES,
    DOCUMENTOS,
    MENSAGENS
}
