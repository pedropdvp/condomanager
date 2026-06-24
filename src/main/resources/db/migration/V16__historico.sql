-- =====================================================================
-- V16 — Auditoria: histórico imutável de operações (multi-tenant)
-- Registo append-only (sem UPDATE/DELETE pela aplicação).
-- id_empresa é nulo para eventos de nível de sistema (ex.: ADMIN_SISTEMA).
-- =====================================================================

CREATE TABLE historico (
    id_historico BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa   BIGINT,
    utilizador   VARCHAR(150),
    operacao     VARCHAR(255) NOT NULL,
    data_hora    DATETIME     NOT NULL,
    CONSTRAINT pk_historico PRIMARY KEY (id_historico),
    CONSTRAINT fk_historico_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    INDEX idx_historico_empresa (id_empresa),
    INDEX idx_historico_data (data_hora)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
