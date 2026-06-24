-- =====================================================================
-- V15 — Ocorrências (pedidos/incidentes; multi-tenant)
-- Acrescenta id_utilizador_responsavel (atribuição) além do schema base.
-- =====================================================================

CREATE TABLE ocorrencia (
    id_ocorrencia            BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa               BIGINT       NOT NULL,
    id_condominio            BIGINT       NOT NULL,
    id_condomino             BIGINT,
    id_utilizador_responsavel BIGINT,
    titulo                   VARCHAR(255) NOT NULL,
    descricao                TEXT,
    estado                   VARCHAR(20)  NOT NULL DEFAULT 'ABERTA',
    prioridade               VARCHAR(20)  NOT NULL DEFAULT 'MEDIA',
    created_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_ocorrencia PRIMARY KEY (id_ocorrencia),
    CONSTRAINT fk_ocorrencia_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_ocorrencia_condominio FOREIGN KEY (id_condominio) REFERENCES condominio (id_condominio),
    CONSTRAINT fk_ocorrencia_condomino FOREIGN KEY (id_condomino) REFERENCES condomino (id_condomino),
    CONSTRAINT fk_ocorrencia_responsavel FOREIGN KEY (id_utilizador_responsavel) REFERENCES utilizador (id_utilizador),
    INDEX idx_ocorrencia_empresa (id_empresa),
    INDEX idx_ocorrencia_condominio (id_condominio)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
