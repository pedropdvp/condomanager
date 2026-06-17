package com.condomanager.repository;

import com.condomanager.model.Quota;
import com.condomanager.model.enums.EstadoQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface QuotaRepository extends JpaRepository<Quota, Long> {

    boolean existsByFracaoIdAndMesAndAno(Long fracaoId, int mes, int ano);

    long countByEstado(EstadoQuota estado);

    java.util.List<Quota> findByEstadoNot(EstadoQuota estado);

    @Query("SELECT COALESCE(SUM(q.valor), 0) FROM Quota q WHERE q.estado <> com.condomanager.model.enums.EstadoQuota.PAGA")
    BigDecimal somarDividasPendentes();
}
