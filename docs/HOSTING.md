# Hosting, limitações e escalabilidade

## Estado atual (gratuito)

- **App:** Render (Web Service Docker, plano **free**) — https://condomanager.onrender.com
- **Base de dados:** TiDB Cloud Serverless (compatível com MySQL 8), TLS.
- **Email:** API HTTP da **Resend** (porta 443) — o Render free **bloqueia portas SMTP**.
- **Keep-alive:** GitHub Actions (`.github/workflows/keepalive.yml`) faz *ping* ao
  `/api/v1/health` a cada ~12 min para reduzir o *cold start*.

## Limitações do plano free (assumidas)

| Limitação | Impacto | Mitigação atual |
|-----------|---------|-----------------|
| Adormece após ~15 min sem tráfego | 1.º acesso demora ~30-60 s (cold start) | Keep-alive a cada ~12 min; barra de carregamento na UI |
| Portas SMTP bloqueadas | Não dá para enviar email por SMTP | Envio via Resend (HTTP) |
| Sem backups geridos da BD | Risco de perda de dados | TiDB Serverless tem snapshots; ver abaixo |
| Recursos limitados (CPU/RAM) | Relatórios Jasper podem ser lentos a frio | Templates compilados em cache |

## Caminhos de escalabilidade (quando se justificar)

1. **Render pago (Starter+):** sem *sleep*, mais CPU/RAM. Só mudar o plano no painel; o
   `render.yaml` e o Dockerfile mantêm-se. **Mais simples.**
2. **Fly.io / Railway:** Docker semelhante; juntar uma **BD gerida** (Postgres/MySQL) com
   *backups automáticos*. Migração de dados via `mysqldump`/Flyway.
3. **BD gerida com backups:** TiDB Cloud (plano pago) ou Aiven/PlanetScale — *point-in-time
   recovery* e réplicas.

## Observabilidade (já disponível)

- `/actuator/health` (público), `/actuator/info`, `/actuator/metrics`, `/actuator/prometheus`
  (protegidos). Integrável com Prometheus/Grafana num plano pago.

## Variáveis de ambiente (Render)

`DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD`, `DB_PARAMS`, `JWT_SECRET`,
`SPRING_PROFILES_ACTIVE`, `MAIL_ENABLED`, `RESEND_API_KEY`, `MAIL_FROM`,
`app.notificacoes.lembretes.enabled` (lembretes agendados, off por defeito).

> Decisão atual: **manter o plano free + keep-alive** (sem custos). Este documento serve
> de guia para quando se optar por um ambiente pago/gerido.
