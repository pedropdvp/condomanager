package com.condomanager.controller;

import com.condomanager.dto.PagamentoResponse;
import com.condomanager.dto.ReferenciaPagamentoResponse;
import com.condomanager.service.PagamentoOnlineService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pagamento online de quotas (scaffolding — ver {@link PagamentoOnlineService}).
 */
@RestController
@RequestMapping("/api/v1/pagamentos/online")
public class PagamentoOnlineController {

    private final PagamentoOnlineService service;

    public PagamentoOnlineController(PagamentoOnlineService service) {
        this.service = service;
    }

    /** Gera a referência de pagamento (simulada) para a quota. */
    @PostMapping("/iniciar")
    @PreAuthorize("@permissaoService.pode('PAGAMENTOS', 'CRIAR')")
    public ReferenciaPagamentoResponse iniciar(@RequestParam Long quotaId) {
        return service.iniciar(quotaId);
    }

    /** Simula o callback do gateway, registando o pagamento (marca a quota como paga). */
    @PostMapping("/confirmar")
    @PreAuthorize("@permissaoService.pode('PAGAMENTOS', 'CRIAR')")
    public PagamentoResponse confirmar(@RequestParam Long quotaId) {
        return service.confirmar(quotaId);
    }
}
