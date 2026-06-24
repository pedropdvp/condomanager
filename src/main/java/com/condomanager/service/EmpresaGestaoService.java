package com.condomanager.service;

import com.condomanager.dto.EmpresaCreateDTO;
import com.condomanager.dto.EmpresaResponse;
import com.condomanager.dto.EmpresaUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.EmpresaMapper;
import com.condomanager.model.EmpresaGestao;
import com.condomanager.model.EstadoEmpresa;
import com.condomanager.repository.EmpresaGestaoRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio das empresas de gestão (tenants).
 *
 * <p>A criação e a listagem global são restringidas ao {@code ADMIN_SISTEMA} ao nível
 * do controlador. O acesso a uma empresa específica é validado por posse: um utilizador
 * não-administrador só pode aceder à sua própria empresa.</p>
 */
@Service
public class EmpresaGestaoService {

    private static final String RECURSO = "Empresa de gestão";

    private static final Logger logger = LoggerFactory.getLogger(EmpresaGestaoService.class);

    private final EmpresaGestaoRepository repository;
    private final EmpresaMapper mapper;

    public EmpresaGestaoService(EmpresaGestaoRepository repository, EmpresaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public EmpresaResponse criar(EmpresaCreateDTO dto) {
        if (repository.existsByNif(dto.nif())) {
            throw new IllegalArgumentException("Já existe uma empresa com o NIF " + dto.nif());
        }
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Já existe uma empresa com o email " + dto.email());
        }
        EmpresaGestao empresa = repository.save(mapper.toEntity(dto));
        logger.info("Empresa de gestão criada: id={}, nif={}", empresa.getId(), empresa.getNif());
        return mapper.toResponse(empresa);
    }

    @Transactional(readOnly = true)
    public Page<EmpresaResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse obterPorId(Long id) {
        EmpresaGestao empresa = obterComAcesso(id);
        return mapper.toResponse(empresa);
    }

    @Transactional
    public EmpresaResponse atualizar(Long id, EmpresaUpdateDTO dto) {
        EmpresaGestao empresa = obterComAcesso(id);
        mapper.aplicarAtualizacao(empresa, dto);
        logger.info("Empresa de gestão atualizada: id={}", id);
        return mapper.toResponse(empresa);
    }

    @Transactional
    public void desativar(Long id) {
        EmpresaGestao empresa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
        empresa.setEstado(EstadoEmpresa.INATIVO);
        logger.info("Empresa de gestão desativada: id={}", id);
    }

    /**
     * Obtém a empresa garantindo que o utilizador atual lhe pode aceder.
     * Um não-administrador só acede à sua própria empresa; caso contrário devolve-se
     * "não encontrado" para não revelar a existência de empresas de outros tenants.
     */
    private EmpresaGestao obterComAcesso(Long id) {
        if (!SecurityUtils.isAdminSistema()) {
            Long tenant = SecurityUtils.utilizadorAtual().map(AuthenticatedUser::idEmpresa).orElse(null);
            if (tenant == null || !tenant.equals(id)) {
                throw new ResourceNotFoundException(RECURSO, id);
            }
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }
}
