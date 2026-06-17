package com.condomanager.controller;

import com.condomanager.dto.ResultadoVotacaoDTO;
import com.condomanager.dto.VotacaoDTO;
import com.condomanager.dto.VotoDTO;
import com.condomanager.service.VotacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votacoes")
public class VotacaoController {

    private final VotacaoService service;

    public VotacaoController(VotacaoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public List<VotacaoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public VotacaoDTO obter(@PathVariable Long id) {
        return service.obter(id);
    }

    @GetMapping("/{id}/resultado")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public ResultadoVotacaoDTO resultado(@PathVariable Long id) {
        return service.contar(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO')")
    public ResponseEntity<VotacaoDTO> criar(@Valid @RequestBody VotacaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PatchMapping("/{id}/encerrar")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO')")
    public VotacaoDTO encerrar(@PathVariable Long id) {
        return service.encerrar(id);
    }

    /** O condomino participa votando (RF12 / matriz: Condomino "Participa"). */
    @PostMapping("/votar")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public ResultadoVotacaoDTO votar(@Valid @RequestBody VotoDTO dto) {
        return service.votar(dto);
    }
}
