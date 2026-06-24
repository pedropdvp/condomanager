package com.condomanager.service;

import com.condomanager.dto.DespesaCreateDTO;
import com.condomanager.dto.DespesaResponse;
import com.condomanager.dto.DespesaUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.DespesaMapper;
import com.condomanager.model.CategoriaDespesa;
import com.condomanager.model.Condominio;
import com.condomanager.model.Despesa;
import com.condomanager.model.EstadoDespesa;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.DespesaRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio das despesas. Garante que o condomínio pertence ao tenant.
 */
@Service
public class DespesaService {

    private static final String RECURSO = "Despesa";

    private static final Logger logger = LoggerFactory.getLogger(DespesaService.class);

    private final DespesaRepository repository;
    private final CondominioRepository condominioRepository;
    private final DespesaMapper mapper;

    public DespesaService(DespesaRepository repository,
                          CondominioRepository condominioRepository,
                          DespesaMapper mapper) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.mapper = mapper;
    }

    @Transactional
    public DespesaResponse criar(DespesaCreateDTO dto) {
        Condominio condominio = condominioDoTenant(dto.condominioId());
        Despesa guardada = repository.save(mapper.toEntity(dto, condominio));
        logger.info("Despesa criada: id={}, id_condominio={}, valor={}",
                guardada.getId(), condominio.getId(), guardada.getValor());
        return mapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public Page<DespesaResponse> listar(Long condominioId, CategoriaDespesa categoria,
                                        EstadoDespesa estado, Pageable pageable) {
        Page<Despesa> pagina;
        if (condominioId != null && categoria != null) {
            pagina = repository.findByCondominio_IdAndCategoria(condominioId, categoria, pageable);
        } else if (condominioId != null && estado != null) {
            pagina = repository.findByCondominio_IdAndEstado(condominioId, estado, pageable);
        } else if (condominioId != null) {
            pagina = repository.findByCondominio_Id(condominioId, pageable);
        } else if (estado != null) {
            pagina = repository.findByEstado(estado, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional
    public DespesaResponse aprovar(Long id) {
        return decidir(id, EstadoDespesa.APROVADA);
    }

    @Transactional
    public DespesaResponse rejeitar(Long id) {
        return decidir(id, EstadoDespesa.REJEITADA);
    }

    private DespesaResponse decidir(Long id, EstadoDespesa novoEstado) {
        Despesa despesa = obterDoTenant(id);
        if (despesa.getEstado() != EstadoDespesa.PENDENTE) {
            throw new IllegalArgumentException(
                    "A despesa já foi decidida (" + despesa.getEstado() + ") e não pode ser alterada.");
        }
        despesa.setEstado(novoEstado);
        logger.info("Despesa id={} -> {}", id, novoEstado);
        return mapper.toResponse(despesa);
    }

    @Transactional(readOnly = true)
    public DespesaResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public DespesaResponse atualizar(Long id, DespesaUpdateDTO dto) {
        Despesa despesa = obterDoTenant(id);
        mapper.aplicarAtualizacao(despesa, dto);
        logger.info("Despesa atualizada: id={}", id);
        return mapper.toResponse(despesa);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obterDoTenant(id));
        logger.info("Despesa eliminada: id={}", id);
    }

    private Despesa obterDoTenant(Long id) {
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
