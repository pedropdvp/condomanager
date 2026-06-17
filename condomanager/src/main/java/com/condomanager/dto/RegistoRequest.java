package com.condomanager.dto;

import com.condomanager.model.enums.NomePerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Pedido de criacao de um novo acesso (utilizador). Usado por POST /api/auth/registo.
 */
public class RegistoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, message = "A password deve ter pelo menos 6 caracteres")
    private String password;

    @NotNull
    private NomePerfil perfil;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public NomePerfil getPerfil() { return perfil; }
    public void setPerfil(NomePerfil perfil) { this.perfil = perfil; }
}
