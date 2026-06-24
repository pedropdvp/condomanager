#!/usr/bin/env bash
# =====================================================================
# Deploy/atualização do CondoManager via Docker Compose (produção).
# Pré-requisitos no servidor Ubuntu: docker, docker compose, ficheiro .env.prod.
# Executar a partir da raiz do projeto:  ./deploy/scripts/deploy.sh
# =====================================================================
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT"
COMPOSE="docker compose -f deploy/docker-compose.prod.yml --env-file .env.prod"

if [[ ! -f .env.prod ]]; then
    echo "ERRO: .env.prod não encontrado. Copie .env.example e preencha os segredos de produção." >&2
    exit 1
fi

echo "[deploy] A reconstruir a imagem da aplicação..."
$COMPOSE build app

echo "[deploy] A arrancar/atualizar a stack..."
$COMPOSE up -d

echo "[deploy] A aguardar o healthcheck da aplicação..."
for i in $(seq 1 30); do
    if curl -fsS http://127.0.0.1:8080/api/v1/health >/dev/null 2>&1; then
        echo "[deploy] Aplicação saudável. Deploy concluído."
        exit 0
    fi
    sleep 5
done

echo "ERRO: a aplicação não respondeu ao healthcheck a tempo. Ver logs:" >&2
echo "  $COMPOSE logs --tail=100 app" >&2
exit 1
