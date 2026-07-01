# Stack Tecnológica

Documento de referência da stack do **CondoManager**. Serve também de _guardrail_:
as decisões técnicas devem respeitá-lo (ver [CLAUDE.md](CLAUDE.md)). As versões
refletem o [`pom.xml`](../pom.xml) e o frontend em [`src/main/resources/static`](../src/main/resources/static).

## Visão geral

| Camada | Tecnologia | Versão | Porquê |
|--------|-----------|--------|--------|
| Linguagem | **Java** | 21 (LTS) | Records, pattern matching, API moderna |
| Framework | **Spring Boot** | 3.3.5 | Web, Security, Data JPA, Validation, Actuator, Mail, AOP |
| Build | **Maven** | wrapper (`mvnw`) | Build reprodutível, sem Maven global |
| Persistência | **JPA / Hibernate** | (Spring Data) | Mapeamento objeto-relacional, repositórios |
| Base de dados | **MySQL** | 8 (local) · **TiDB Cloud** (produção) | Compatível MySQL; TiDB serverless no _free tier_ |
| Migrações | **Flyway** | core + mysql | Esquema versionado `V1`…`V22` |
| Segurança | **Spring Security + JWT (jjwt)** | jjwt 0.12.6 | Auth _stateless_, BCrypt, RBAC granular |
| Relatórios | **JasperReports** | 6.21.3 | PDF e Excel (quotas, despesas, ocorrências, balancete) |
| Documentação API | **springdoc-openapi** | 2.6.0 | Swagger UI a partir dos controladores |
| Observabilidade | **Actuator + Micrometer** | Prometheus registry | `/actuator/health\|metrics\|prometheus` |
| Boilerplate | **Lombok** | (gerido) | Menos código repetitivo nos modelos |
| Frontend | **HTML5 + CSS3 + JS (ES6+)** | — | SPA _vanilla_, sem framework/_build_ |
| UI | **Bootstrap** | 5.3.3 (CDN) | Layout responsivo, tema claro/escuro |
| Gráficos | **Chart.js** | 4.4.1 (CDN) | Gráficos do dashboard |
| Testes | **JUnit 5 + Mockito + Testcontainers** | (Spring BOM) | Unitários e integração |
| E2E | **Playwright** | (`e2e/`) | Smoke tests contra a app publicada |
| Container | **Docker** | multi-stage | Imagem de produção (Render) |
| Email | **Resend (HTTP API)** | — | Render bloqueia SMTP; envio por HTTP |

## Backend — dependências principais

- `spring-boot-starter-web`, `-security`, `-data-jpa`, `-validation`, `-aop`, `-mail`
- `spring-boot-starter-actuator` + `micrometer-registry-prometheus`
- `flyway-core` + `flyway-mysql`, `mysql-connector-j`
- `jjwt-api` / `jjwt-impl` / `jjwt-jackson` (0.12.6)
- `net.sf.jasperreports:jasperreports:6.21.3`
- `springdoc-openapi-starter-webmvc-ui:2.6.0`
- Testes: `spring-boot-starter-test`, `spring-security-test`, `spring-boot-testcontainers`, `junit-jupiter`, testcontainer `mysql`

## Frontend

SPA servida como conteúdo estático pelo próprio Spring Boot (sem passo de _build_):

- [`index.html`](../src/main/resources/static/index.html) — estrutura/_markup_
- [`css/app.css`](../src/main/resources/static/css/app.css) — estilos (tema claro/escuro, navbar em _pill_)
- [`js/app.js`](../src/main/resources/static/js/app.js) — toda a lógica (auth JWT, chamadas à API, _render_, gráficos)
- Bootstrap 5.3 e Chart.js 4.4 via CDN; tipografia Inter/Onest (Google Fonts)

## Tecnologias obrigatórias

Frontend: HTML5, CSS3, JavaScript ES6+, Bootstrap 5 · Backend: Java 21, Spring Boot 3 ·
ORM: JPA/Hibernate · BD: MySQL 8 · Segurança: Spring Security, BCrypt, JWT ·
Relatórios: JasperReports.

## Tecnologias proibidas

Não utilizar (salvo aprovação explícita): PHP, Laravel, Python, Django, Flask, Node.js,
Express, MongoDB, Firebase, Angular, React, Vue.

> Nota: **Node.js/Playwright** é usado **apenas** para os testes E2E (`e2e/`), fora do
> artefacto de produção — não faz parte da aplicação servida.

## Regras

Todas as decisões técnicas devem respeitar este documento. Não alterar a stack sem
aprovação. Ver também [ARCHITECTURE.md](ARCHITECTURE.md) e [DATABASE_RULES.md](DATABASE_RULES.md).
