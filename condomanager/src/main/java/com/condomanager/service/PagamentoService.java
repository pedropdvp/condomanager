package com.condomanager.service;

import com.condomanager.dto.PagamentoDTO;
import com.condomanager.exception.ResourceNotFoundException;
import com.condomanager.model.Pagamento;
import com.condomanager.model.Quota;
import com.condomanager.model.enums.EstadoQuota;
import com.condomanager.repository.PagamentoRepository;
import com.condomanager.repository.QuotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PagamentoService {

    private final PagamentoRepository repository;
    private final QuotaRepository quotaRepository;

    public PagamentoService(PagamentoRepository repository, QuotaRepository quotaRepository) {
        this.repository = repository;
        this.quotaRepository = quotaRepository;
    }

    @Transactional(readOnly = true)
    public List<PagamentoDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagamentoDTO obter(Long id) {
        return toDTO(buscar(id));
    }

    /** Regista um pagamento de uma quota e atualiza o estado da quota para PAGA. */
    public PagamentoDTO registar(PagamentoDTO dto) {
        Quota quota = quotaRepository.findById(dto.getQuotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Quota nao encontrada: " + dto.getQuotaId()));

        Pagamento p = new Pagamento();
        p.setValor(dto.getValor());
        p.setData(dto.getData() != null ? dto.getData() : LocalDate.now());
        p.setMetodo(dto.getMetodo());
        p.setEstado(dto.getEstado() != null ? dto.getEstado() : "CONFIRMADO");
        p.setQuota(quota);
        Pagamento guardado = repository.save(p);

        quota.setEstado(EstadoQuota.PAGA);
        quotaRepository.save(quota);

        return toDTO(guardado);
    }

    private Pagamento buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento nao encontrado: " + id));
    }

    private PagamentoDTO toDTO(Pagamento p) {
        PagamentoDTO dto = new PagamentoDTO();
        dto.setId(p.getId());
        dto.setValor(p.getValor());
        dto.setData(p.getData());
        dto.setMetodo(p.getMetodo());
        dto.setEstado(p.getEstado());
        dto.setQuotaId(p.getQuota() != null ? p.getQuota().getId() : null);
        return dto;
    }
}
