package com.condomanager.repository;

import com.condomanager.model.Documento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    Optional<Documento> findByIdAndIdEmpresa(Long id, Long idEmpresa);

    Page<Documento> findByCondominio_Id(Long condominioId, Pageable pageable);

    Page<Documento> findByCondominio_IdAndNomeContainingIgnoreCase(Long condominioId, String nome, Pageable pageable);
}
