-- =====================================================================
-- V18 — Recuperação de password: tokens de reposição
-- =====================================================================

CREATE TABLE password_reset_token (
    id_token      BIGINT       NOT NULL AUTO_INCREMENT,
    token         VARCHAR(100) NOT NULL,
    id_utilizador BIGINT       NOT NULL,
    expira_em     DATETIME     NOT NULL,
    usado         BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_password_reset_token PRIMARY KEY (id_token),
    CONSTRAINT uk_password_reset_token UNIQUE (token),
    CONSTRAINT fk_prt_utilizador FOREIGN KEY (id_utilizador) REFERENCES utilizador (id_utilizador)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
