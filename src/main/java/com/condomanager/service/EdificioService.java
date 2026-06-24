package com.condomanager.service;

import com.condomanager.dto.EdificioCreateDTO;
import com.condomanager.dto.EdificioResponse;
import com.condomanager.dto.EdificioUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.EdificioMapper;
import com.condomanager.model.Condominio;
import com.condomanager.model.Edificio;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.EdificioRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio dos edifícios. Garante que o condomínio-pai pertence ao tenant.
 */
@Service
public class EdificioService {

    private static final String RECURSO = "Edifício";

    private static final Logger logger = LoggerFactory.getLogger(EdificioService.class);

    private final EdificioRepository repository;
    private final CondominioRepository condominioRepository;
    private final EdificioMapper mapper;

    public EdificioService(EdificioRepository repository,
                           CondominioRepository condominioRepository,
                           EdificioMapper mapper) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EdificioResponse criar(EdificioCreateDTO dto) {
        Condominio condominio = condominioDoTenant(dto.condominioId());
        Edificio guardado = repository.save(mapper.toEntity(dto, condominio));
        logger.info("Edifício criado: id={}, id_condominio={}", guardado.getId(), condominio.getId());
        return mapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<EdificioResponse> listar(Long condominioId, Pageable pageable) {
        Page<Edificio> pagina = (condominioId != null)
                ? repository.findByCondominio_Id(condominioId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EdificioResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public EdificioResponse atualizar(Long id, EdificioUpdateDTO dto) {
        Edificio edificio = obterDoTenant(id);
        mapper.aplicarAtualizacao(edificio, dto);
        logger.info("Edifício atualizado: id={}", id);
        return mapper.toResponse(edificio);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obterDoTenant(id));
        logger.info("Edifício eliminado: id={}", id);
    }

    private Edificio obterDoTenant(Long id) {
        return repository.findByIdAndIdEmpresa(id, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }

    private Condominio condominioDoTenant(Long condominioId) {
        return condominioRepository.findByIdAndIdEmpresa(condominioId, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", condominioId));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
