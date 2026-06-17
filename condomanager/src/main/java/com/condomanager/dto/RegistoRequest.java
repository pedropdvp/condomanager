package com.condomanager.dto;

import com.condomanager.model.enums.Acao;
import com.condomanager.model.enums.Funcionalidade;
import com.condomanager.model.enums.NomePerfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

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

    /**
     * Permissoes granulares escolhidas (Funcionalidade -> lista de Acoes).
     * Se omitido, sao aplicadas as permissoes-padrao do perfil (matriz).
     */
    private Map<Funcionalidade, List<Acao>> permissoes;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public NomePerfil getPerfil() { return perfil; }
    public void setPerfil(NomePerfil perfil) { this.perfil = perfil; }

    public Map<Funcionalidade, List<Acao>> getPermissoes() { return permissoes; }
    public void setPermissoes(Map<Funcionalidade, List<Acao>> permissoes) { this.permissoes = permissoes; }
}
