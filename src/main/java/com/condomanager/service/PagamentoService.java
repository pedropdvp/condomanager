package com.condomanager.service;

import com.condomanager.dto.PagamentoCreateDTO;
import com.condomanager.dto.PagamentoResponse;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.mapper.PagamentoMapper;
import com.condomanager.model.EstadoPagamento;
import com.condomanager.model.EstadoQuota;
import com.condomanager.model.Pagamento;
import com.condomanager.model.Quota;
import com.condomanager.repository.PagamentoRepository;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Regras de negócio dos pagamentos. Ao registar, se o total confirmado atingir o valor
 * da quota, esta passa a {@code PAGO}.
 */
@Service
public class PagamentoService {

    private static final String RECURSO = "Pagamento";

    private static final Logger logger = LoggerFactory.getLogger(PagamentoService.class);

    private final PagamentoRepository repository;
    private final QuotaRepository quotaRepository;
    private final PagamentoMapper mapper;

    public PagamentoService(PagamentoRepository repository,
                            QuotaRepository quotaRepository,
                            PagamentoMapper mapper) {
        this.repository = repository;
        this.quotaRepository = quotaRepository;
        this.mapper = mapper;
    }

    @Transactional
    public PagamentoResponse registar(PagamentoCreateDTO dto) {
        Long tenant = tenantObrigatorio();
        Quota quota = quotaRepository.findByIdAndIdEmpresa(dto.quotaId(), tenant)
                .orElseThrow(() -> new ResourceNotFoundException("Quota", dto.quotaId()));
        if (quota.getEstado() == EstadoQuota.ANULADO) {
            throw new IllegalArgumentException("Não é possível pagar uma quota anulada.");
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setQuota(quota);
        pagamento.setValor(dto.valor());
        pagamento.setMetodo(dto.metodo());
        pagamento.setEstado(EstadoPagamento.CONFIRMADO);
        pagamento.setDataPagamento(dto.dataPagamento() != null ? dto.dataPagamento() : LocalDateTime.now());
        Pagamento guardado = repository.save(pagamento);

        atualizarEstadoQuota(quota);
        logger.info("Pagamento registado: id={}, quota={}, novo estado quota={}",
                guardado.getId(), quota.getId(), quota.getEstado());
        return mapper.toResponse(guardado);
    }

    private void atualizarEstadoQuota(Quota quota) {
        BigDecimal totalPago = repository
                .findByQuota_IdAndEstado(quota.getId(), EstadoPagamento.CONFIRMADO).stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPago.compareTo(quota.getValor()) >= 0) {
            quota.setEstado(EstadoQuota.PAGO);
        }
    }

    @Transactional(readOnly = true)
    public Page<PagamentoResponse> listar(Long quotaId, Pageable pageable) {
        Page<Pagamento> pagina = (quotaId != null)
                ? repository.findByQuota_Id(quotaId, pageable)
                : repository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse obterPorId(Long id) {
        return mapper.toResponse(
                repository.findByIdAndIdEmpresa(id, tenantObrigatorio())
                        .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id)));
    }

    private Long tenantObrigatorio() {
        Long tenant = TenantContext.getTenantId();
        if (tenant == null) {
            throw new AccessDeniedException("Operação requer uma empresa associada.");
        }
        return tenant;
    }
}
