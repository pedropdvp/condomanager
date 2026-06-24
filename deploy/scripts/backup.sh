#!/usr/bin/env bash
# =====================================================================
# Backup da base de dados CondoManager (mysqldump) com rotação.
# Agendar via cron (ver deploy/DEPLOY.md), ex.: diariamente às 03:00.
# =====================================================================
set -euo pipefail

DB_NAME="${DB_NAME:-condomanager}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:?defina DB_PASSWORD}"
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
BACKUP_DIR="${BACKUP_DIR:-/var/backups/condomanager}"
RETENTION_DAYS="${RETENTION_DAYS:-14}"

timestamp="$(date +%Y%m%d-%H%M%S)"
mkdir -p "$BACKUP_DIR"
outfile="$BACKUP_DIR/condomanager-$timestamp.sql.gz"

echo "[backup] A exportar $DB_NAME -> $outfile"
mysqldump \
    --host="$DB_HOST" --port="$DB_PORT" \
    --user="$DB_USER" --password="$DB_PASSWORD" \
    --single-transaction --quick --routines --triggers \
    "$DB_NAME" | gzip -9 > "$outfile"

echo "[backup] Concluído ($(du -h "$outfile" | cut -f1))"

# Rotação: remove backups com mais de RETENTION_DAYS dias.
find "$BACKUP_DIR" -name 'condomanager-*.sql.gz' -mtime "+$RETENTION_DAYS" -delete
echo "[backup] Rotação concluída (retenção: ${RETENTION_DAYS} dias)"
