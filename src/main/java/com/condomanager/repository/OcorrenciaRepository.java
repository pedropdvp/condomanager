package com.condomanager.repository;

import com.condomanager.model.EstadoOcorrencia;
import com.condomanager.model.Ocorrencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    Optional<Ocorrencia> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Ocorrencia> findByCondominio_Id(Long condominioId, Pageable pageable);

    Page<Ocorrencia> findByCondominio_IdAndEstado(Long condominioId, EstadoOcorrencia estado, Pageable pageable);

    Page<Ocorrencia> findByEstado(EstadoOcorrencia estado, Pageable pageable);

    long countByEstado(EstadoOcorrencia estado);
}
