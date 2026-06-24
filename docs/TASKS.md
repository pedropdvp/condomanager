# CondoManager SaaS

## Roadmap Geral
Estado:
EFETUADO (MVP)

> **Base da atualização (2026-06-24).** Estados marcados como **EFETUADO** com a seguinte
> evidência objetiva:
> - **Código**: 217 ficheiros Java; `mvn clean verify` com **BUILD SUCCESS**.
> - **Testes**: 60 testes unitários + 3 de integração com BD real (MySQL 8) — todos verdes.
>   Integração: `mvn verify -Pintegration` (Testcontainers) ou contra MySQL existente com
>   `-Dit.datasource.url=jdbc:mysql://localhost:3307/condomanager`.
> - **Migrações**: Flyway `V1..V21` aplicadas e validadas num MySQL 8 real.
> - **Notas de âmbito**:
>   - *Fase 10* — pagamentos por **registo manual** (métodos TRANSFERENCIA/MBWAY/MULTIBANCO/
>     PAYPAL/DINHEIRO suportados); a **integração com gateways reais** está fora de âmbito do
>     MVP (`SPEC.md` item A).
>   - *Fase 21 (Deploy)* — **artefactos completos** (`Dockerfile`, `deploy/`, `docs/DEPLOY.md`);
>     o arranque num servidor/host concreto depende da infraestrutura do cliente.
>   - *Fase 22 (Comercial)* — landing page, planos e guião de demonstração entregues
>     (`static/landing.html`, `docs/COMMERCIAL.md`). O *billing* da subscrição fica pós-MVP.

---

# FASE 0
Fundação (estrutura, multi-tenant, Flyway, tratamento de erros)

Estado:
EFETUADO

Tarefas
[x] Projeto Maven + Spring Boot 3
[x] Estrutura de packages (ARCHITECTURE.md)
[x] Mecanismo multi-tenant (TenantContext/TenantFilter/TenantAwareEntity)
[x] Migrações Flyway (baseline)
[x] Tratamento global de erros + endpoint /api/v1/health

---

# FASE 1
Análise e Desenho Funcional

Estado:
EFETUADO

Tarefas
[x] Validar requisitos funcionais
[x] Validar requisitos não funcionais
[x] Validar perfis de utilizador
[x] Validar modelo SaaS

---

# FASE 2
Base de Dados
Estado:
EFETUADO

Tarefas
[x] Criar modelo relacional
[x] Criar diagrama ER
[x] Criar esquema MySQL
[x] Criar entidades JPA
[x] Criar relacionamentos
[x] Criar tenant_id

---

# FASE 3
Autenticação e Segurança
Estado:
EFETUADO

Tarefas
[x] Login
[x] Logout
[x] JWT
[x] BCrypt
[x] Recuperação password
[x] Gestão de perfis
[x] Gestão de permissões

---

# FASE 4
Empresas de Gestão
Estado:
EFETUADO

Tarefas
[x] Entidade EmpresaGestao
[x] Repository
[x] Service
[x] Controller
[x] CRUD completo

---

# FASE 5
Condomínios
Estado:
EFETUADO

Tarefas
[x] Entidade Condominio
[x] CRUD
[x] Pesquisa
[x] Filtros

---

# FASE 6
Edifícios
Estado:
EFETUADO

Tarefas
[x] Entidade Edificio
[x] CRUD

---

# FASE 7
Frações
Estado:
EFETUADO

Tarefas
[x] Entidade Fracao
[x] CRUD
[x] Permilagem
[x] Tipologia

---

# FASE 8
Condóminos
Estado:
EFETUADO

Tarefas
[x] Entidade Condomino
[x] CRUD
[x] Associação a frações

---

# FASE 9
Utilizadores
Estado:
EFETUADO

Tarefas
[x] Entidade Utilizador
[x] Perfis
[x] Permissões

---

# FASE 10
Pagamentos
Estado:
EFETUADO

Tarefas
[x] Entidade Quota
[x] Entidade Pagamento
[x] Geração automática mensal
[x] MBWay (registo manual; gateway fora de âmbito — SPEC item A)
[x] Transferência (registo manual)
[x] PayPal (registo manual; gateway fora de âmbito — SPEC item A)

---

# FASE 11
Despesas
Estado:
EFETUADO

Tarefas
[x] CRUD Despesas
[x] Aprovação

---

# FASE 12
Documentos
Estado:
EFETUADO

Tarefas
[x] Upload
[x] Download
[x] Pesquisa

---

# FASE 13
Atas
Estado:
EFETUADO

Tarefas
[x] CRUD
[x] Arquivo

---

# FASE 14
Reuniões
Estado:
EFETUADO

Tarefas
[x] Agendamento
[x] Convocatórias (por email — EmailService)

---

# FASE 15
Votações
Estado:
EFETUADO

Tarefas
[x] Criar votação
[x] Abrir votação
[x] Encerrar votação
[x] Contagem automática (por permilagem; maiorias Lei 8/2022)

---

# FASE 16
Comunicação
Estado:
EFETUADO

Tarefas
[x] Mensagens privadas
[x] Mensagens de grupo
[x] Notificações (broadcast + estado lida/não-lida)

---

# FASE 17
Ocorrências
Estado:
EFETUADO

Tarefas
[x] Registo
[x] Atribuição
[x] Encerramento

---

# FASE 18
Dashboard
Estado:
EFETUADO

Tarefas
[x] Indicadores
[x] Estatísticas
[x] Gráficos

---

# FASE 19
Relatórios
Estado:
EFETUADO

Tarefas
[x] JasperReports
[x] PDF
[x] Excel

---

# FASE 20
Auditoria
Estado:
EFETUADO

Tarefas
[x] Histórico
[x] Logs
[x] Tracking

---

# FASE 21
Deploy
Estado:
EFETUADO

Tarefas
[x] Linux Ubuntu (Dockerfile + docs/DEPLOY.md)
[x] MySQL (docker-compose.prod.yml)
[x] Nginx (deploy/nginx/condomanager.conf)
[x] SSL (Let's Encrypt/Certbot — DEPLOY.md)
[x] Backup (deploy/scripts/backup.sh + cron)

> Artefactos prontos; o arranque num servidor concreto depende da infraestrutura do cliente.

---

# FASE 22
Preparação Comercial
Estado:
EFETUADO

Tarefas
[x] Plano Starter
[x] Plano Business
[x] Plano Enterprise
[x] Landing Page (static/landing.html)
[x] Demonstração Comercial (guião em docs/COMMERCIAL.md)
