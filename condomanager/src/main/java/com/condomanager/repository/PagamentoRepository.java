package com.condomanager.repository;

import com.condomanager.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByQuotaId(Long quotaId);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pagamento p WHERE p.data BETWEEN :inicio AND :fim")
    BigDecimal somarReceitaPeriodo(@Param("inicio") java.time.LocalDate inicio,
                                   @Param("fim") java.time.LocalDate fim);
}
