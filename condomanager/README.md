# CondoManager SaaS — Implementação Inicial

Fundação do **Sistema de Gestão de Condomínios (SGC)** descrito em
[`../PLANEAMENTO_SGC_CondoManager.md`](../PLANEAMENTO_SGC_CondoManager.md).

## Stack
Java 21 · Spring Boot 3 · JPA/Hibernate · MySQL 8 (prod) / H2 (dev) ·
Spring Security · BCrypt · JWT · HTML5/CSS3/JS/Bootstrap 5.

## O que já está implementado
- **Arquitetura em camadas** com a estrutura de packages do plano
  (`controller`, `service`, `repository`, `model`, `dto`, `mapper`, `security`,
  `configuration`, `exception`).
- **Entidades JPA**: EmpresaGestao, Condominio, Edificio, Fracao, Condomino,
  Utilizador, Perfil (M:N), Quota, Pagamento, Despesa, Historico.
- **Segurança** (RF01, RNF01): autenticação por email/password, hashing BCrypt,
  emissão e validação de **JWT**, proteção por método com `@PreAuthorize` e
  base para isolamento multi-tenant (`TenantContext`).
- **CRUD de exemplo** end-to-end: Empresas e Condomínios.
- **Motor de faturação automática** de quotas (`@Scheduled`, Módulo 8).
- **Tratamento global de erros** (`@RestControllerAdvice`).
- **Seed inicial**: perfis + Administrador do Sistema.
- **Frontend** de login (Bootstrap 5) em `src/main/resources/static/index.html`.

## Como executar

### Modo desenvolvimento (H2, sem instalar MySQL)
```bash
mvn spring-boot:run
# Perfil 'dev' ativo por omissão. App em http://localhost:8080
```

### Modo produção (MySQL 8)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod \
  -Dspring-boot.run.arguments="--DB_HOST=localhost --DB_USER=condomanager --DB_PASSWORD=segredo"
```

## Teste rápido da API
```bash
# 1. Login (obter token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@condomanager.com","password":"admin123"}'

# 2. Usar o token nos pedidos seguintes
curl http://localhost:8080/api/empresas -H "Authorization: Bearer <TOKEN>"
```

Credenciais iniciais: `admin@condomanager.com` / `admin123` (**alterar em produção**).

## Módulos implementados (1–18)
Autenticação/Segurança, Empresas, Condomínios, Edifícios, Frações (limite por plano +
permilagem ≤ 1000), Condóminos, Utilizadores, Pagamentos (MBWay/Transferência/PayPal),
Despesas, Documentos, Atas, Reuniões, Votações (voto único + contagem), Comunicação,
Ocorrências, Dashboard, Relatórios (JasperReports → PDF) e Auditoria (AOP).

## Interface web
Servida em `src/main/resources/static`:
- `index.html` — login (guarda o JWT em localStorage).
- `app.html` — painel SPA (Bootstrap 5) com sidebar para todos os módulos, dashboard
  com indicadores, CRUD config-driven, geração de relatório PDF e **modo escuro** (RNF08).

Abrir `http://localhost:8080/` após arrancar a aplicação.

## Testes
```bash
mvn test   # 15 testes (JUnit 5 + Mockito + MockMvc)
```
Cobrem: regras de permilagem e limite de plano (FracaoService), voto único e contagem
(VotacaoService), marcação de quota paga (PagamentoService), cálculo da faturação
(QuotaScheduler), geração/validação de JWT, e o fluxo de login (AuthControllerTest).
