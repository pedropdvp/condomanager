-- =====================================================================
-- V8 — Pagamentos (liquidação de quotas; registo manual; multi-tenant)
-- =====================================================================

CREATE TABLE pagamento (
    id_pagamento   BIGINT        NOT NULL AUTO_INCREMENT,
    id_empresa     BIGINT        NOT NULL,
    id_quota       BIGINT        NOT NULL,
    valor          DECIMAL(10,2) NOT NULL,
    data_pagamento DATETIME      NOT NULL,
    metodo         VARCHAR(50)   NOT NULL,
    estado         VARCHAR(50)   NOT NULL DEFAULT 'CONFIRMADO',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_pagamento PRIMARY KEY (id_pagamento),
    CONSTRAINT fk_pagamento_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_pagamento_quota FOREIGN KEY (id_quota) REFERENCES quota (id_quota),
    INDEX idx_pagamento_empresa (id_empresa),
    INDEX idx_pagamento_quota (id_quota)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
