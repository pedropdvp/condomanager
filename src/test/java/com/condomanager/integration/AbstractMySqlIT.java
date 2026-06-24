package com.condomanager.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base dos testes de integração com base de dados real (MySQL 8).
 *
 * <p>Como o esquema é gerido por Flyway e o Hibernate está em {@code ddl-auto: validate},
 * o simples arranque do contexto valida que todas as migrações {@code V1..V21} aplicam num
 * MySQL 8 real e que todas as entidades JPA mapeiam corretamente o esquema migrado.</p>
 *
 * <h2>Dois modos de execução</h2>
 * <ol>
 *   <li><b>Testcontainers (default)</b> — arranca um contentor MySQL 8 efémero. Portável e
 *       ideal para CI. Requer um Docker acessível pelo cliente Java (docker-java).</li>
 *   <li><b>BD externa</b> — se a propriedade de sistema {@code it.datasource.url} estiver
 *       definida, os testes usam essa base de dados em vez de arrancar um contentor. Útil
 *       quando o Docker existe mas a sua API não é acessível ao docker-java (ex.: certas
 *       versões do Docker Desktop no Windows), apontando para o MySQL do {@code docker-compose}.</li>
 * </ol>
 *
 * <p>Executar (Testcontainers): {@code mvn verify -Pintegration}<br>
 * Executar (BD externa, porta 3307 do docker-compose):
 * {@code mvn verify -Pintegration -Dit.datasource.url=jdbc:mysql://localhost:3307/condomanager}</p>
 */
@Testcontainers
@SpringBootTest
@ActiveProfiles("it")
public abstract class AbstractMySqlIT {

    private static final String EXTERNAL_URL = System.getProperty("it.datasource.url", "").trim();
    private static final boolean USE_CONTAINER = EXTERNAL_URL.isEmpty();

    @SuppressWarnings("resource") // contentor singleton; ciclo de vida gerido pelo Testcontainers/Ryuk
    static final MySQLContainer<?> MYSQL = USE_CONTAINER
            ? new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("condomanager")
                    .withUsername("condomanager")
                    .withPassword("condomanager")
            : null;

    static {
        if (USE_CONTAINER) {
            MYSQL.start();
        }
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        if (USE_CONTAINER) {
            registry.add("spring.datasource.url", () ->
                    MYSQL.getJdbcUrl() + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            registry.add("spring.datasource.username", MYSQL::getUsername);
            registry.add("spring.datasource.password", MYSQL::getPassword);
            registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        } else {
            registry.add("spring.datasource.url", () -> EXTERNAL_URL
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            registry.add("spring.datasource.username", () -> System.getProperty("it.datasource.username", "condomanager"));
            registry.add("spring.datasource.password", () -> System.getProperty("it.datasource.password", "condomanager"));
            registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        }
    }
}
