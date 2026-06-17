package com.condomanager.dto;

import com.condomanager.model.enums.Plano;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmpresaDTO {

    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String nif;

    @NotBlank
    @Email
    private String email;

    private String telefone;
    private String morada;
    private String estado;
    private Plano plano;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Plano getPlano() { return plano; }
    public void setPlano(Plano plano) { this.plano = plano; }
}
