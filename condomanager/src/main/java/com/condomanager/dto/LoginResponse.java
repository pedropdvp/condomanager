package com.condomanager.dto;

import java.util.List;

public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private String nome;
    private String email;
    private List<String> perfis;

    public LoginResponse(String token, String nome, String email, List<String> perfis) {
        this.token = token;
        this.nome = nome;
        this.email = email;
        this.perfis = perfis;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getPerfis() { return perfis; }
    public void setPerfis(List<String> perfis) { this.perfis = perfis; }
}
