package com.condomanager.controller;

import com.condomanager.dto.DocumentoDTO;
import com.condomanager.service.DocumentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("@permissoes.pode('DOCUMENTOS','CONSULTAR')")
    public List<DocumentoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissoes.pode('DOCUMENTOS','CONSULTAR')")
    public DocumentoDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @PostMapping
    @PreAuthorize("@permissoes.pode('DOCUMENTOS','CRIAR')")
    public ResponseEntity<DocumentoDTO> registar(@Valid @RequestBody DocumentoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registar(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissoes.pode('DOCUMENTOS','APAGAR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
