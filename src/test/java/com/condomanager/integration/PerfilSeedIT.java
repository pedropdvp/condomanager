package com.condomanager.integration;

import com.condomanager.model.PerfilTipo;
import com.condomanager.repository.PerfilRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração que valida, contra MySQL real e através da camada JPA, que os
 * cinco perfis RBAC semeados pela migração {@code V2} estão presentes e legíveis.
 */
class PerfilSeedIT extends AbstractMySqlIT {

    @Autowired
    private PerfilRepository perfilRepository;

    @Test
    void osCincoPerfisRbacEstaoSemeados() {
        assertThat(perfilRepository.count()).isEqualTo(5);

        for (PerfilTipo tipo : PerfilTipo.values()) {
            assertThat(perfilRepository.findByNome(tipo.name()))
                    .as("Perfil %s deve existir (migração V2)", tipo.name())
                    .isPresent();
        }
    }
}
