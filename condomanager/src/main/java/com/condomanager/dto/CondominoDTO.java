package com.condomanager.dto;

import com.condomanager.model.enums.TipoCondomino;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CondominoDTO {

    private Long id;

    @NotBlank
    private String nome;

    private String nif;

    @Email
    private String email;

    private String telefone;
    private TipoCondomino tipo;
    private Long fracaoId;

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

    public TipoCondomino getTipo() { return tipo; }
    public void setTipo(TipoCondomino tipo) { this.tipo = tipo; }

    public Long getFracaoId() { return fracaoId; }
    public void setFracaoId(Long fracaoId) { this.fracaoId = fracaoId; }
}
