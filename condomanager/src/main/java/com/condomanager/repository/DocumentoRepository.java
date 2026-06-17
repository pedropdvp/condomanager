package com.condomanager.repository;

import com.condomanager.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByCondominioId(Long condominioId);
    List<Documento> findByTipo(String tipo);
}
