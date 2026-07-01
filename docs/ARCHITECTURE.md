# Arquitetura

Aplicação **SaaS multi-tenant** em camadas (Spring Boot 3 / Java 21), com frontend SPA
servido como conteúdo estático. Este documento é _guardrail_ — o código deve respeitá-lo
(ver [CLAUDE.md](CLAUDE.md)). Ver também [TECH_STACK.md](TECH_STACK.md) e
[DATABASE_SCHEMA.md](DATABASE_SCHEMA.md).

## Camadas

```
Browser (SPA: index.html + app.js + app.css)
        │  HTTPS + JWT (Authorization: Bearer)
        ▼
Controller  ── validação (@Valid), @PreAuthorize (RBAC)
        ▼
Service     ── regras de negócio, @Transactional, contexto de tenant
        ▼
Repository  ── Spring Data JPA
        ▼
JPA / Hibernate  ── filtro multi-tenant (id_empresa)
        ▼
MySQL 8 / TiDB Cloud  ── esquema versionado por Flyway
```

Objetos de transporte: **DTOs/records** de entrada e saída; **mappers** convertem
entre `model` (entidades) e `dto`. As entidades nunca são expostas diretamente na API.

## Multi-tenant

- Discriminador **`id_empresa`** em todas as entidades de negócio.
- O _tenant_ é resolvido a partir de um **claim do JWT** e colocado num contexto por pedido
  (`TenantContext`); um **filtro Hibernate** garante o isolamento nas queries.
- **Empresas** são a raiz do _tenant_ (o `ADMIN_SISTEMA` gere-as e tem acesso total).

## Segurança

- **JWT** (jjwt) com validade de 1 h; filtro de autenticação por pedido; passwords em **BCrypt**.
- **RBAC granular** persistido: matriz **perfil × funcionalidade × ação**
  (`CRIAR/EDITAR/APAGAR/CONSULTAR`), aplicada com
  `@PreAuthorize("@permissaoService.pode('MODULO','ACAO')")`.
- **Rate-limiting** no login (respostas 429) e conformidade **RGPD** (exportar/apagar dados).
- Endpoints **`/me/**`** (Portal do Condómino) são _scoped_ à própria fração do utilizador.

## Princípios

Separação de responsabilidades · SOLID · Clean Code / Clean Architecture ·
Injeção de dependências · DTO Pattern · entidades preparadas para multi-tenant ·
código pronto para produção.

## Packages (`com.condomanager`)

| Package | Responsabilidade |
|---------|------------------|
| `controller` | Endpoints REST (`/api/v1/**`), validação e autorização (~25 controladores) |
| `service` | Regras de negócio e transações (~27 serviços) |
| `repository` | Interfaces Spring Data JPA |
| `model` | Entidades JPA (domínio) |
| `dto` | Records de entrada/saída da API |
| `mapper` | Conversão model ↔ dto |
| `security` | JWT, filtros, contexto de tenant, RBAC (`PermissaoService`) |
| `configuration` | Config Spring (Security, OpenAPI, Web, Scheduling, Actuator) |
| `report` | Geração de relatórios JasperReports (PDF/Excel) |
| `exception` | Exceções de domínio + handler global |
| `validation` | Validadores/anotações personalizadas |
| `util` | Utilitários transversais |

## Persistência e migrações

- **Flyway** aplica as migrações `V1`…`V22` em [`src/main/resources/db/migration`](../src/main/resources/db/migration).
- `spring.jpa.hibernate.ddl-auto: validate` — o Hibernate **não** altera o esquema; a
  fonte de verdade é o Flyway. Modelo de dados detalhado em [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)
  e [DIAGRAMA_ER.md](DIAGRAMA_ER.md).

## API e observabilidade

- **springdoc-openapi** publica Swagger UI em `/swagger-ui.html` a partir dos controladores.
- **Actuator + Micrometer/Prometheus**: `/actuator/health`, `/metrics`, `/prometheus`.

## Deploy (resumo)

**Docker** (multi-stage) → **Render** (autoDeploy no `git push` para `master`) + **TiDB Cloud**.
Email via **Resend** (HTTP, porque o _free tier_ do Render bloqueia SMTP); _keep-alive_ por
GitHub Actions. Detalhe em [DEPLOY-RENDER.md](DEPLOY-RENDER.md) e [HOSTING.md](HOSTING.md).
