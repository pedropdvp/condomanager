package com.condomanager.service;

import com.condomanager.dto.CondominioCreateDTO;
import com.condomanager.dto.CondominioResponse;
import com.condomanager.dto.CondominioUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.CondominioMapper;
import com.condomanager.model.Condominio;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio dos condomínios.
 *
 * <p>O isolamento por empresa é garantido de duas formas complementares:</p>
 * <ul>
 *   <li>nas listagens, pelo filtro {@code tenantFilter} do Hibernate (aplicado às queries);</li>
 *   <li>nos acessos por id, por {@code findByIdAndIdEmpresa} (o filtro não cobre {@code em.find}).</li>
 * </ul>
 */
@Service
public class CondominioService {

    private static final String RECURSO = "Condomínio";

    private static final Logger logger = LoggerFactory.getLogger(CondominioService.class);

    private final CondominioRepository repository;
    private final CondominioMapper mapper;

    public CondominioService(CondominioRepository repository, CondominioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public CondominioResponse criar(CondominioCreateDTO dto) {
        tenantObrigatorio();
        // O id_empresa é preenchido automaticamente por TenantEntityListener (@PrePersist).
        Condominio guardado = repository.save(mapper.toEntity(dto));
        logger.info("Condomínio criado: id={}, id_empresa={}", guardado.getId(), guardado.getIdEmpresa());
        return mapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<CondominioResponse> listar(Pageable pageable) {
        // Filtrado automaticamente pelo tenantFilter do Hibernate.
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CondominioResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public CondominioResponse atualizar(Long id, CondominioUpdateDTO dto) {
        Condominio condominio = obterDoTenant(id);
        mapper.aplicarAtualizacao(condominio, dto);
        logger.info("Condomínio atualizado: id={}", id);
        return mapper.toResponse(condominio);
    }

    @Transactional
    public void eliminar(Long id) {
        Condominio condominio = obterDoTenant(id);
        repository.delete(condominio);
        logger.info("Condomínio eliminado: id={}", id);
    }

    private Condominio obterDoTenant(Long id) {
        Long tenant = tenantObrigatorio();
        return repository.findByIdAndIdEmpresa(id, tenant)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação sobre condomínios requer uma empresa associada.");
        }
        return tenant;
    }
}
