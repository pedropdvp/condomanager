package com.condomanager.controller;

import com.condomanager.model.enums.Acao;
import com.condomanager.model.enums.Funcionalidade;
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
 * Fornece, ao formulario de criacao de acessos, as acoes possiveis e os
 * valores-padrao (pre-selecao dos interruptores) de cada perfil.
 * Disponivel a quem pode criar utilizadores (CRIAR em UTILIZADORES).
 */
@RestController
@RequestMapping("/api/permissoes")
public class PermissaoController {

    private final MatrizPermissoes matriz;

    public PermissaoController(MatrizPermissoes matriz) {
        this.matriz = matriz;
    }

    @GetMapping
    @PreAuthorize("@permissoes.pode('UTILIZADORES','CRIAR')")
    public Map<String, Object> obterMatriz() {
        // Colunas: as 4 acoes (codigo + etiqueta legivel)
        List<Map<String, String>> acoes = new ArrayList<>();
        for (Acao a : Acao.values()) {
            Map<String, String> col = new LinkedHashMap<>();
            col.put("codigo", a.name());
            col.put("etiqueta", a.getEtiqueta());
            acoes.add(col);
        }

        // Uma linha por funcionalidade, com as acoes-padrao de cada perfil
        List<Map<String, Object>> funcionalidades = new ArrayList<>();
        for (Funcionalidade f : Funcionalidade.values()) {
            Map<String, List<String>> perfis = new LinkedHashMap<>();
            for (NomePerfil p : NomePerfil.values()) {
                List<String> nomes = new ArrayList<>();
                for (Acao a : matriz.acoesPadrao(f, p)) {
                    nomes.add(a.name());
                }
                perfis.put(p.name(), nomes);
            }
            Map<String, Object> linha = new LinkedHashMap<>();
            linha.put("funcionalidade", f.name());
            linha.put("etiqueta", f.getEtiqueta());
            linha.put("perfis", perfis);
            funcionalidades.add(linha);
        }

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("acoes", acoes);
        resposta.put("funcionalidades", funcionalidades);
        return resposta;
    }
}
