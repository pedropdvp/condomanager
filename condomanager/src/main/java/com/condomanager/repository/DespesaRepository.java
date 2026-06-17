package com.condomanager.repository;

import com.condomanager.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    List<Despesa> findByCondominioId(Long condominioId);
}
