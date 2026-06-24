package com.condomanager.controller;

import com.condomanager.dto.DocumentoResponse;
import com.condomanager.dto.PageResponse;
import com.condomanager.service.DocumentoService;
import com.condomanager.service.DocumentoService.ConteudoDocumento;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Gestão documental: upload, download, pesquisa e remoção, no âmbito do tenant.
 */
@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<DocumentoResponse> upload(@RequestParam Long condominioId,
                                                    @RequestParam String nome,
                                                    @RequestParam(required = false) String tipo,
                                                    @RequestParam("ficheiro") MultipartFile ficheiro,
                                                    UriComponentsBuilder uriBuilder) {
        if (ficheiro.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O ficheiro é obrigatório e não pode ser vazio.");
        }
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome é obrigatório.");
        }
        DocumentoResponse criado = service.criar(condominioId, nome, tipo, ficheiro);
        URI location = uriBuilder.path("/api/v1/documentos/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public PageResponse<DocumentoResponse> listar(@RequestParam(required = false) Long condominioId,
                                                  @RequestParam(required = false) String nome,
                                                  Pageable pageable) {
        return PageResponse.de(service.listar(condominioId, nome, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public DocumentoResponse obter(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO', 'ADMIN_CONDOMINIO', 'CONDOMINO')")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        ConteudoDocumento conteudo = service.descarregar(id);
        String nomeFicheiro = StringUtils.cleanPath(conteudo.nome());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeFicheiro + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(conteudo.recurso());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTOR_EMPRESA', 'FUNCIONARIO')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
