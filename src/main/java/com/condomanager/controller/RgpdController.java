package com.condomanager.controller;

import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.SecurityUtils;
import com.condomanager.service.UtilizadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RGPD — direitos do titular dos dados: acesso/portabilidade (exportar) e
 * apagamento (anonimizar a própria conta). Operam sempre sobre o utilizador autenticado.
 */
@RestController
@RequestMapping("/api/v1/rgpd")
@PreAuthorize("isAuthenticated()")
public class RgpdController {

    private final UtilizadorService utilizadorService;

    public RgpdController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    /** Exporta os dados pessoais do utilizador autenticado (direito de acesso/portabilidade). */
    @GetMapping("/meus-dados")
    public Map<String, Object> meusDados() {
        Map<String, Object> export = new LinkedHashMap<>();
        export.put("geradoEm", LocalDateTime.now().toString());
        export.put("aviso", "Exportação dos seus dados pessoais (RGPD).");
        export.put("utilizador", utilizadorService.obterPorId(idAtual()));
        return export;
    }

    /** Apaga (anonimiza) a conta do utilizador autenticado (direito ao apagamento). */
    @DeleteMapping("/minha-conta")
    public ResponseEntity<Void> apagarMinhaConta() {
        utilizadorService.anonimizar(idAtual());
        return ResponseEntity.noContent().build();
    }

    private Long idAtual() {
        return SecurityUtils.utilizadorAtual().map(AuthenticatedUser::id)
                .orElseThrow(() -> new AccessDeniedException("Sem sessão ativa."));
    }
}
