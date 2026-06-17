package com.condomanager.controller;

import com.condomanager.dto.DespesaDTO;
import com.condomanager.service.DespesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    private final DespesaService service;

    public DespesaController(DespesaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public List<DespesaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public DespesaDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public ResponseEntity<DespesaDTO> criar(@Valid @RequestBody DespesaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public DespesaDTO atualizar(@PathVariable Long id, @Valid @RequestBody DespesaDTO dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public DespesaDTO aprovar(@PathVariable Long id) {
        return service.aprovar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
