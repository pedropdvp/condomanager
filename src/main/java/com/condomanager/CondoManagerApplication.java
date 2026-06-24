package com.condomanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação CondoManager.
 *
 * <p>Sistema de Gestão de Condomínios — plataforma SaaS multi-tenant.
 * A arquitetura segue Controller → Service → Repository → JPA/Hibernate → MySQL,
 * conforme {@code docs/ARCHITECTURE.md}.</p>
 */
@SpringBootApplication
public class CondoManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CondoManagerApplication.class, args);
    }
}
