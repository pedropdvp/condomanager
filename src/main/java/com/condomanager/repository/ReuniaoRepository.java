package com.condomanager.repository;

import com.condomanager.model.EstadoReuniao;
import com.condomanager.model.Reuniao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    Optional<Reuniao> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Reuniao> findByCondominio_Id(Long condominioId, Pageable pageable);

    Page<Reuniao> findByCondominio_IdAndEstado(Long condominioId, EstadoReuniao estado, Pageable pageable);

    long countByEstado(EstadoReuniao estado);
}
