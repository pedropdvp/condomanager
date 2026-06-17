package com.condomanager.controller;

import com.condomanager.dto.OcorrenciaDTO;
import com.condomanager.model.enums.EstadoOcorrencia;
import com.condomanager.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public List<OcorrenciaDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public OcorrenciaDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    /** Qualquer perfil autenticado pode registar uma ocorrencia, incluindo o condomino (RF14). */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public ResponseEntity<OcorrenciaDTO> registar(@Valid @RequestBody OcorrenciaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registar(dto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO')")
    public OcorrenciaDTO alterarEstado(@PathVariable Long id, @RequestParam EstadoOcorrencia estado) {
        return service.alterarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        service.apagar(id);
        return ResponseEntity.noContent().build();
    }
}
