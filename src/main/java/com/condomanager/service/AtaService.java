package com.condomanager.service;

import com.condomanager.dto.AtaCreateDTO;
import com.condomanager.dto.AtaResponse;
import com.condomanager.dto.AtaUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.AtaMapper;
import com.condomanager.model.Ata;
import com.condomanager.repository.AtaRepository;
import com.condomanager.repository.ReuniaoRepository;
import com.condomanager.security.TenantContext;
import com.condomanager.service.DocumentoService.ConteudoDocumento;
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
 * Regras de negócio das atas, incluindo o anexo opcional de ficheiro (arquivo).
 */
@Service
public class AtaService {

    private static final String RECURSO = "Ata";

    private static final Logger logger = LoggerFactory.getLogger(AtaService.class);

    private final AtaRepository repository;
    private final ReuniaoRepository reuniaoRepository;
    private final FileStorageService storage;
    private final AtaMapper mapper;

    public AtaService(AtaRepository repository, ReuniaoRepository reuniaoRepository,
                      FileStorageService storage, AtaMapper mapper) {
        this.repository = repository;
        this.reuniaoRepository = reuniaoRepository;
        this.storage = storage;
        this.mapper = mapper;
    }

    @Transactional
    public AtaResponse criar(AtaCreateDTO dto) {
        Long tenant = tenantObrigatorio();
        validarReuniao(dto.reuniaoId(), tenant);
        Ata guardada = repository.save(mapper.toEntity(dto));
        logger.info("Ata criada: id={}", guardada.getId());
        return mapper.toResponse(guardada);
    }

    /** Garante que a reunião indicada (se houver) pertence ao tenant. */
    private void validarReuniao(Long reuniaoId, Long tenant) {
        if (reuniaoId != null
                && reuniaoRepository.findByIdAndIdEmpresa(reuniaoId, tenant).isEmpty()) {
            throw new ResourceNotFoundException("Reunião", reuniaoId);
        }
    }

    @Transactional(readOnly = true)
    public Page<AtaResponse> listar(Long reuniaoId, String titulo, Pageable pageable) {
        Page<Ata> pagina;
        if (reuniaoId != null) {
            pagina = repository.findByIdReuniao(reuniaoId, pageable);
        } else if (titulo != null && !titulo.isBlank()) {
            pagina = repository.findByTituloContainingIgnoreCase(titulo, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AtaResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public AtaResponse atualizar(Long id, AtaUpdateDTO dto) {
        Ata ata = obterDoTenant(id);
        mapper.aplicarAtualizacao(ata, dto);
        logger.info("Ata atualizada: id={}", id);
        return mapper.toResponse(ata);
    }

    @Transactional
    public AtaResponse anexarFicheiro(Long id, MultipartFile ficheiro) {
        Ata ata = obterDoTenant(id);
        String anterior = ata.getFicheiro();
        String caminho = storage.guardar(ficheiro, ata.getIdEmpresa());
        ata.setFicheiro(caminho);
        if (anterior != null) {
            storage.eliminar(anterior);
        }
        logger.info("Ficheiro anexado à ata id={}: {}", id, caminho);
        return mapper.toResponse(ata);
    }

    @Transactional(readOnly = true)
    public ConteudoDocumento descarregar(Long id) {
        Ata ata = obterDoTenant(id);
        if (ata.getFicheiro() == null) {
            throw new ResourceNotFoundException("Ficheiro da ata", id);
        }
        Resource recurso = storage.carregar(ata.getFicheiro());
        return new ConteudoDocumento(ata.getTitulo(), recurso);
    }

    @Transactional
    public void eliminar(Long id) {
        Ata ata = obterDoTenant(id);
        repository.delete(ata);
        if (ata.getFicheiro() != null) {
            storage.eliminar(ata.getFicheiro());
        }
        logger.info("Ata eliminada: id={}", id);
    }

    private Ata obterDoTenant(Long id) {
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
}
