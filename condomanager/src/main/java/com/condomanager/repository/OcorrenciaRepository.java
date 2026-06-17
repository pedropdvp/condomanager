package com.condomanager.repository;

import com.condomanager.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    List<Ocorrencia> findByCondominoId(Long condominoId);
}
