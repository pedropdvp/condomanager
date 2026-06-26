package com.condomanager.service;

import com.condomanager.dto.LembreteResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Condomino;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.Quota;
import com.condomanager.repository.CondominioRepository;
import com.condomanager.repository.CondominoRepository;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Notificações automáticas — lembretes de quotas em atraso por email.
 *
 * <p>Pode ser desencadeado manualmente por um condomínio (gestor) ou de forma
 * agendada (a tarefa só corre se {@code app.notificacoes.lembretes.enabled=true}).</p>
 */
@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    private final QuotaRepository quotaRepository;
    private final CondominoRepository condominoRepository;
    private final CondominioRepository condominioRepository;
    private final EmailService emailService;
    private final boolean agendadoAtivo;

    public NotificacaoService(QuotaRepository quotaRepository,
                              CondominoRepository condominoRepository,
                              CondominioRepository condominioRepository,
                              EmailService emailService,
                              @Value("${app.notificacoes.lembretes.enabled:false}") boolean agendadoAtivo) {
        this.quotaRepository = quotaRepository;
        this.condominoRepository = condominoRepository;
        this.condominioRepository = condominioRepository;
        this.emailService = emailService;
        this.agendadoAtivo = agendadoAtivo;
    }

    /** Envio manual (gestor), validado pelo tenant. */
    @Transactional(readOnly = true)
    public LembreteResponse lembretesQuotasAtraso(Long condominioId) {
        condominioRepository.findByIdAndIdEmpresa(condominioId, tenantObrigatorio())
                .orElseThrow(() -> new ResourceNotFoundException("Condomínio", condominioId));
        return processarCondominio(condominioId);
    }

    /** Tarefa agendada (sem contexto de tenant): processa todos os condomínios com quotas em atraso. */
    @Scheduled(cron = "${app.notificacoes.cron:0 0 9 * * *}")
    @Transactional(readOnly = true)
    public void executarAgendado() {
        if (!agendadoAtivo) {
            return;
        }
        List<Long> condominios = quotaRepository.findByEstado(EstadoQuota.ATRASADO, Pageable.unpaged())
                .getContent().stream()
                .map(q -> q.getFracao().getCondominio().getId())
                .distinct()
                .toList();
        int total = 0;
        for (Long cid : condominios) {
            total += processarCondominio(cid).enviados();
        }
        logger.info("Lembretes agendados de quotas em atraso: {} emails em {} condomínios", total, condominios.size());
    }

    private LembreteResponse processarCondominio(Long condominioId) {
        Map<Long, List<Quota>> porFracao = quotaRepository.findByFracao_Condominio_Id(condominioId).stream()
                .filter(q -> q.getEstado() == EstadoQuota.ATRASADO)
                .collect(Collectors.groupingBy(q -> q.getFracao().getId()));

        int enviados = 0;
        List<String> destinatarios = new ArrayList<>();
        for (Condomino c : condominoRepository.findByFracao_Condominio_Id(condominioId)) {
            if (c.getEmail() == null || c.getEmail().isBlank() || c.getFracao() == null) {
                continue;
            }
            List<Quota> quotas = porFracao.get(c.getFracao().getId());
            if (quotas == null || quotas.isEmpty()) {
                continue;
            }
            BigDecimal total = quotas.stream().map(Quota::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
            String corpo = "Caro(a) " + c.getNome() + ",\n\n"
                    + "Constam " + quotas.size() + " quota(s) em atraso na sua fração, no valor total de "
                    + total + " €.\nAgradecemos a regularização da situação com a maior brevidade.\n\n— CondoManager";
            emailService.enviar(c.getEmail(), "Lembrete: quotas em atraso", corpo);
            enviados++;
            destinatarios.add(c.getNome());
        }
        logger.info("Lembretes de quotas em atraso (condomínio {}): {} emails enviados", condominioId, enviados);
        return new LembreteResponse(enviados, destinatarios);
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
