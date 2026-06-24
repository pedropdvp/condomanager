package com.condomanager.service;

import com.condomanager.dto.FracaoCreateDTO;
import com.condomanager.dto.FracaoResponse;
import com.condomanager.dto.FracaoUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.FracaoMapper;
import com.condomanager.model.Edificio;
import com.condomanager.model.Fracao;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.repository.FracaoRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio das frações. Garante coerência tenant → condomínio → edifício.
 */
@Service
public class FracaoService {

    private static final String RECURSO = "Fração";

    private static final Logger logger = LoggerFactory.getLogger(FracaoService.class);

    private final FracaoRepository repository;
    private final EdificioRepository edificioRepository;
    private final FracaoMapper mapper;

    public FracaoService(FracaoRepository repository,
                         EdificioRepository edificioRepository,
                         FracaoMapper mapper) {
        this.repository = repository;
        this.edificioRepository = edificioRepository;
        this.mapper = mapper;
    }

    @Transactional
    public FracaoResponse criar(FracaoCreateDTO dto) {
        Edificio edificio = edificioDoTenant(dto.edificioId());
        if (!edificio.getCondominio().getId().equals(dto.condominioId())) {
            throw new IllegalArgumentException("O edifício não pertence ao condomínio indicado.");
        }
        Fracao guardada = repository.save(mapper.toEntity(dto, edificio.getCondominio(), edificio));
        logger.info("Fração criada: id={}, id_edificio={}", guardada.getId(), edificio.getId());
        return mapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public Page<FracaoResponse> listar(Long condominioId, Long edificioId, Pageable pageable) {
        Page<Fracao> pagina;
        if (edificioId != null) {
            pagina = repository.findByEdificio_Id(edificioId, pageable);
        } else if (condominioId != null) {
            pagina = repository.findByCondominio_Id(condominioId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public FracaoResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public FracaoResponse atualizar(Long id, FracaoUpdateDTO dto) {
        Fracao fracao = obterDoTenant(id);
        mapper.aplicarAtualizacao(fracao, dto);
        logger.info("Fração atualizada: id={}", id);
        return mapper.toResponse(fracao);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obterDoTenant(id));
        logger.info("Fração eliminada: id={}", id);
    }

    private Fracao obterDoTenant(Long id) {
        return repository.findByIdAndIdEmpresa(id, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }

    private Edificio edificioDoTenant(Long edificioId) {
        return edificioRepository.findByIdAndIdEmpresa(edificioId, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException("Edifício", edificioId));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
