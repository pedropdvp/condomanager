package com.condomanager.controller;

import com.condomanager.dto.AlterarEstadoOcorrenciaDTO;
import com.condomanager.dto.AtribuirOcorrenciaDTO;
import com.condomanager.dto.OcorrenciaCreateDTO;
import com.condomanager.dto.OcorrenciaResponse;
import com.condomanager.dto.OcorrenciaUpdateDTO;
import com.condomanager.dto.PageResponse;
import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.service.OcorrenciaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Gestão de ocorrências no âmbito do tenant: registo, atribuição e estados.
 */
@RestController
@RequestMapping("/api/v1/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public ResponseEntity<OcorrenciaResponse> criar(@Valid @RequestBody OcorrenciaCreateDTO dto,
                                                    UriComponentsBuilder uriBuilder) {
        OcorrenciaResponse criada = service.criar(dto);
        URI location = uriBuilder.path("/api/v1/ocorrencias/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public PageResponse<OcorrenciaResponse> listar(@RequestParam(required = false) Long condominioId,
                                                   @RequestParam(required = false) EstadoOcorrencia estado,
                                                   Pageable pageable) {
        return PageResponse.de(service.listar(condominioId, estado, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public OcorrenciaResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public OcorrenciaResponse atualizar(@PathVariable Long id, @Valid @RequestBody OcorrenciaUpdateDTO dto) {
        return service.atualizar(id, dto);
    }

    @PostMapping("/{id}/atribuir")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public OcorrenciaResponse atribuir(@PathVariable Long id, @Valid @RequestBody AtribuirOcorrenciaDTO dto) {
        return service.atribuir(id, dto);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public OcorrenciaResponse alterarEstado(@PathVariable Long id,
                                            @Valid @RequestBody AlterarEstadoOcorrenciaDTO dto) {
        return service.alterarEstado(id, dto.estado());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
