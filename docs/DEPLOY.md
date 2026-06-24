# DEPLOY — CondoManager (Fase 21)

Guia de instalação em produção: **Ubuntu + MySQL 8 + Nginx + SSL + Backup**.
Cumpre o RNF de deploy do `SPEC.md` §7 e a Fase 21 do `TASKS.md`.

Artefactos (na raiz do projeto e em `deploy/`):

| Ficheiro | Função |
|---|---|
| `Dockerfile` | Imagem de produção multi-stage (build Maven → runtime JRE 21). |
| `src/main/resources/application-prod.yml` | Perfil `prod` (sem dados semeados; segredos por ambiente). |
| `deploy/docker-compose.prod.yml` | Stack de produção (app + MySQL). |
| `deploy/nginx/condomanager.conf` | Reverse proxy + TLS. |
| `deploy/systemd/condomanager.service` | Execução como serviço (alternativa sem Docker). |
| `deploy/scripts/deploy.sh` | Build + arranque + verificação de saúde. |
| `deploy/scripts/backup.sh` | `mysqldump` com rotação. |

---

## Opção A — Docker Compose (recomendada)

### 1. Preparar o servidor (Ubuntu 22.04+)
```bash
sudo apt update && sudo apt install -y docker.io docker-compose-plugin nginx certbot python3-certbot-nginx
sudo systemctl enable --now docker
```

### 2. Obter o código e configurar segredos
```bash
git clone <repo> /opt/condomanager && cd /opt/condomanager
cp .env.example .env.prod
# Editar .env.prod: DB_PASSWORD, DB_ROOT_PASSWORD, JWT_SECRET (>=32 chars), MAIL_*
```

### 3. Arrancar a stack
```bash
./deploy/scripts/deploy.sh
```
O Flyway aplica as migrações `V1..V21` automaticamente no primeiro arranque.
A app fica exposta apenas em `127.0.0.1:8080` (o Nginx faz o proxy).

---

## Opção B — Jar nativo + systemd
```bash
mvn -DskipTests clean package
sudo useradd --system --home /opt/condomanager condomanager
sudo mkdir -p /opt/condomanager/data /etc/condomanager
sudo cp target/condomanager-*.jar /opt/condomanager/condomanager.jar
# Criar /etc/condomanager/condomanager.env com DB_*, JWT_SECRET, MAIL_* (chmod 600)
sudo cp deploy/systemd/condomanager.service /etc/systemd/system/
sudo systemctl daemon-reload && sudo systemctl enable --now condomanager
```

---

## Nginx + SSL (Let's Encrypt)
```bash
sudo cp deploy/nginx/condomanager.conf /etc/nginx/sites-available/condomanager
# Substituir condomanager.example.com pelo domínio real
sudo ln -s /etc/nginx/sites-available/condomanager /etc/nginx/sites-enabled/
sudo mkdir -p /var/www/certbot
sudo nginx -t && sudo systemctl reload nginx
# Emitir o certificado (preenche automaticamente o bloco 443):
sudo certbot --nginx -d condomanager.example.com
```
A renovação é automática (timer do Certbot). HSTS já está ativo na config.

---

## Backup automático
```bash
sudo cp deploy/scripts/backup.sh /usr/local/bin/condomanager-backup
sudo chmod +x /usr/local/bin/condomanager-backup
# Cron diário às 03:00 (segredos via /etc/condomanager/condomanager.env):
echo '0 3 * * * root . /etc/condomanager/condomanager.env && DB_HOST=127.0.0.1 /usr/local/bin/condomanager-backup' \
  | sudo tee /etc/cron.d/condomanager-backup
```
Restauro: `gunzip < backup.sql.gz | mysql -u root -p condomanager`.

---

## Checklist de produção
- [ ] `JWT_SECRET` forte (≥ 32 caracteres) e único — **a app `prod` não arranca sem ele**.
- [ ] `MAIL_ENABLED=true` + credenciais SMTP (recuperação de password e convocatórias).
- [ ] Firewall: expor apenas 80/443; MySQL e app só no loopback/rede interna.
- [ ] Backups a correr e **testados** (restauro validado).
- [ ] DNS do domínio a apontar para o servidor antes do `certbot`.

> **Testes de integração com BD**: `mvn verify -Pintegration` (Testcontainers).
> Em ambientes onde o docker-java não acede ao Docker, apontar para um MySQL existente:
> `mvn verify -Pintegration -Dit.datasource.url=jdbc:mysql://localhost:3307/condomanager`.
