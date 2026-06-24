package com.condomanager.service;

import com.condomanager.dto.DocumentoResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.DocumentoMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.Documento;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DocumentoRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Regras de negócio dos documentos: metadados em BD, conteúdo no filesystem local.
 */
@Service
public class DocumentoService {

    private static final String RECURSO = "Documento";

    private static final Logger logger = LoggerFactory.getLogger(DocumentoService.class);

    private final DocumentoRepository repository;
    private final CondominioRepository condominioRepository;
    private final FileStorageService storage;
    private final DocumentoMapper mapper;

    public DocumentoService(DocumentoRepository repository,
                            CondominioRepository condominioRepository,
                            FileStorageService storage,
                            DocumentoMapper mapper) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.storage = storage;
        this.mapper = mapper;
    }

    @Transactional
    public DocumentoResponse criar(Long condominioId, String nome, String tipo, MultipartFile ficheiro) {
        Long tenant = tenantObrigatorio();
        Condominio condominio = condominioRepository.findByIdAndIdEmpresa(condominioId, tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", condominioId));

        String caminho = storage.guardar(ficheiro, tenant);

        Documento documento = new Documento();
        documento.setCondominio(condominio);
        documento.setNome(nome);
        documento.setTipo(tipo);
        documento.setFicheiro(caminho);
        Documento guardado = repository.save(documento);

        logger.info("Documento criado: id={}, id_condominio={}, ficheiro={}",
                guardado.getId(), condominio.getId(), caminho);
        return mapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<DocumentoResponse> listar(Long condominioId, String nome, Pageable pageable) {
        Page<Documento> pagina;
        if (condominioId != null && nome != null && !nome.isBlank()) {
            pagina = repository.findByCondominio_IdAndNomeContainingIgnoreCase(condominioId, nome, pageable);
        } else if (condominioId != null) {
            pagina = repository.findByCondominio_Id(condominioId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public DocumentoResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional(readOnly = true)
    public ConteudoDocumento descarregar(Long id) {
        Documento documento = obterDoTenant(id);
        Resource recurso = storage.carregar(documento.getFicheiro());
        return new ConteudoDocumento(documento.getNome(), recurso);
    }

    @Transactional
    public void eliminar(Long id) {
        Documento documento = obterDoTenant(id);
        repository.delete(documento);
        storage.eliminar(documento.getFicheiro());
        logger.info("Documento eliminado: id={}", id);
    }

    private Documento obterDoTenant(Long id) {
        return repository.findByIdAndIdEmpresa(id, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }

    /** Conteúdo descarregável de um documento. */
    public record ConteudoDocumento(String nome, Resource recurso) {
    }
}
