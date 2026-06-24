# SPEC — Especificação de Produto

Sistema de Gestão de Condomínios (SGC) — **CondoManager SaaS**

> Este documento descreve **o quê** e **para quem**, não o **como** técnico.
> O "como" está em `ARCHITECTURE.md`, `TECH_STACK.md`, `DATABASE_SCHEMA.md`,
> `CODING_STANDARDS.md`, `SECURITY_RULES.md`, `API_RULES.md`.
> Em caso de conflito, os documentos técnicos prevalecem sobre este.

---

## 1. O que faz (Resumo executivo)

Plataforma **SaaS multi-tenant** que permite a empresas de gestão de condomínios
(e condomínios autogeridos) administrar todo o ciclo de vida de um condomínio
num único sistema web:

- **Estrutura física**: empresas → condomínios → edifícios → frações → condóminos.
- **Financeiro**: emissão de quotas, registo de pagamentos, despesas e orçamento anual.
- **Governação**: reuniões/assembleias, atas, votações digitais.
- **Operação**: ocorrências, comunicação interna, gestão documental.
- **Visibilidade**: dashboard, relatórios (PDF/Excel) e auditoria completa.

Cada **empresa de gestão é um Tenant**; os dados de um tenant são totalmente
isolados dos restantes (`id_empresa` em todas as entidades de negócio).

---

## 2. Para quem é (Público-alvo)

| Persona | Perfil RBAC | Necessidade principal |
|---|---|---|
| Administrador da plataforma | `ROLE_ADMIN_SYSTEM` | Gerir tenants, planos e saúde do sistema |
| Empresa de gestão de condomínios | `ROLE_MANAGER` | Gerir vários condomínios como clientes |
| Funcionário da empresa de gestão | `ROLE_EMPLOYEE` | Executar tarefas operacionais do dia a dia |
| Administrador de condomínio | `ROLE_CONDOMINIUM_ADMIN` | Gerir um condomínio específico |
| Condómino (proprietário/inquilino) | `ROLE_OWNER` | Consultar quotas, documentos, votar, abrir ocorrências |

**Mercado-alvo** (de `PROJECT_CONTEXT.md`):
Empresas de gestão de condomínios, administradores de condomínio e condomínios autogeridos.

---

## 3. Para quem NÃO é (Não-público)

- **Não** é um ERP de contabilidade certificada (SAF-T, faturação legal certificada AT).
  Regista movimentos financeiros, mas não substitui software de faturação certificado.
- **Não** é um portal imobiliário (compra/venda/arrendamento de imóveis).
- **Não** é um sistema de gestão de obras/empreitadas (project management de construção).
- **Não** se destina a uso pessoal por um único proprietário sem estrutura de condomínio.
- **Não** é uma rede social de vizinhos nem um marketplace de serviços.

---

## 4. O que será considerado sucesso (Critérios de sucesso)

### Sucesso de produto
1. Uma empresa consegue, do zero, criar condomínio → edifício → frações → condóminos
   e **emitir quotas mensais** sem intervenção de suporte.
2. Um condómino consegue autenticar-se e **consultar as suas quotas e documentos**.
3. Uma assembleia consegue ser registada e uma **votação digital encerrada com contagem automática**.

### Sucesso técnico (alinhado com os docs)
4. **Isolamento multi-tenant garantido**: nenhum tenant acede a dados de outro (`SAAS_RULES.md`).
5. **Segurança**: passwords em BCrypt, autenticação JWT, autorização RBAC (`SECURITY_RULES.md`).
6. **Qualidade**: arquitetura Controller→Service→Repository, DTO Pattern, cobertura de testes ≥ 70% (`CODING_STANDARDS.md`).
7. **Auditoria imutável**: todas as operações relevantes registadas e não alteráveis (`DOMAIN_MODEL.md`).
8. **API** REST versionada em `/api/v1`, respostas JSON (`API_RULES.md`).
9. **UI** responsiva (mobile-first), light/dark, WCAG 2.1 (`UI_UX_RULES.md`).

### Métrica de aceitação mínima (MVP)
- Fases 1–10 do `MODULES_ROADMAP.md` funcionais ponta-a-ponta (Autenticação → Pagamentos).

---

## 5. Fora de âmbito (Out of scope)

Explicitamente **fora** desta versão (podem ser fases futuras):

- Integração com **gateways de pagamento reais** (MBWay/Multibanco/PayPal a nível de
  transação efetiva). *Estado atual: a decidir — ver secção 8, item A.*
- Contabilidade certificada / SAF-T / comunicação à Autoridade Tributária.
- Aplicações móveis nativas (iOS/Android). O acesso é via web responsiva.
- Integração com fechaduras/IoT, contadores de água/energia ou domótica.
- Tradução multi-idioma (i18n). MVP em **Português (PT)**.
- Assinatura digital qualificada de atas (Chave Móvel Digital / certificados).
- Faturação automática de subscrição da própria plataforma (billing do SaaS).
- Tecnologias proibidas em `TECH_STACK.md` (PHP, Python, Node, React, etc.).

---

## 6. Âmbito funcional (módulos)

Segue a ordem de `MODULES_ROADMAP.md`. Cada módulo expõe CRUD via REST `/api/v1`,
com DTOs, validação `jakarta.validation` e filtro obrigatório por `id_empresa`.

1. **Autenticação & Segurança** — login/logout, JWT, BCrypt, recuperação de password, perfis/permissões.
2. **Empresas de Gestão** — tenant raiz; CRUD; estado ativo/inativo.
3. **Condomínios** — CRUD, pesquisa, filtros; orçamento anual.
4. **Edifícios** — CRUD; blocos e nº de pisos.
5. **Frações** — CRUD; tipologia, permilagem, área.
6. **Condóminos** — CRUD; proprietário/inquilino; associação a fração.
7. **Utilizadores** — contas, perfis (M:N), permissões.
8. **Pagamentos** — geração mensal de quotas, registo de pagamentos, estados.
9. **Despesas** — CRUD, categorias, aprovação.
10. **Documentos** — upload/download/pesquisa.
11. **Atas** — CRUD, arquivo.
12. **Reuniões** — agendamento, convocatórias.
13. **Votações** — criar/abrir/encerrar, contagem automática.
14. **Comunicação** — mensagens individuais/grupo/broadcast, notificações.
15. **Ocorrências** — registo, atribuição, encerramento (estados/prioridades).
16. **Dashboard** — indicadores, estatísticas, gráficos.
17. **Relatórios** — JasperReports → PDF/Excel.
18. **Auditoria** — histórico imutável de operações.

---

## 7. Requisitos não funcionais

- **Multi-tenant**: discriminador `id_empresa` em todas as entidades de negócio; filtragem automática.
- **Segurança**: Spring Security + JWT + BCrypt; RBAC com os 5 perfis definidos.
- **Performance**: `FetchType.LAZY` por defeito; paginação nas listagens.
- **Manutenção**: SOLID, Clean Code, DRY/KISS; métodos ≤ 30 linhas, classes ≤ 300 linhas.
- **Observabilidade**: logging SLF4J; nunca `System.out`/`printStackTrace`.
- **Erros**: `@RestControllerAdvice` + `GlobalExceptionHandler`; respostas de erro JSON consistentes.
- **Testes**: JUnit 5 + Mockito, cobertura mínima 70%.
- **UI/UX**: Bootstrap 5, mobile-first, light/dark, WCAG 2.1.
- **Deploy** (alvo): Linux Ubuntu, MySQL, Nginx, SSL, backups.

---

## 8. Decisões confirmadas e questões em aberto

> **Legenda da coluna "Fonte"** — proveniência de cada item, para distinguir o que
> já existia no projeto do que é proposta minha a validar:
> - 📄 **Ancorado em docs** — baseado em conteúdo já existente na pasta `docs/`.
> - 🧩 **Parcial** — parte vem dos docs, parte é inferência minha.
> - 💡 **Inferência** — proposta minha por conhecimento do domínio; não existia nos docs.
> - 👤 **Utilizador** — decisão dada por ti nesta sessão.

### 8.1 Decisões confirmadas (2026-06-21)

| # | Tema | Decisão | Fonte |
|---|---|---|---|
| A | Pagamentos | **Registo manual** no MVP. Sem gateway real; integração MBWay/Multibanco/PayPal fica fora de âmbito (fase futura). | 👤 Utilizador |
| B | Cálculo de quotas | **Por permilagem (base 1000)**: `quota_anual = orcamento_anual × (permilagem ÷ 1000)`; `quota_mensal = quota_anual ÷ 12`. A quota decompõe-se em quota corrente + **Fundo de Reserva (≥10%)**. Ver `LEGAL_RULES.md` §2-3. | 👤 Utilizador + 📄 `LEGAL_RULES.md` (Art.º 1424.º) |
| C | Peso do voto | **Por permilagem** da fração (regime da propriedade horizontal). | 👤 Utilizador |
| D | Quórum e maiorias | **Definidos por lei** (Lei 8/2022): quórum 50% (1.ª) / 25% (2.ª conv.); maiorias Simples / Sem Oposição / 2/3 / Unanimidade. Motor de votação soma **permilagem**. Ver `LEGAL_RULES.md` §5-6. | 📄 `LEGAL_RULES.md` (Art.º 1424.º, 1425.º) |
| E | Armazenamento de ficheiros | **Filesystem local**; caminho guardado em BD (`ficheiro VARCHAR`). | 👤 Utilizador (campo `ficheiro` em `DATABASE_SCHEMA.md`) |
| I | Legislação aplicável | **Documentada** em `LEGAL_RULES.md` (Código Civil PT, propriedade horizontal, Lei 8/2022). | 👤 Utilizador (PDF fornecido) |

### 8.2 Questões ainda em aberto

> Dependem de decisão de negócio ou de documentação a fornecer.
> Cada uma tem um **valor por defeito** assumido caso não haja indicação.

| # | Tema | Questão | Default assumido | Fonte |
|---|---|---|---|---|
| F | Notificações | Email (SMTP) e/ou notificações in-app? Que servidor SMTP? **Nota:** convocatória, notificação de deliberações e declaração de dívida são legalmente admissíveis por email (`LEGAL_RULES.md` §4) — prioridade subiu. | In-app no MVP; email logo que haja SMTP definido | 🧩 Parcial (`LEGAL_RULES.md` §4, `MODULES_ROADMAP.md` Fase 16) |
| G | Recuperação de password | Por email com token? Requer SMTP. | Token + email (depende de F) | 🧩 Parcial (`TASKS.md` Fase 3) |
| H | Estados/ENUMs de pagamento | Confirmar lista final de métodos e estados | Conforme `DOMAIN_MODEL.md` | 📄 Ancorado em docs (`DOMAIN_MODEL.md`) |

---

## 9. Glossário rápido

- **Tenant** = Empresa de gestão (`empresa_gestao`).
- **Permilagem** = peso relativo da fração no condomínio (‰); base de rateio e de voto.
- **Quota** = mensalidade emitida a uma fração.
- **Ata** = documento oficial de uma reunião/assembleia.
- **Ocorrência** = pedido/incidente registado por um condómino.
