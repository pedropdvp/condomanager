-- =====================================================================
-- V20 — Liga um utilizador (perfil CONDOMINO) a um registo de condómino,
-- permitindo o auto-voto nas votações.
--
-- Nota: a coluna e a chave estrangeira são adicionadas em ALTER separados.
-- O TiDB (e o modo strict de alguns motores) não suporta adicionar a coluna
-- e a FK que a referencia no mesmo ALTER (erro 1072). Em dois passos funciona
-- tanto no MySQL como no TiDB.
-- =====================================================================

ALTER TABLE utilizador
    ADD COLUMN id_condomino BIGINT;

ALTER TABLE utilizador
    ADD CONSTRAINT fk_utilizador_condomino FOREIGN KEY (id_condomino) REFERENCES condomino (id_condomino);
