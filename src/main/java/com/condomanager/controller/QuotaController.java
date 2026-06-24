package com.condomanager.controller;

import com.condomanager.dto.GeracaoQuotasResultado;
import com.condomanager.dto.GerarQuotasDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.dto.QuotaResponse;
import com.condomanager.model.EstadoQuota;
import com.condomanager.service.QuotaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Gestão de quotas no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/quotas")
public class QuotaController {

    private final QuotaService service;

    public QuotaController(QuotaService service) {
        this.service = service;
    }

    @PostMapping("/gerar")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<GeracaoQuotasResultado> gerar(@Valid @RequestBody GerarQuotasDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerar(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public PageResponse<QuotaResponse> listar(@RequestParam(required = false) Long condominioId,
                                              @RequestParam(required = false) Long fracaoId,
                                              @RequestParam(required = false) EstadoQuota estado,
                                              Pageable pageable) {
        return PageResponse.de(service.listar(condominioId, fracaoId, estado, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public QuotaResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PostMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public QuotaResponse anular(@PathVariable Long id) {
        return service.anular(id);
    }
}
