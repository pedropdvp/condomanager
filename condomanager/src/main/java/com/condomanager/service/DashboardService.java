package com.condomanager.service;

import com.condomanager.dto.DashboardDTO;
import com.condomanager.model.enums.EstadoQuota;
import com.condomanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final CondominioRepository condominioRepository;
    private final CondominoRepository condominoRepository;
    private final FracaoRepository fracaoRepository;
    private final QuotaRepository quotaRepository;
    private final PagamentoRepository pagamentoRepository;

    public DashboardService(CondominioRepository condominioRepository,
                            CondominoRepository condominoRepository,
                            FracaoRepository fracaoRepository,
                            QuotaRepository quotaRepository,
                            PagamentoRepository pagamentoRepository) {
        this.condominioRepository = condominioRepository;
        this.condominoRepository = condominoRepository;
        this.fracaoRepository = fracaoRepository;
        this.quotaRepository = quotaRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDTO indicadores() {
        DashboardDTO dto = new DashboardDTO();
        dto.setCondominiosAtivos(condominioRepository.count());
        dto.setCondominosAtivos(condominoRepository.count());
        dto.setFracoes(fracaoRepository.count());
        dto.setQuotasPorPagar(
                quotaRepository.countByEstado(EstadoQuota.PENDENTE)
                        + quotaRepository.countByEstado(EstadoQuota.ATRASO));
        dto.setDividasPendentes(quotaRepository.somarDividasPendentes());

        LocalDate hoje = LocalDate.now();
        LocalDate inicio = hoje.withDayOfMonth(1);
        LocalDate fim = hoje.withDayOfMonth(hoje.lengthOfMonth());
        dto.setReceitaMesAtual(pagamentoRepository.somarReceitaPeriodo(inicio, fim));
        return dto;
    }
}
