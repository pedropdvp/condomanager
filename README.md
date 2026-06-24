# CondoManager — Sistema de Gestão de Condomínios (SaaS)

Plataforma SaaS multi-tenant para gestão de condomínios.
Documentação funcional e técnica em [`docs/`](docs/) — em especial
[`docs/SPEC.md`](docs/SPEC.md) e [`docs/LEGAL_RULES.md`](docs/LEGAL_RULES.md).

## Stack

Java 21 · Spring Boot 3 · JPA/Hibernate · MySQL 8 · Flyway · Bootstrap 5
(ver [`docs/TECH_STACK.md`](docs/TECH_STACK.md)).

## Estado atual — Fase 0 (Fundação)

Esta fase entrega a base sobre a qual os módulos serão construídos:

- Projeto Maven `com.condomanager` e configuração Spring Boot 3.
- Estrutura de packages conforme [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).
- Migrações de base de dados com **Flyway** (`src/main/resources/db/migration`).
- **Mecanismo multi-tenant** (discriminador `id_empresa` + filtro Hibernate):
  - `security/TenantContext` — guarda o tenant do pedido (ThreadLocal).
  - `security/TenantFilter` — resolve o tenant por pedido (Fase 0: cabeçalho
    `X-Tenant-Id`; Fase 1: passará a ler o *claim* do JWT).
  - `model/TenantAwareEntity` — superclasse com `id_empresa` e o filtro `tenantFilter`.
  - `configuration/TenantFilterAspect` — ativa o filtro antes de cada acesso a repositório.
- Tratamento global de erros (`exception/GlobalExceptionHandler`) e endpoint `/api/v1/health`.

## Como executar

Pré-requisitos: **JDK 21**, **Maven** e **Docker** (para o MySQL).

```bash
# 1. Arrancar o MySQL 8
docker compose up -d

# 2. Compilar e correr os testes
mvn clean verify

# 3. Arrancar a aplicação
mvn spring-boot:run
```

Depois:

- Frontend: <http://localhost:8080/>
- Health: <http://localhost:8080/api/v1/health>
- Login: `POST /api/v1/auth/login` com `{"email":"admin@condomanager.local","password":"admin123"}` (utilizador semeado no perfil `dev`).

### Notas de ambiente (portas em uso)

Se já existir um MySQL local na 3306 ou algo na 8080, usa portas alternativas:

```powershell
$env:DB_PORT="3307"      # o docker-compose mapeia 3307->3306
$env:SERVER_PORT="8081"  # a app passa a escutar na 8081
docker compose up -d
mvn spring-boot:run
```

No Windows, se o `docker compose` falhar com `docker-credential-desktop ... not found`,
adiciona ao PATH da sessão: `C:\Program Files\Docker\Docker\resources\bin`.

## Próximas fases

Ver [`docs/MODULES_ROADMAP.md`](docs/MODULES_ROADMAP.md).
A seguir: **Fase 1 — Autenticação & Segurança** (Spring Security + JWT + BCrypt),
momento em que o `TenantFilter` passa a obter o tenant a partir do token.
