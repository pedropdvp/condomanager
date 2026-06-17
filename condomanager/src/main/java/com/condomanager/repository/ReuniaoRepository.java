package com.condomanager.repository;

import com.condomanager.model.Reuniao;
import com.condomanager.model.enums.EstadoReuniao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
    List<Reuniao> findByCondominioId(Long condominioId);
    long countByEstado(EstadoReuniao estado);
}
