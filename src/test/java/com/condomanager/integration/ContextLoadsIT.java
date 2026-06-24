package com.condomanager.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração de fumo: o contexto Spring arranca contra um MySQL 8 real.
 *
 * <p>Por si só, o arranque valida a aplicação de todas as migrações Flyway e o
 * mapeamento das entidades JPA ({@code ddl-auto: validate}). Adicionalmente,
 * confirma que o histórico do Flyway regista todas as versões esperadas.</p>
 */
class ContextLoadsIT extends AbstractMySqlIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextoArrancaEMigracoesFlywaySaoAplicadas() {
        Integer migracoesComSucesso = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = 1", Integer.class);

        // V1..V21 = 21 migrações versionadas.
        assertThat(migracoesComSucesso).isNotNull().isGreaterThanOrEqualTo(21);
    }

    @Test
    void tabelasDeNegocioForamCriadas() {
        Integer tabelas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() "
                        + "AND table_name IN ('utilizador','perfil','condominio','fracao','quota',"
                        + "'votacao','voto','mensagem','mensagem_destinatario')",
                Integer.class);

        assertThat(tabelas).isEqualTo(9);
    }
}
