-- =====================================================================
-- V14 — Comunicação: mensagens internas (multi-tenant)
-- Suporta INDIVIDUAL (destino) e BROADCAST (todos os utilizadores da empresa).
-- GRUPO exigirá uma tabela de destinatários (fase futura).
-- =====================================================================

CREATE TABLE mensagem (
    id_mensagem           BIGINT       NOT NULL AUTO_INCREMENT,
    id_empresa            BIGINT       NOT NULL,
    tipo                  VARCHAR(20)  NOT NULL,
    assunto               VARCHAR(200) NOT NULL,
    conteudo              TEXT,
    data_envio            DATETIME     NOT NULL,
    id_utilizador_origem  BIGINT       NOT NULL,
    id_utilizador_destino BIGINT,
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_mensagem PRIMARY KEY (id_mensagem),
    CONSTRAINT fk_mensagem_empresa FOREIGN KEY (id_empresa) REFERENCES empresa_gestao (id_empresa),
    CONSTRAINT fk_mensagem_origem FOREIGN KEY (id_utilizador_origem) REFERENCES utilizador (id_utilizador),
    CONSTRAINT fk_mensagem_destino FOREIGN KEY (id_utilizador_destino) REFERENCES utilizador (id_utilizador),
    INDEX idx_mensagem_empresa (id_empresa),
    INDEX idx_mensagem_destino (id_utilizador_destino),
    INDEX idx_mensagem_origem (id_utilizador_origem)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
