package com.condomanager.service;

import com.condomanager.dto.MensagemCreateDTO;
import com.condomanager.dto.MensagemResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.MensagemMapper;
import com.condomanager.model.Mensagem;
import com.condomanager.model.TipoMensagem;
import com.condomanager.model.Utilizador;
import com.condomanager.repository.MensagemRepository;
import com.condomanager.repository.UtilizadorRepository;
import com.condomanager.security.AuthenticatedUser;
import com.condomanager.security.SecurityUtils;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Regras de negócio da comunicação interna (mensagens individuais e broadcast).
 */
@Service
public class MensagemService {

    private static final String RECURSO = "Mensagem";

    private static final Logger logger = LoggerFactory.getLogger(MensagemService.class);

    private final MensagemRepository repository;
    private final UtilizadorRepository utilizadorRepository;
    private final MensagemMapper mapper;

    public MensagemService(MensagemRepository repository,
                           UtilizadorRepository utilizadorRepository,
                           MensagemMapper mapper) {
        this.repository = repository;
        this.utilizadorRepository = utilizadorRepository;
        this.mapper = mapper;
    }

    @Transactional
    public MensagemResponse enviar(MensagemCreateDTO dto) {
        Long tenant = tenantObrigatorio();
        Long uid = utilizadorAtualId();
        Utilizador origem = utilizadorRepository.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador", uid));

        Mensagem mensagem = new Mensagem();
        mensagem.setTipo(dto.tipo());
        mensagem.setAssunto(dto.assunto());
        mensagem.setConteudo(dto.conteudo());
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagem.setOrigem(origem);

        if (dto.tipo() == TipoMensagem.INDIVIDUAL) {
            if (dto.destinoId() == null) {
                throw new IllegalArgumentException("O destino é obrigatório numa mensagem individual.");
            }
            Utilizador destino = utilizadorRepository.findById(dto.destinoId())
                    .filter(u -> tenant.equals(u.getIdEmpresa()))
                    .orElseThrow(() -> new ResourceNotFoundException("Utilizador destino", dto.destinoId()));
            mensagem.setDestino(destino);
        } else if (dto.tipo() == TipoMensagem.GRUPO) {
            if (dto.destinatarios() == null || dto.destinatarios().isEmpty()) {
                throw new IllegalArgumentException("Indique pelo menos um destinatário numa mensagem de grupo.");
            }
            for (Long destinatarioId : dto.destinatarios()) {
                Utilizador destinatario = utilizadorRepository.findById(destinatarioId)
                        .filter(u -> tenant.equals(u.getIdEmpresa()))
                        .orElseThrow(() -> new ResourceNotFoundException("Utilizador destinatário", destinatarioId));
                mensagem.getDestinatarios().add(destinatario.getId());
            }
        }

        Mensagem guardada = repository.save(mensagem);
        logger.info("Mensagem enviada: id={}, tipo={}, origem={}", guardada.getId(), dto.tipo(), uid);
        return mapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public Page<MensagemResponse> caixaDeEntrada(Pageable pageable) {
        tenantObrigatorio();
        return repository.caixaDeEntrada(utilizadorAtualId(), TipoMensagem.BROADCAST, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MensagemResponse> enviadas(Pageable pageable) {
        tenantObrigatorio();
        return repository.findByOrigem_Id(utilizadorAtualId(), pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MensagemResponse obterPorId(Long id) {
        Long tenant = tenantObrigatorio();
        Long uid = utilizadorAtualId();
        Mensagem mensagem = repository.findByIdAndIdEmpresa(id, tenant)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
        boolean acessivel = mensagem.getTipo() == TipoMensagem.BROADCAST
                || uid.equals(mensagem.getOrigem().getId())
                || (mensagem.getDestino() != null && uid.equals(mensagem.getDestino().getId()));
        if (!acessivel) {
            throw new ResourceNotFoundException(RECURSO, id);
        }
        return mapper.toResponse(mensagem);
    }

    @Transactional
    public MensagemResponse marcarComoLida(Long id) {
        Long tenant = tenantObrigatorio();
        Long uid = utilizadorAtualId();
        Mensagem mensagem = repository.findByIdAndIdEmpresa(id, tenant)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
        if (mensagem.getDestino() == null || !uid.equals(mensagem.getDestino().getId())) {
            throw new IllegalArgumentException("Só o destinatário pode marcar a mensagem como lida.");
        }
        mensagem.setLida(true);
        return mapper.toResponse(mensagem);
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas() {
        tenantObrigatorio();
        return repository.countByDestino_IdAndLidaFalse(utilizadorAtualId());
    }

    /** Apaga uma mensagem — só o remetente pode apagar as que enviou. */
    @Transactional
    public void eliminar(Long id) {
        Long uid = utilizadorAtualId();
        Mensagem mensagem = repository.findByIdAndIdEmpresa(id, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
        if (mensagem.getOrigem() == null || !uid.equals(mensagem.getOrigem().getId())) {
            throw new AccessDeniedException("Só pode apagar as mensagens que enviou.");
        }
        repository.delete(mensagem);
    }

    private Long utilizadorAtualId() {
        return SecurityUtils.utilizadorAtual()
                .map(AuthenticatedUser::id)
                .orElseThrow(() -> new AccessDeniedException("Sem utilizador autenticado."));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
