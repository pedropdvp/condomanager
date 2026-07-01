package com.condomanager.controller;

import com.condomanager.dto.PageResponse;
import com.condomanager.dto.ResultadoVotacaoResponse;
import com.condomanager.dto.VotacaoCreateDTO;
import com.condomanager.dto.VotacaoResponse;
import com.condomanager.dto.VotoCreateDTO;
import com.condomanager.dto.VotoProprioDTO;
import com.condomanager.dto.VotoResponse;
import com.condomanager.model.EstadoVotacao;
import com.condomanager.service.VotacaoService;
import com.condomanager.service.VotoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Gestão de votações e respetivos votos, no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/votacoes")
public class VotacaoController {

    private final VotacaoService votacaoService;
    private final VotoService votoService;

    public VotacaoController(VotacaoService votacaoService, VotoService votoService) {
        this.votacaoService = votacaoService;
        this.votoService = votoService;
    }

    @PostMapping
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'CRIAR')")
    public ResponseEntity<VotacaoResponse> criar(@Valid @RequestBody VotacaoCreateDTO dto,
                                                 UriComponentsBuilder uriBuilder) {
        VotacaoResponse criada = votacaoService.criar(dto);
        URI location = uriBuilder.path("/api/v1/votacoes/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'CONSULTAR')")
    public PageResponse<VotacaoResponse> listar(@RequestParam(required = false) Long reuniaoId,
                                                @RequestParam(required = false) EstadoVotacao estado,
                                                Pageable pageable) {
        return PageResponse.de(votacaoService.listar(reuniaoId, estado, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'CONSULTAR')")
    public VotacaoResponse obter(@PathVariable Long id) {
        return votacaoService.obterPorId(id);
    }

    @PostMapping("/{id}/abrir")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'EDITAR')")
    public VotacaoResponse abrir(@PathVariable Long id) {
        return votacaoService.abrir(id);
    }

    @PostMapping("/{id}/encerrar")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'EDITAR')")
    public VotacaoResponse encerrar(@PathVariable Long id) {
        return votacaoService.encerrar(id);
    }

    @GetMapping("/{id}/resultado")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'CONSULTAR')")
    public ResultadoVotacaoResponse resultado(@PathVariable Long id) {
        return votacaoService.resultado(id);
    }

    @PostMapping("/{id}/votos")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'CRIAR')")
    public ResponseEntity<VotoResponse> votar(@PathVariable Long id, @Valid @RequestBody VotoCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votar(id, dto));
    }

    /** Voto do próprio condómino autenticado. */
    @PostMapping("/{id}/votar")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'CRIAR')")
    public ResponseEntity<VotoResponse> votarProprio(@PathVariable Long id,
                                                     @Valid @RequestBody VotoProprioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votarProprio(id, dto));
    }

    @GetMapping("/{id}/votos")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'CONSULTAR')")
    public PageResponse<VotoResponse> listarVotos(@PathVariable Long id, Pageable pageable) {
        return PageResponse.de(votoService.listar(id, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissaoService.pode('VOTACOES', 'EDITAR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        votacaoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
