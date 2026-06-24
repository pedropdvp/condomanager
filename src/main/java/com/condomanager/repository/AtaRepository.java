package com.condomanager.repository;

import com.condomanager.model.Ata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtaRepository extends JpaRepository<Ata, Long> {

    Optional<Ata> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Ata> findByIdReuniao(Long idReuniao, Pageable pageable);

    Page<Ata> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);
}
