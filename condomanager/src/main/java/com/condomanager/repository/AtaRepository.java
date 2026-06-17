package com.condomanager.repository;

import com.condomanager.model.Ata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtaRepository extends JpaRepository<Ata, Long> {
    List<Ata> findByCondominioId(Long condominioId);
}
