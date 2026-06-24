-- =====================================================================
-- V19 — Estado de leitura das mensagens (lida/não-lida)
-- Aplica-se a mensagens INDIVIDUAL; as BROADCAST não têm leitura individual.
-- =====================================================================

ALTER TABLE mensagem
    ADD COLUMN lida BOOLEAN NOT NULL DEFAULT FALSE;
