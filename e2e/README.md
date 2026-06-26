# Testes E2E (Playwright)

Smoke tests de ponta-a-ponta do CondoManager, executados contra a aplicação publicada
(usam a conta de demonstração `gestor.alfa@demo.local`).

## Correr

```bash
cd e2e
npm install
npx playwright install chromium
npm test            # usa BASE_URL=https://condomanager.onrender.com por defeito
```

Para apontar a outra instância (ex.: local):

```bash
BASE_URL=http://localhost:8080 npm test
```

## O que é verificado (`smoke.mjs`)

- Login + dashboard (KPIs e gráficos).
- Atas: criação reflete-se de imediato na lista (read-after-write).
- Ocorrências: criação (estado `ABERTA`).
- Votações: ciclo completo criar → abrir → votar → resultado (contagem por permilagem).
- Mensagens: envio de difusão (broadcast).
- Relatórios: PDF e Excel geram conteúdo.
- Swagger: `/v3/api-docs` responde 200.
- Ausência de erros de consola (`pageerror`).

O processo termina com código `!= 0` se algum teste falhar (adequado a CI).

> Nota: a instância gratuita do Render pode estar a "acordar" no primeiro acesso
> (~30-60s); os testes fazem *polling* para tolerar essa latência.
