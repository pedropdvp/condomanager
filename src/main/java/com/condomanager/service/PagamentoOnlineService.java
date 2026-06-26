package com.condomanager.service;

import com.condomanager.dto.PagamentoCreateDTO;
import com.condomanager.dto.PagamentoResponse;
import com.condomanager.dto.ReferenciaPagamentoResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.MetodoPagamento;
import com.condomanager.model.Quota;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.security.TenantContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pagamento online — <strong>scaffolding</strong> de um fluxo de checkout.
 *
 * <p>{@link #iniciar(Long)} gera uma referência (estilo Multibanco) e
 * {@link #confirmar(Long)} simula o callback do gateway, registando o pagamento.
 * Para produção, substituir por Stripe/SIBS (criar PaymentIntent em {@code iniciar}
 * e tratar o webhook em {@code confirmar}).</p>
 */
@Service
public class PagamentoOnlineService {

    private static final String ENTIDADE = "11604";
    private static final String NOTA = "SIMULAÇÃO (scaffolding) — integrar Stripe/SIBS para produção.";

    private final QuotaRepository quotaRepository;
    private final PagamentoService pagamentoService;

    public PagamentoOnlineService(QuotaRepository quotaRepository, PagamentoService pagamentoService) {
        this.quotaRepository = quotaRepository;
        this.pagamentoService = pagamentoService;
    }

    @Transactional(readOnly = true)
    public ReferenciaPagamentoResponse iniciar(Long quotaId) {
        Quota quota = quota(quotaId);
        String referencia = String.format("%09d", Math.floorMod(quotaId * 137L + 100000007L, 1_000_000_000L));
        return new ReferenciaPagamentoResponse(ENTIDADE, referencia, quota.getValor(),
                "Multibanco: indique a Entidade, a Referência e o valor no homebanking ou ATM.", NOTA);
    }

    @Transactional
    public PagamentoResponse confirmar(Long quotaId) {
        Quota quota = quota(quotaId);
        return pagamentoService.registar(
                new PagamentoCreateDTO(quotaId, quota.getValor(), MetodoPagamento.MULTIBANCO, null));
    }

    private Quota quota(Long quotaId) {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return quotaRepository.findByIdAndIdEmpresa(quotaId, tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Quota", quotaId));
    }
}
