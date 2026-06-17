package com.condomanager.model.enums;

/**
 * Funcionalidades cobertas pela matriz de permissoes (RNF de seguranca).
 * A ordem reflete a tabela de permissoes do planeamento.
 */
public enum Funcionalidade {
    EMPRESAS("Empresas"),
    CONDOMINIOS("Condominios"),
    CONDOMINOS("Condominos"),
    UTILIZADORES("Utilizadores"),
    ATAS("Atas"),
    PAGAMENTOS("Pagamentos"),
    REUNIOES("Reunioes"),
    VOTACOES("Votacoes"),
    DOCUMENTOS("Documentos"),
    MENSAGENS("Mensagens");

    private final String etiqueta;

    Funcionalidade(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
