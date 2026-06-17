package com.condomanager.controller;

import com.condomanager.dto.CondominoDTO;
import com.condomanager.service.CondominoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/condominos")
public class CondominoController {

    private final CondominoService service;

    public CondominoController(CondominoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("@permissoes.pode('CONDOMINOS','CONSULTAR')")
    public List<CondominoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissoes.pode('CONDOMINOS','CONSULTAR')")
    public CondominoDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("@permissoes.pode('CONDOMINOS','CRIAR')")
    public ResponseEntity<CondominoDTO> criar(@Valid @RequestBody CondominoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissoes.pode('CONDOMINOS','EDITAR')")
    public CondominoDTO atualizar(@PathVariable Long id, @Valid @RequestBody CondominoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissoes.pode('CONDOMINOS','APAGAR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
