package com.condomanager.controller;

import com.condomanager.dto.AtaDTO;
import com.condomanager.service.AtaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atas")
public class AtaController {

    private final AtaService service;

    public AtaController(AtaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("@permissoes.pode('ATAS','CONSULTAR')")
    public List<AtaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissoes.pode('ATAS','CONSULTAR')")
    public AtaDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("@permissoes.pode('ATAS','CRIAR')")
    public ResponseEntity<AtaDTO> criar(@Valid @RequestBody AtaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissoes.pode('ATAS','EDITAR')")
    public AtaDTO atualizar(@PathVariable Long id, @Valid @RequestBody AtaDTO dto) {
        return service.atualizar(id, dto);
    }

    @PatchMapping("/{id}/arquivar")
    @PreAuthorize("@permissoes.pode('ATAS','EDITAR')")
    public AtaDTO arquivar(@PathVariable Long id) {
        return service.arquivar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissoes.pode('ATAS','APAGAR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
