package com.condomanager.repository;

import com.condomanager.model.EstadoQuota;
import com.condomanager.model.Quota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface QuotaRepository extends JpaRepository<Quota, Long> {

    Optional<Quota> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    boolean existsByFracao_IdAndMesAndAno(Long fracaoId, int mes, int ano);

    Page<Quota> findByFracao_Id(Long fracaoId, Pageable pageable);

    Page<Quota> findByFracao_Condominio_Id(Long condominioId, Pageable pageable);

    /** Todas as quotas de um condomínio (usado nos relatórios). */
    java.util.List<Quota> findByFracao_Condominio_Id(Long condominioId);

    Page<Quota> findByEstado(EstadoQuota estado, Pageable pageable);

    long countByEstado(EstadoQuota estado);

    @Query("select coalesce(sum(q.valor), 0) from Quota q where q.estado = :estado")
    BigDecimal somaValorPorEstado(@Param("estado") EstadoQuota estado);
}
