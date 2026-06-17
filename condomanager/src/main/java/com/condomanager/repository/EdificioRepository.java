package com.condomanager.repository;

import com.condomanager.model.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EdificioRepository extends JpaRepository<Edificio, Long> {
    List<Edificio> findByCondominioId(Long condominioId);
}
