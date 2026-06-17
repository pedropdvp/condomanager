package com.condomanager.controller;

import com.condomanager.dto.EmpresaDTO;
import com.condomanager.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("@permissoes.pode('EMPRESAS','CONSULTAR')")
    public List<EmpresaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissoes.pode('EMPRESAS','CONSULTAR')")
    public EmpresaDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("@permissoes.pode('EMPRESAS','CRIAR')")
    public ResponseEntity<EmpresaDTO> criar(@Valid @RequestBody EmpresaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissoes.pode('EMPRESAS','EDITAR')")
    public EmpresaDTO atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissoes.pode('EMPRESAS','APAGAR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
