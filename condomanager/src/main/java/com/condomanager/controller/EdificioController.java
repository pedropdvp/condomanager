package com.condomanager.controller;

import com.condomanager.dto.EdificioDTO;
import com.condomanager.service.EdificioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edificios")
@PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO')")
public class EdificioController {

    private final EdificioService service;

    public EdificioController(EdificioService service) {
        this.service = service;
    }

    @GetMapping
    public List<EdificioDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public EdificioDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    public ResponseEntity<EdificioDTO> criar(@Valid @RequestBody EdificioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public EdificioDTO atualizar(@PathVariable Long id, @Valid @RequestBody EdificioDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
