-- =====================================================================
-- V17 — Workflow de aprovação de despesas (Fase 9)
-- Acrescenta o estado de aprovação à despesa (PENDENTE/APROVADA/REJEITADA).
-- =====================================================================

ALTER TABLE despesa
    ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'PENDENTE';
