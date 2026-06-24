-- =====================================================================
-- V21 — Mensagens de GRUPO: destinatários explícitos
-- =====================================================================

CREATE TABLE mensagem_destinatario (
    id_mensagem   BIGINT NOT NULL,
    id_utilizador BIGINT NOT NULL,
    CONSTRAINT pk_mensagem_destinatario PRIMARY KEY (id_mensagem, id_utilizador),
    CONSTRAINT fk_md_mensagem FOREIGN KEY (id_mensagem) REFERENCES mensagem (id_mensagem) ON DELETE CASCADE,
    CONSTRAINT fk_md_utilizador FOREIGN KEY (id_utilizador) REFERENCES utilizador (id_utilizador)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
