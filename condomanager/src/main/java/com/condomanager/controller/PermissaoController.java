package com.condomanager.controller;

import com.condomanager.model.enums.NivelAcesso;
import com.condomanager.model.enums.NomePerfil;
import com.condomanager.security.MatrizPermissoes;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Expoe a matriz de permissoes para o formulario de criacao de acessos.
 * Disponivel a quem pode criar utilizadores (ADMIN_SISTEMA, GESTOR).
 */
@RestController
@RequestMapping("/api/permissoes")
public class PermissaoController {

    private final MatrizPermissoes matriz;

    public PermissaoController(MatrizPermissoes matriz) {
        this.matriz = matriz;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public Map<String, Object> obterMatriz() {
        // Descricao legivel de cada nivel
        Map<String, String> niveis = new LinkedHashMap<>();
        for (NivelAcesso nivel : NivelAcesso.values()) {
            niveis.put(nivel.name(), nivel.getDescricao());
        }

        // Uma linha por funcionalidade, com o nivel de cada perfil
        List<Map<String, Object>> linhas = new ArrayList<>();
        matriz.getMatriz().forEach((funcionalidade, perfis) -> {
            Map<String, String> permissoes = new LinkedHashMap<>();
            for (NomePerfil perfil : NomePerfil.values()) {
                permissoes.put(perfil.name(), perfis.get(perfil).name());
            }
            Map<String, Object> linha = new LinkedHashMap<>();
            linha.put("funcionalidade", funcionalidade.name());
            linha.put("etiqueta", funcionalidade.getEtiqueta());
            linha.put("permissoes", permissoes);
            linhas.add(linha);
        });

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("niveis", niveis);
        resposta.put("linhas", linhas);
        return resposta;
    }
}
