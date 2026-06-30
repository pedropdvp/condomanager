# 🎤 Guião de Apresentação — CondoManager

Duração sugerida: **10-15 min**. App ao vivo: <https://condomanager.onrender.com>
(abre o link ~1 min antes para "acordar" a instância).

---

## 1. O problema (1 min)

A gestão de condomínios em Portugal é trabalhosa e regulada (Lei 8/2022): cálculo de
quotas por **permilagem**, **votações com maiorias** específicas, **fundo de reserva
obrigatório**, atas, convocatórias, cobranças. Faz-se muito em papel/Excel.

## 2. A solução (1 min)

**CondoManager** — um **SaaS multi-tenant** que centraliza tudo: condomínios, quotas e
pagamentos, despesas e tesouraria, reuniões/atas/votações, ocorrências, documentos,
comunicação e um **portal self-service para o condómino** — com as **regras legais embebidas**.

## 3. Demonstração ao vivo (6-8 min)

> Tema escuro (🌓) realça os gráficos. Se o 1.º clique demorar, é o cold start do plano free.

### a) Gestor — `gestor.alfa@demo.local` / `gestor123`
1. **Dashboard** — KPIs + gráficos (estrutura, quotas por estado, valor, ocorrências).
2. **Condomínios → Visualizar** um condomínio:
   - **Frações** — gráfico "Permilagens de cada Fração".
   - **Quotas** — gerar quotas; **Pagar** e **Pagar online** (referência Multibanco simulada → confirmar).
   - **Despesas** — criar/aprovar; **Balancete** — receitas/despesas/saldo + **fundo de reserva (10%)**; exportar **PDF**.
   - **Reuniões** — **Convocar** (mostra a lista de condóminos contactados por email); **Atas** — nova ata + anexar ficheiro.
   - **Ocorrências** — registar, alterar estado, atribuir; relatório **PDF/Excel**.
3. **Votações** — criar → **Abrir** → **Votar** (por condómino) → **Resultado** (contagem por **permilagem**, aprovado/reprovado).
4. **Mensagens** — enviar difusão (broadcast).

### b) Administrador — `admin@condomanager.local` / `admin123`
1. **Utilizadores** — criar acesso; **associar a um condómino**.
2. **Permissões** — matriz **RBAC granular** (perfil × funcionalidade × ação), persistida e aplicada.
3. **Auditoria** — histórico imutável (pesquisa, filtro por método, paginação, exportar CSV).
4. **Empresas** — gestão de *tenants* (multi-tenant).

### c) Condómino (Portal self-service) — `portal.demo@demo.local` / `portal123`
1. **A minha área** — o meu condomínio/fração.
2. **As minhas quotas** — pagar online.
3. **Votações abertas** — **votar** diretamente (voto conta pela permilagem da minha fração).
4. **A minha conta → RGPD** — exportar / apagar os meus dados.

## 4. Destaques técnicos (2-3 min)

- **Multi-tenant** (isolamento por `id_empresa` + filtro Hibernate; tenant no JWT).
- **RBAC granular** persistente, aplicado em 10 módulos via `@PreAuthorize`.
- **Regras legais** (Lei 8/2022): permilagem, maiorias/quórum, **fundo de reserva ≥10%**.
- **Segurança**: JWT + BCrypt, **rate-limiting** no login (429), **RGPD** (exportar/apagar).
- **Relatórios** JasperReports (PDF/Excel) com template genérico reutilizável.
- **Observabilidade**: Spring Actuator (`/actuator/health|metrics|prometheus`).
- **API documentada** (Swagger), **testes** unitários + **smoke E2E (Playwright)**.
- **Deploy** Docker → Render + TiDB; email por **Resend** (HTTP, porque o free bloqueia SMTP);
  **keep-alive** por GitHub Actions.

## 5. O que demonstra valor

Resolve um problema real e regulado, com uma arquitetura **profissional e escalável**
(multi-tenant, RBAC, observabilidade, CI/testes), **publicada e a funcionar** — e com
caminho claro de evolução (gateway de pagamentos real, app móvel, contabilidade avançada).

## 6. Perguntas prováveis (preparação)

- **Como garantem o isolamento entre condomínios/empresas?** Discriminador `id_empresa` +
  filtro Hibernate ativado por pedido; o tenant vem do *claim* do JWT.
- **Os pagamentos são reais?** O fluxo está pronto (*scaffolding* com referência Multibanco);
  basta ligar Stripe/SIBS (PaymentIntent + webhook).
- **E a conformidade legal?** Permilagem, maiorias, fundo de reserva e convocatórias seguem a
  Lei 8/2022 (ver `docs/LEGAL_RULES.md`).
- **Escala?** Documentado em `docs/HOSTING.md` (plano pago / BD gerida com backups).
