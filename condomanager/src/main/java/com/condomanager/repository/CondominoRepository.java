package com.condomanager.repository;

import com.condomanager.model.Condomino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CondominoRepository extends JpaRepository<Condomino, Long> {
    List<Condomino> findByFracaoId(Long fracaoId);
    List<Condomino> findByFracaoCondominioId(Long condominioId);
}
