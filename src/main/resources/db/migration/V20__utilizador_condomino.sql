-- =====================================================================
-- V20 — Liga um utilizador (perfil CONDOMINO) a um registo de condómino,
-- permitindo o auto-voto nas votações.
-- =====================================================================

ALTER TABLE utilizador
    ADD COLUMN id_condomino BIGINT,
    ADD CONSTRAINT fk_utilizador_condomino FOREIGN KEY (id_condomino) REFERENCES condomino (id_condomino);
