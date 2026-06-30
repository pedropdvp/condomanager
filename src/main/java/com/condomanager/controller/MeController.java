package com.condomanager.controller;

import com.condomanager.dto.ContextoCondominoResponse;
import com.condomanager.dto.QuotaResponse;
import com.condomanager.service.MeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Portal do Condómino — dados self-service do condómino autenticado.
 */
@RestController
@RequestMapping("/api/v1/me")
@PreAuthorize("isAuthenticated()")
public class MeController {

    private final MeService service;

    public MeController(MeService service) {
        this.service = service;
    }

    /** Contexto do condómino (fração e condomínio a que pertence). */
    @GetMapping("/contexto")
    public ContextoCondominoResponse contexto() {
        return service.contexto();
    }

    /** Quotas da fração do condómino autenticado. */
    @GetMapping("/quotas")
    public List<QuotaResponse> quotas() {
        return service.minhasQuotas();
    }
}
