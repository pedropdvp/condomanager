-- =====================================================================
-- V7 — Quotas (mensalidade por fração; multi-tenant)
-- O valor é calculado por permilagem sobre o orçamento anual
-- (ver docs/LEGAL_RULES.md §2 e docs/SPEC.md item B).
-- =====================================================================

CREATE TABLE quota (
    id_quota   BIGINT        NOT NULL AUTO_INCREMENT,
    id_empresa BIGINT        NOT NULL,
    id_fracao  BIGINT        NOT NULL,
    mes        INT           NOT NULL,
    ano        INT           NOT NULL,
    valor      DECIMAL(10,2) NOT NULL,
    estado     VARCHAR(20)   NOT NULL DEFAULT 'PENDENTE',
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_quota PRIMARY KEY (id_quota),
    CONSTRAINT fk_quota_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_quota_fracao FOREIGN KEY (id_fracao) REFERENCES fracao (id_fracao),
    CONSTRAINT uk_quota_fracao_periodo UNIQUE (id_fracao, mes, ano),
    INDEX idx_quota_empresa (id_empresa),
    INDEX idx_quota_fracao (id_fracao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
