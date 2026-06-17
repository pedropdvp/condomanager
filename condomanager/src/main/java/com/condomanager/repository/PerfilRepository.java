package com.condomanager.repository;

import com.condomanager.model.Perfil;
import com.condomanager.model.enums.NomePerfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByNome(NomePerfil nome);
}
