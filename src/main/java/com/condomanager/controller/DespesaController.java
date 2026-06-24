package com.condomanager.controller;

import com.condomanager.dto.DespesaCreateDTO;
import com.condomanager.dto.DespesaResponse;
import com.condomanager.dto.DespesaUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.model.CategoriaDespesa;
import com.condomanager.model.EstadoDespesa;
import com.condomanager.service.DespesaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Gestão de despesas no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/despesas")
public class DespesaController {

    private final DespesaService service;

    public DespesaController(DespesaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<DespesaResponse> criar(@Valid @RequestBody DespesaCreateDTO dto,
                                                 UriComponentsBuilder uriBuilder) {
        DespesaResponse criada = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/despesas/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public PageResponse<DespesaResponse> listar(@RequestParam(required = false) Long condominioId,
                                                @RequestParam(required = false) CategoriaDespesa categoria,
                                                @RequestParam(required = false) EstadoDespesa estado,
                                                Pageable pageable) {
        return PageResponse.de(service.listar(condominioId, categoria, estado, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public DespesaResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public DespesaResponse atualizar(@PathVariable Long id, @Valid @RequestBody DespesaUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'ADMIN_CONDOMINIO')")
    public DespesaResponse aprovar(@PathVariable Long id) {
        return service.aprovar(id);
    }

    @PostMapping("/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'ADMIN_CONDOMINIO')")
    public DespesaResponse rejeitar(@PathVariable Long id) {
        return service.rejeitar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
