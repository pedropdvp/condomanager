package com.condomanager.repository;

import com.condomanager.model.EstadoPagamento;
import com.condomanager.model.Pagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Pagamento> findByQuota_Id(Long quotaId, Pageable pageable);

    /** Pagamentos de uma quota num dado estado (usado para somar o que está liquidado). */
    List<Pagamento> findByQuota_IdAndEstado(Long quotaId, EstadoPagamento estado);
}
