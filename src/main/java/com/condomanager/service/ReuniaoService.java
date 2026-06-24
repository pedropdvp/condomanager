package com.condomanager.service;

import com.condomanager.dto.ConvocatoriaResponse;
import com.condomanager.dto.ReuniaoCreateDTO;
import com.condomanager.dto.ReuniaoResponse;
import com.condomanager.dto.ReuniaoUpdateDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.ReuniaoMapper;
import com.condomanager.model.Condomino;
import com.condomanager.model.Condominio;
import com.condomanager.model.EstadoReuniao;
import com.condomanager.model.Reuniao;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.ReuniaoRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio das reuniões, incluindo transições de estado e convocatória.
 */
@Service
public class ReuniaoService {

    private static final String RECURSO = "Reunião";

    private static final Logger logger = LoggerFactory.getLogger(ReuniaoService.class);

    private final ReuniaoRepository repository;
    private final CondominioRepository condominioRepository;
    private final CondominoRepository condominoRepository;
    private final EmailService emailService;
    private final ReuniaoMapper mapper;

    public ReuniaoService(ReuniaoRepository repository,
                          CondominioRepository condominioRepository,
                          CondominoRepository condominoRepository,
                          EmailService emailService,
                          ReuniaoMapper mapper) {
        this.repository = repository;
        this.condominioRepository = condominioRepository;
        this.condominoRepository = condominoRepository;
        this.emailService = emailService;
        this.mapper = mapper;
    }

    @Transactional
    public ReuniaoResponse criar(ReuniaoCreateDTO dto) {
        Long tenant = tenantObrigatorio();
        Condominio condominio = condominioRepository.findByIdAndIdEmpresa(dto.condominioId(), tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", dto.condominioId()));
        Reuniao guardada = repository.save(mapper.toEntity(dto, condominio));
        logger.info("Reunião agendada: id={}, id_condominio={}, data={}",
                guardada.getId(), condominio.getId(), guardada.getData());
        return mapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public Page<ReuniaoResponse> listar(Long condominioId, EstadoReuniao estado, Pageable pageable) {
        Page<Reuniao> pagina;
        if (condominioId != null && estado != null) {
            pagina = repository.findByCondominio_IdAndEstado(condominioId, estado, pageable);
        } else if (condominioId != null) {
            pagina = repository.findByCondominio_Id(condominioId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ReuniaoResponse obterPorId(Long id) {
        return mapper.toResponse(obterDoTenant(id));
    }

    @Transactional(readOnly = true)
    public ConvocatoriaResponse convocatoria(Long id) {
        return mapper.toConvocatoria(obterDoTenant(id));
    }

    /**
     * Envia a convocatória por email a todos os condóminos do condomínio da reunião.
     *
     * @return número de emails enviados
     */
    @Transactional(readOnly = true)
    public int convocar(Long id) {
        Reuniao reuniao = obterDoTenant(id);
        Condominio condominio = reuniao.getCondominio();
        String assunto = "Convocatória de Assembleia — " + condominio.getNome();
        String corpo = "Convoca-se a assembleia do condomínio para:\n"
                + "Data: " + reuniao.getData()
                + (reuniao.getHora() != null ? "  Hora: " + reuniao.getHora() : "") + "\n"
                + "Local: " + (reuniao.getLocal() != null ? reuniao.getLocal() : "(a definir)") + "\n\n"
                + "Ordem de trabalhos:\n" + (reuniao.getOrdemTrabalhos() != null ? reuniao.getOrdemTrabalhos() : "(a definir)")
                + "\n\nNos termos da Lei 8/2022, a convocatória é feita com pelo menos 10 dias de antecedência.";

        int enviados = 0;
        for (Condomino condomino : condominoRepository.findByFracao_Condominio_Id(condominio.getId())) {
            if (condomino.getEmail() != null && !condomino.getEmail().isBlank()) {
                emailService.enviar(condomino.getEmail(), assunto, corpo);
                enviados++;
            }
        }
        logger.info("Convocatória da reunião id={} enviada a {} condóminos", id, enviados);
        return enviados;
    }

    @Transactional
    public ReuniaoResponse atualizar(Long id, ReuniaoUpdateDTO dto) {
        Reuniao reuniao = obterDoTenant(id);
        if (reuniao.getEstado() != EstadoReuniao.AGENDADA) {
            throw new IllegalArgumentException("Só é possível editar uma reunião AGENDADA.");
        }
        mapper.aplicarAtualizacao(reuniao, dto);
        logger.info("Reunião atualizada: id={}", id);
        return mapper.toResponse(reuniao);
    }

    @Transactional
    public ReuniaoResponse marcarRealizada(Long id) {
        return transitar(id, EstadoReuniao.REALIZADA);
    }

    @Transactional
    public ReuniaoResponse cancelar(Long id) {
        return transitar(id, EstadoReuniao.CANCELADA);
    }

    private ReuniaoResponse transitar(Long id, EstadoReuniao novo) {
        Reuniao reuniao = obterDoTenant(id);
        if (reuniao.getEstado() != EstadoReuniao.AGENDADA) {
            throw new IllegalArgumentException(
                    "Transição inválida: a reunião está " + reuniao.getEstado() + ".");
        }
        reuniao.setEstado(novo);
        logger.info("Reunião id={} -> {}", id, novo);
        return mapper.toResponse(reuniao);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obterDoTenant(id));
        logger.info("Reunião eliminada: id={}", id);
    }

    private Reuniao obterDoTenant(Long id) {
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
