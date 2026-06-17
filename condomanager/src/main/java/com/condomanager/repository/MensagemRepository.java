package com.condomanager.repository;

import com.condomanager.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByCondominioId(Long condominioId);
}
