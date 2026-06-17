package com.condomanager.controller;

import com.condomanager.dto.CondominioDTO;
import com.condomanager.service.CondominioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/condominios")
public class CondominioController {

    private final CondominioService service;

    public CondominioController(CondominioService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("@permissoes.pode('CONDOMINIOS','CONSULTAR')")
    public List<CondominioDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissoes.pode('CONDOMINIOS','CONSULTAR')")
    public CondominioDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("@permissoes.pode('CONDOMINIOS','CRIAR')")
    public ResponseEntity<CondominioDTO> criar(@Valid @RequestBody CondominioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissoes.pode('CONDOMINIOS','EDITAR')")
    public CondominioDTO atualizar(@PathVariable Long id, @Valid @RequestBody CondominioDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissoes.pode('CONDOMINIOS','APAGAR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
