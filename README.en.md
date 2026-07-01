# 🏢 CondoManager — Condominium Management System (SaaS)

📖 **Languages:** [Português](README.md) · English

Multi-tenant **SaaS platform** for condominium management, aligned with Portuguese
**Law 8/2022** (fees by permillage, quorum/majorities, reserve fund).
Java 21 · Spring Boot 3 · MySQL/TiDB · SPA frontend (Bootstrap 5 + Chart.js).

## 🌐 Live demo

- **App:** <https://condomanager.onrender.com>
- **Landing page:** <https://condomanager.onrender.com/landing.html>
- **API (Swagger):** <https://condomanager.onrender.com/swagger-ui.html>
- **Health:** <https://condomanager.onrender.com/api/v1/health>

> The free Render instance sleeps after ~15 min — the first request may take
> ~30–60 s (cold start). See [`docs/HOSTING.md`](docs/HOSTING.md).

### Demo accounts

| Role | Email | Password | What they see |
|------|-------|----------|---------------|
| **Manager** | `gestor.alfa@demo.local` | `gestor123` | Full management of the condominium(s) |
| **Administrator** | `admin@condomanager.local` | `admin123` | Users, Permissions, Audit, Companies |
| **Owner** | `portal.demo@demo.local` | `portal123` | Self-service portal (my fees, vote) |

## 📸 Overview

![CondoManager dashboard in dark theme — condominium KPIs and charts for structure, fees by status/value and issues](docs/img/dashboard.png)

> Manager dashboard (dark theme): condominium KPIs and interactive charts
> (structure, fees by status and by value, issues by status).

| Voting (counted by permillage) | Balance sheet / Treasury |
|---|---|
| [![Voting and result by permillage](docs/img/votacoes.png)](docs/img/votacoes.png) | [![Balance sheet with reserve fund](docs/img/balancete.png)](docs/img/balancete.png) |
| **Permissions — granular RBAC** | **Owner Portal** |
| [![Permission matrix role × feature × action](docs/img/permissoes.png)](docs/img/permissoes.png) | [![Owner self-service portal](docs/img/portal.png)](docs/img/portal.png) |

## ✨ Features

**Condominium management**
- Condominiums, buildings, **units** (permillage, base 1000) and **owners**.
- **Fees** by permillage (`fee = budget × permillage ÷ 1000`) and **payments**
  (manual + **online payment** with a Multibanco reference — *scaffolding* ready for Stripe/SIBS).
- **Expenses** (approval/rejection) and **Balance sheet/Treasury** (income, expenses, balance and
  **reserve fund ≥10%**).
- **Meetings** (schedule, convene by email, hold/cancel), **Minutes** (with attachments) and **Documents**.
- **Voting** counted by **permillage** with Law 8/2022 majorities; **Issues** tracking.
- Internal **Messages** (individual / broadcast), **Notifications** (overdue-fee reminders).

**Platform**
- **Multi-tenant** (isolation by `id_empresa`), **Companies** (tenant management).
- Persistent **granular RBAC** (role × feature × action matrix) enforced across 10 modules.
- **Owner Portal** (self-service): context, my fees, pay and **vote**.
- Immutable **Audit** log (search, filters, pagination), **PDF/Excel reports**
  (fees, expenses, issues, balance sheet).
- **i18n** (PT; EN/FR under construction), light/dark theme, per-menu themed backgrounds.

## 🧰 Stack

| Layer | Technologies |
|-------|--------------|
| Backend | Java 21, Spring Boot 3.3 (Web, Security, Data JPA, Validation, Actuator) |
| Persistence | MySQL 8 / TiDB Cloud, **Flyway** (migrations V1–V22) |
| Security | JWT, BCrypt, granular RBAC, login rate-limiting, GDPR |
| Reports | JasperReports (PDF/Excel) |
| Frontend | SPA `static/` — Bootstrap 5.3, Chart.js, vanilla JS (`js/app.js`, `css/app.css`) |
| API docs | springdoc-openapi (Swagger UI) |
| Observability | Spring Actuator + Micrometer/Prometheus |
| Deploy | Docker + Render (free) + TiDB Cloud Serverless; email via Resend (HTTP) |
| Tests | JUnit 5 + Mockito (unit/integration) + Playwright (smoke E2E in `e2e/`) |

Details in [`docs/TECH_STACK.md`](docs/TECH_STACK.md) and [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## 🏗️ Architecture (summary)

- **Multi-tenant** via `id_empresa` discriminator + Hibernate filter; tenant resolved from the JWT *claim*.
- Layers `controller → service → repository`, DTOs/records, mappers.
- **Security**: JWT (1 h) + authentication filter; `@PreAuthorize` with granular RBAC
  (`@permissaoService.pode('MODULE','ACTION')`); `ADMIN_SISTEMA` has full access.
- **`/me` endpoints** (Owner Portal) — everything *scoped* to the user's own unit.

## ▶️ Running locally

Prerequisites: **JDK 21**, **Maven**, **Docker** (for MySQL).

```bash
docker compose up -d          # MySQL 8 (maps 3307->3306)
mvn clean verify              # compile + tests
mvn spring-boot:run           # starts at http://localhost:8080
```

- Frontend: <http://localhost:8080/> · Swagger: `/swagger-ui.html` · Health: `/api/v1/health`
- The `dev` profile seeds the demo accounts.
- Ports in use? `set DB_PORT=3307` and `set SERVER_PORT=8081` (see notes in `docs/`).

### E2E tests (Playwright)

```bash
cd e2e && npm install && npx playwright install chromium && npm test
```

## 🚀 Deploy

Render (Docker, free) + TiDB Cloud Serverless. `git push` to `master` → autoDeploy.
A GitHub Actions workflow performs a *keep-alive* every ~12 min.
Details and scalability in [`docs/HOSTING.md`](docs/HOSTING.md) and [`docs/DEPLOY-RENDER.md`](docs/DEPLOY-RENDER.md).

## ⚖️ Legal compliance (Law 8/2022)

Fees and votes by **permillage** (base 1000), **quorum/majorities** per resolution type,
**common reserve fund ≥10%**, notices with advance. See [`docs/LEGAL_RULES.md`](docs/LEGAL_RULES.md).

## 📚 Documentation

Specification, business/legal rules, architecture, database and roadmap in [`docs/`](docs/) —
notably [`docs/SPEC.md`](docs/SPEC.md), [`docs/LEGAL_RULES.md`](docs/LEGAL_RULES.md) and
[`docs/GUIAO_APRESENTACAO.md`](docs/GUIAO_APRESENTACAO.md) (presentation script, in Portuguese).
