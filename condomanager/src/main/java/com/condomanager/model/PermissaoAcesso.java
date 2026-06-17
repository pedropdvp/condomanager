package com.condomanager.model;

import com.condomanager.model.enums.Acao;
import com.condomanager.model.enums.Funcionalidade;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Objects;

/**
 * Permissao granular concedida a um utilizador: um par (Funcionalidade, Acao).
 * Persistida como elemento da colecao 'utilizador_permissao'.
 */
@Embeddable
public class PermissaoAcesso {

    @Enumerated(EnumType.STRING)
    @Column(name = "funcionalidade", length = 20, nullable = false)
    private Funcionalidade funcionalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "acao", length = 20, nullable = false)
    private Acao acao;

    public PermissaoAcesso() {
    }

    public PermissaoAcesso(Funcionalidade funcionalidade, Acao acao) {
        this.funcionalidade = funcionalidade;
        this.acao = acao;
    }

    public Funcionalidade getFuncionalidade() { return funcionalidade; }
    public void setFuncionalidade(Funcionalidade funcionalidade) { this.funcionalidade = funcionalidade; }

    public Acao getAcao() { return acao; }
    public void setAcao(Acao acao) { this.acao = acao; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissaoAcesso)) return false;
        PermissaoAcesso that = (PermissaoAcesso) o;
        return funcionalidade == that.funcionalidade && acao == that.acao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcionalidade, acao);
    }
}
