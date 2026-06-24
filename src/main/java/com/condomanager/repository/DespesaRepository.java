package com.condomanager.repository;

import com.condomanager.model.CategoriaDespesa;
import com.condomanager.model.Despesa;
import com.condomanager.model.EstadoDespesa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    Optional<Despesa> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Despesa> findByCondominio_Id(Long condominioId, Pageable pageable);

    Page<Despesa> findByCondominio_IdAndCategoria(Long condominioId, CategoriaDespesa categoria, Pageable pageable);

    Page<Despesa> findByCondominio_IdAndEstado(Long condominioId, EstadoDespesa estado, Pageable pageable);

    Page<Despesa> findByEstado(EstadoDespesa estado, Pageable pageable);

    @Query("select coalesce(sum(d.valor), 0) from Despesa d")
    BigDecimal somaTotal();
}
