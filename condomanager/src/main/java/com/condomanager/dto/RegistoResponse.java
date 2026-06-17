package com.condomanager.dto;

import java.util.List;
import java.util.Map;

/**
 * Resposta da criacao de um novo acesso. Nunca inclui a password.
 */
public class RegistoResponse {

    private Long id;
    private String nome;
    private String email;
    private List<String> perfis;
    private Map<String, List<String>> permissoes;

    public RegistoResponse(Long id, String nome, String email,
                           List<String> perfis, Map<String, List<String>> permissoes) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfis = perfis;
        this.permissoes = permissoes;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public List<String> getPerfis() { return perfis; }
    public Map<String, List<String>> getPermissoes() { return permissoes; }
}
