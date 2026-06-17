package com.condomanager.dto;

import java.util.List;

/**
 * Resposta da criacao de um novo acesso. Nunca inclui a password.
 */
public class RegistoResponse {

    private Long id;
    private String nome;
    private String email;
    private List<String> perfis;

    public RegistoResponse(Long id, String nome, String email, List<String> perfis) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfis = perfis;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public List<String> getPerfis() { return perfis; }
}
