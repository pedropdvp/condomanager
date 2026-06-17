package com.condomanager.controller;

import com.condomanager.dto.QuotaDTO;
import com.condomanager.model.Quota;
import com.condomanager.repository.QuotaRepository;
import com.condomanager.service.QuotaScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quotas")
public class QuotaController {

    private final QuotaRepository repository;
    private final QuotaScheduler scheduler;

    public QuotaController(QuotaRepository repository, QuotaScheduler scheduler) {
        this.repository = repository;
        this.scheduler = scheduler;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR','FUNCIONARIO','ADMIN_CONDOMINIO','CONDOMINO')")
    public List<QuotaDTO> listar() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /** Dispara manualmente a faturacao do mes corrente (alem do agendamento @Scheduled). */
    @PostMapping("/gerar")
    @PreAuthorize("hasAnyRole('ADMIN_SISTEMA','GESTOR')")
    public List<QuotaDTO> gerar() {
        scheduler.gerarQuotasMensais();
        return listar();
    }

    private QuotaDTO toDTO(Quota q) {
        QuotaDTO dto = new QuotaDTO();
        dto.setId(q.getId());
        dto.setMes(q.getMes());
        dto.setAno(q.getAno());
        dto.setValor(q.getValor());
        dto.setEstado(q.getEstado());
        dto.setFracaoId(q.getFracao() != null ? q.getFracao().getId() : null);
        return dto;
    }
}
