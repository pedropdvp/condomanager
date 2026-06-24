package com.condomanager.service;

import com.condomanager.dto.CondominoCreateDTO;
import com.condomanager.dto.CondominoResponse;
import com.condomanager.dto.CondominoUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.CondominoMapper;
import com.condomanager.model.Condomino;
import com.condomanager.model.Fracao;
import com.condomanager.repository.CondominoRepository;
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
 * Regras de negócio dos condóminos. Garante que a fração-pai pertence ao tenant.
 */
@Service
public class CondominoService {

    private static final String RECURSO = "Condómino";

    private static final Logger logger = LoggerFactory.getLogger(CondominoService.class);

    private final CondominoRepository repository;
    private final FracaoRepository fracaoRepository;
    private final CondominoMapper mapper;

    public CondominoService(CondominoRepository repository,
                            FracaoRepository fracaoRepository,
                            CondominoMapper mapper) {
        this.repository = repository;
        this.fracaoRepository = fracaoRepository;
        this.mapper = mapper;
    }

    @Transactional
    public CondominoResponse criar(CondominoCreateDTO dto) {
        Fracao fracao = fracaoDoTenant(dto.fracaoId());
        Condomino guardado = repository.save(mapper.toEntity(dto, fracao));
        logger.info("Condómino criado: id={}, id_fracao={}", guardado.getId(), fracao.getId());
        return mapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<CondominoResponse> listar(Long fracaoId, Pageable pageable) {
        Page<Condomino> pagina = (fracaoId != null)
                ? repository.findByFracao_Id(fracaoId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CondominoResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public CondominoResponse atualizar(Long id, CondominoUpdateDTO dto) {
        Condomino condomino = obterDoTenant(id);
        mapper.aplicarAtualizacao(condomino, dto);
        logger.info("Condómino atualizado: id={}", id);
        return mapper.toResponse(condomino);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obterDoTenant(id));
        logger.info("Condómino eliminado: id={}", id);
    }

    private Condomino obterDoTenant(Long id) {
        return repository.findByIdAndIdEmpresa(id, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }

    private Fracao fracaoDoTenant(Long fracaoId) {
        return fracaoRepository.findByIdAndIdEmpresa(fracaoId, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException("Fração", fracaoId));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
