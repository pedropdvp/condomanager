package com.condomanager.service;

import com.condomanager.dto.AtribuirOcorrenciaDTO;
import com.condomanager.dto.OcorrenciaCreateDTO;
import com.condomanager.dto.OcorrenciaResponse;
import com.condomanager.dto.OcorrenciaUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.OcorrenciaMapper;
import com.condomanager.model.Condomino;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.model.Ocorrencia;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.OcorrenciaRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio das ocorrências: registo, atribuição e transições de estado.
 */
@Service
public class OcorrenciaService {

    private static final String RECURSO = "Ocorrência";

    private static final Logger logger = LoggerFactory.getLogger(OcorrenciaService.class);

    private final OcorrenciaRepository repository;
    private final CondominioRepository condominioRepository;
    private final CondominoRepository condominoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final OcorrenciaMapper mapper;

    public OcorrenciaService(OcorrenciaRepository repository,
                             CondominioRepository condominioRepository,
                             CondominoRepository condominoRepository,
                             UtilizadorRepository utilizadorRepository,
                             OcorrenciaMapper mapper) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.condominoRepository = condominoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.mapper = mapper;
    }

    @Transactional
    public OcorrenciaResponse criar(OcorrenciaCreateDTO dto) {
        Long tenant = tenantObrigatorio();
        Condominio condominio = condominioRepository.findByIdAndIdEmpresa(dto.condominioId(), tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", dto.condominioId()));

        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setCondominio(condominio);
        ocorrencia.setTitulo(dto.titulo());
        ocorrencia.setDescricao(dto.descricao());
        ocorrencia.setPrioridade(dto.prioridade());
        ocorrencia.setEstado(EstadoOcorrencia.ABERTA);

        if (dto.condominoId() != null) {
            Condomino condomino = condominoRepository.findByIdAndIdEmpresa(dto.condominoId(), tenant)
                    .orElseThrow(() -> new ResourceNotFoundException("Condómino", dto.condominoId()));
            ocorrencia.setCondomino(condomino);
        }

        Ocorrencia guardada = repository.save(ocorrencia);
        logger.info("Ocorrência criada: id={}, id_condominio={}", guardada.getId(), condominio.getId());
        return mapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public Page<OcorrenciaResponse> listar(Long condominioId, EstadoOcorrencia estado, Pageable pageable) {
        Page<Ocorrencia> pagina;
        if (condominioId != null && estado != null) {
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

    @Transactional(readOnly = true)
    public OcorrenciaResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional
    public OcorrenciaResponse atualizar(Long id, OcorrenciaUpdateDTO dto) {
        Ocorrencia o = obterDoTenant(id);
        o.setTitulo(dto.titulo());
        o.setDescricao(dto.descricao());
        o.setPrioridade(dto.prioridade());
        return mapper.toResponse(o);
    }

    @Transactional
    public OcorrenciaResponse atribuir(Long id, AtribuirOcorrenciaDTO dto) {
        Long tenant = tenantObrigatorio();
        Ocorrencia o = obterDoTenant(id);
        if (o.getEstado().isTerminal()) {
            throw new IllegalArgumentException("Não é possível atribuir uma ocorrência " + o.getEstado() + ".");
        }
        Utilizador responsavel = utilizadorRepository.findById(dto.utilizadorId())
                .filter(u -> tenant.equals(u.getIdEmpresa()))
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", dto.utilizadorId()));
        o.setResponsavel(responsavel);
        if (o.getEstado() == EstadoOcorrencia.ABERTA) {
            o.setEstado(EstadoOcorrencia.EM_ANALISE);
        }
        logger.info("Ocorrência id={} atribuída a utilizador={}", id, responsavel.getId());
        return mapper.toResponse(o);
    }

    @Transactional
    public OcorrenciaResponse alterarEstado(Long id, EstadoOcorrencia novoEstado) {
        Ocorrencia o = obterDoTenant(id);
        if (o.getEstado().isTerminal()) {
            throw new IllegalArgumentException("A ocorrência está " + o.getEstado() + " e não pode mudar de estado.");
        }
        o.setEstado(novoEstado);
        logger.info("Ocorrência id={} -> estado {}", id, novoEstado);
        return mapper.toResponse(o);
    }

    private Ocorrencia obterDoTenant(Long id) {
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
