# Implantação do CondoManager

O frontend estático já está no **GitHub Pages**:
`https://pedropdvp.github.io/condomanager/`

Falta pôr o **backend** (Spring Boot + MySQL) na cloud e ligar o frontend a ele.

---

## 1. Variáveis de ambiente do backend

| Variável | Descrição | Exemplo |
|----------|-----------|---------|
| `SPRING_PROFILES_ACTIVE` | Perfil (use `prod` para MySQL) | `prod` |
| `DB_HOST` | Host do MySQL | `containers-us-...railway.app` |
| `DB_PORT` | Porta do MySQL | `3306` |
| `DB_NAME` | Nome da base de dados | `condomanager` |
| `DB_USER` | Utilizador da BD | `condomanager` |
| `DB_PASSWORD` | Password da BD | *(segredo)* |
| `CONDO_JWT_SECRET` | Chave JWT (≥ 32 bytes) | *(segredo — ver abaixo)* |
| `CONDO_CORS_ORIGINS` | Origens permitidas (frontend) | `https://pedropdvp.github.io` |
| `PORT` | Porta HTTP (injetada pelo PaaS) | `8080` |

Gerar um segredo JWT forte:

```bash
openssl rand -base64 48
```

---

## 2. Opção A — Railway (recomendado: Java + MySQL geridos)

1. Criar conta em https://railway.app e instalar o CLI: `npm i -g @railway/cli` e `railway login`.
2. No projeto: `railway init` e adicionar um plugin **MySQL** (cria `MYSQLHOST`, `MYSQLDATABASE`, etc.).
3. Adicionar um serviço a partir do **GitHub repo** `pedropdvp/condomanager` (raiz do build = pasta `condomanager/`, que tem o `Dockerfile`).
4. Definir as variáveis de ambiente da tabela acima (mapear `DB_*` para os valores do plugin MySQL).
5. Deploy. O Railway constrói o `Dockerfile` e publica um URL, ex.: `https://condomanager-production.up.railway.app`.

## 2. Opção B — Render

1. Conta em https://render.com → **New > Web Service** a partir do repo (Docker, root `condomanager/`).
2. MySQL: o Render não tem MySQL nativo — usar um externo (ex.: Railway, PlanetScale, Aiven) e preencher `DB_*`.
3. Definir as variáveis de ambiente e fazer deploy.

## 2. Opção C — Local (testar o perfil de produção com Docker)

```bash
docker compose up --build   # app + MySQL  ->  http://localhost:8080
```

---

## 3. Ligar o frontend (GitHub Pages) ao backend

1. Editar [`condomanager/src/main/resources/static/config.js`](condomanager/src/main/resources/static/config.js):

   ```js
   window.CONDO_API_BASE = "https://<o-seu-backend>";  // URL do passo 2
   ```

2. Garantir que o backend tem `CONDO_CORS_ORIGINS=https://pedropdvp.github.io`.
3. Commit + push para `master` → o workflow de Pages republica automaticamente.
4. Abrir `https://pedropdvp.github.io/condomanager/` e iniciar sessão
   (`admin@condomanager.com` / `admin123`) — agora autentica contra a cloud.

> Notas de segurança para produção: trocar a password do admin semeado,
> definir `CONDO_JWT_SECRET` próprio e restringir `CONDO_CORS_ORIGINS`.
