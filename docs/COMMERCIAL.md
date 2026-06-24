# COMMERCIAL — Preparação Comercial (Fase 22)

Definição dos planos comerciais, página de apresentação e guião de demonstração.
A landing page pública está em `src/main/resources/static/landing.html` (rota `/landing.html`).

> **Nota de âmbito:** o *billing* da própria subscrição (cobrança automática dos planos)
> está **fora de âmbito** do MVP (ver `SPEC.md` §5). Os planos abaixo são a oferta
> comercial e os limites são aplicados ao nível de produto/contrato.

---

## 1. Planos

| | **Starter** | **Business** | **Enterprise** |
|---|---|---|---|
| Público | Condomínios autogeridos / pequenos administradores | Empresas de gestão | Grandes operações |
| Preço (ref.) | 29 €/mês | 89 €/mês | Sob consulta |
| Condomínios | até 3 | até 25 | ilimitados |
| Frações | até 60 | ilimitadas | ilimitadas |
| Quotas, pagamentos, despesas | ✅ | ✅ | ✅ |
| Documentos e ocorrências | ✅ | ✅ | ✅ |
| Assembleias + convocatórias email | — | ✅ | ✅ |
| Votações digitais | — | ✅ | ✅ |
| Relatórios | PDF | PDF + Excel | PDF + Excel |
| Dashboard e auditoria | básico | ✅ | ✅ |
| Suporte | email | email prioritário | SLA dedicado |
| Marca/domínio próprios | — | — | ✅ |

> Os preços são de referência para demonstração comercial, não constituem tabela contratual.

---

## 2. Proposta de valor

- **Conformidade legal** — rateio e voto por permilagem (Art.º 1424.º), maiorias da Lei 8/2022,
  fundo de reserva ≥10%, convocatórias/notificações por email.
- **Tudo num só sistema** — estrutura física, financeiro, governação, operação e visibilidade.
- **SaaS multi-tenant** — isolamento total dos dados por empresa de gestão.
- **Seguro** — JWT, RBAC (5 perfis), BCrypt, auditoria imutável.

---

## 3. Guião de demonstração comercial (~10 min)

1. **Login** como gestor (perfil `dev`): `admin@condomanager.local` / `admin123`.
2. **Criar estrutura**: condomínio → edifício → frações (com permilagem) → condóminos.
3. **Emitir quotas** do mês e **registar um pagamento**.
4. **Agendar uma assembleia** e enviar **convocatória por email** (com `MAIL_ENABLED=true`).
5. **Abrir uma votação**, registar votos e **encerrar** — mostrar a contagem por permilagem.
6. **Gerar relatório de quotas** em **PDF e Excel**.
7. **Dashboard** com indicadores e **auditoria** das operações.

---

## 4. Próximos passos comerciais (pós-MVP)

- Billing automático da subscrição (gateway de pagamento).
- Página de registo *self-service* de novas empresas (trial).
- Métricas de produto (ativação, retenção) e CRM.
