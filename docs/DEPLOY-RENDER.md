# DEPLOY GRÁTIS — Render + MySQL gerido (passo a passo)

Põe o **sistema real** (Spring Boot + MySQL) online de graça. Resumo:
**1)** criar um MySQL grátis → **2)** ligar a Render ao repositório GitHub → **3)** preencher as
variáveis de ambiente. A Render constrói a imagem pelo `Dockerfile` e aplica as migrações Flyway.

> ⚠️ **Limitações do plano grátis:** o serviço **adormece** após ~15 min sem tráfego (o 1.º
> acesso seguinte demora ~30–60s a acordar). Adequado a demonstração/testes, não a produção.

---

## Passo 1 — Base de dados MySQL grátis

Escolhe **uma** opção e guarda host / porta / utilizador / password / nome da BD.

### Opção A — TiDB Cloud Serverless (recomendada; MySQL 8 compatível, persistente)
1. Conta em <https://tidbcloud.com> → **Create Cluster** → *Serverless* (grátis).
2. **Connect** → cria utilizador/password → cria a base de dados `condomanager`.
3. Anota: `HOST` (ex.: `gateway01.eu-central-1.prod.aws.tidbcloud.com`), `PORT` `4000`,
   `USER`, `PASSWORD`.
4. TiDB exige TLS — usar este `DB_PARAMS` (Passo 3):
   `sslMode=VERIFY_IDENTITY&serverTimezone=UTC`

### Opção B — Aiven for MySQL (free trial)
1. <https://aiven.io> → criar serviço **MySQL** grátis. Anota host/porta/user/password.
2. Criar a BD `condomanager`. `DB_PARAMS`: `sslMode=REQUIRE&serverTimezone=UTC`

---

## Passo 2 — Criar o serviço na Render

1. Conta em <https://render.com> (entra com o GitHub **pedropdvp**).
2. **New** → **Blueprint** → escolhe o repositório `condomanager` (a Render lê o `render.yaml`).
   - (Alternativa sem blueprint: **New → Web Service → Docker**, apontando ao mesmo repo.)
3. A Render deteta o `Dockerfile` e o plano **free**.

---

## Passo 3 — Variáveis de ambiente (no painel da Render)

`JWT_SECRET` é gerado automaticamente. Preenche os restantes com os dados do Passo 1:

| Variável | Valor |
|---|---|
| `DB_HOST` | host do MySQL (ex.: `gateway01...tidbcloud.com`) |
| `DB_PORT` | `4000` (TiDB) ou a porta indicada |
| `DB_NAME` | `condomanager` |
| `DB_USER` | utilizador do MySQL |
| `DB_PASSWORD` | password do MySQL |
| `DB_PARAMS` | `sslMode=VERIFY_IDENTITY&serverTimezone=UTC` (TiDB) |
| `MAIL_ENABLED` | `false` (ou `true` + `MAIL_*` para emails reais) |

Clica **Create / Deploy**. No primeiro arranque, o Flyway cria o esquema (`V1..V21`).

---

## Passo 4 — Aceder

O URL público é `https://condomanager.onrender.com` (ou o nome que deres). Testa:
- App: `https://<o-teu-serviço>.onrender.com/`
- Landing: `.../landing.html`
- Health: `.../api/v1/health` → deve responder `{"status":"UP"}`

> O perfil `prod` **não semeia dados** (sem login demo). Para criar o 1.º utilizador, regista
> uma empresa/admin via API, ou (só para demonstração) define temporariamente
> `SPRING_PROFILES_ACTIVE=dev` para semear `admin@condomanager.local / admin123`.

---

## Atualizações futuras
Com `autoDeploy: true`, cada `git push` para o branch principal redeploya automaticamente.
