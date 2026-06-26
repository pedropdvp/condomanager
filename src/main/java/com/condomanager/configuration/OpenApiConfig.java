package com.condomanager.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Documentação OpenAPI 3 (Swagger UI em {@code /swagger-ui.html}).
 *
 * <p>Define o esquema de autenticação <strong>bearer JWT</strong>, para que o Swagger UI
 * permita colar o token obtido em {@code POST /api/v1/auth/login} e invocar os endpoints
 * protegidos.</p>
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "CondoManager API",
        version = "v1",
        description = "API de gestão de condomínios — multi-tenant, RBAC granular e regras da Lei 8/2022.",
        contact = @Contact(name = "CondoManager")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
