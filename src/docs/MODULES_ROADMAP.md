# Módulos do Sistema — Roadmap

> Estado verificado em 2026-06-24. Legenda: ✅ implementado · 🟡 implementado no backend, a expor/again na UI · ⛔ por fazer.

---

## Módulo 1 — Autenticação e Segurança

### Funcionalidades

| Funcionalidade | Estado | Detalhe técnico |
|---|---|---|
| **Login** | ✅ | `POST /api/v1/auth/login` → JWT. UI: formulário em `/`. |
| **Logout** | ✅ | Sem estado no servidor (JWT *stateless*); a UI descarta o token. |
| **Recuperação de password** | ✅ backend · 🟡 UI | `POST /api/v1/auth/recuperar-password` (gera token, `V18`) e `POST /api/v1/auth/redefinir-password` (token + nova password). **Requer SMTP** para enviar o token por email (`MAIL_ENABLED=true`); com email desligado, o token é apenas registado no log. UI: ligações na página de login. |
| **Alteração de password** | ✅ backend · ✅ UI | `PUT /api/v1/utilizadores/{id}/password`. UI: opção "Alterar password" (utilizador na sessão). |
| **Gestão de perfis** | ✅ backend · ✅ UI | Perfis RBAC fixos (`ADMIN_SISTEMA`, `GESTOR_EMPRESA`, `FUNCIONARIO`, `ADMIN_CONDOMINIO`, `CONDOMINO`), semeados em `V2`. **Ecrã "Gestão de Utilizadores"** (separador na UI, para admin/gestor): listar, pesquisar, criar acesso, editar e apagar utilizadores, atribuindo o perfil. O gestor não pode atribuir `ADMIN_SISTEMA` (validado no backend e na UI). A matriz CRIAR/EDITAR/APAGAR/CONSULTAR é mostrada **pré-preenchida pelo perfil** como guia visual. |
| **Gestão de permissões** | ✅ (por desenho) | As permissões são **baseadas em papéis (RBAC)** via `@PreAuthorize("hasAnyRole(...)")` em cada endpoint — não existe um CRUD de permissões granulares separado; gere-se atribuindo perfis aos utilizadores. |

### Tecnologias
- **Spring Security** (filtros, `@PreAuthorize`, `SecurityFilterChain`)
- **BCrypt** (`PasswordEncoder` — hash das passwords)
- **JWT** (`JwtService` + `JwtAuthenticationFilter`; *claim* `id_empresa` resolve o tenant)

### Notas
- `ADMIN_SISTEMA` é administrador de **plataforma** (gere empresas/tenants), **não** gere
  condomínios diretamente — por isso os endpoints com escopo de tenant (condomínios, quotas…)
  devolvem `403` para esta conta. Para gerir condomínios use uma conta de **gestor**.

---

## Próximos módulos
Ver também `docs/MODULES_ROADMAP.md` e `docs/TASKS.md` para o roadmap completo (Fases 0–22),
já maioritariamente implementado (estrutura física, financeiro, governação, operação,
dashboard, relatórios e auditoria).
