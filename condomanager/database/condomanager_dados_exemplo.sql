-- =====================================================================
--  CondoManager SaaS - Dados de exemplo (seed)
-- ---------------------------------------------------------------------
--  Executar DEPOIS de condomanager_mysql.sql (cria o esquema).
--  No MySQL Workbench: File > Open SQL Script... > Execute (raio).
--
--  Credenciais de acesso criadas:
--    admin@condomanager.com / admin123   (ADMIN_SISTEMA, acesso total)
--    gestor@silvagestao.pt  / admin123   (GESTOR)
--  As passwords sao hashes BCrypt do texto 'admin123'.
-- =====================================================================

USE condomanager;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE utilizador_permissao;
TRUNCATE TABLE utilizador_perfil;
TRUNCATE TABLE historico;
TRUNCATE TABLE mensagem;
TRUNCATE TABLE voto;
TRUNCATE TABLE votacao;
TRUNCATE TABLE reuniao;
TRUNCATE TABLE ata;
TRUNCATE TABLE documento;
TRUNCATE TABLE ocorrencia;
TRUNCATE TABLE pagamento;
TRUNCATE TABLE quota;
TRUNCATE TABLE despesa;
TRUNCATE TABLE condomino;
TRUNCATE TABLE fracao;
TRUNCATE TABLE edificio;
TRUNCATE TABLE condominio;
TRUNCATE TABLE utilizador;
TRUNCATE TABLE perfil;
TRUNCATE TABLE empresa_gestao;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
--  Perfis (roles)
-- ---------------------------------------------------------------------
INSERT INTO perfil (id_perfil, nome, descricao) VALUES
    (1, 'ADMIN_SISTEMA',    'Administrador do Sistema'),
    (2, 'GESTOR',           'Gestor da empresa de administracao'),
    (3, 'FUNCIONARIO',      'Funcionario da empresa de administracao'),
    (4, 'ADMIN_CONDOMINIO', 'Administrador de condominio'),
    (5, 'CONDOMINO',        'Condomino');

-- ---------------------------------------------------------------------
--  Empresa de gestao (tenant)
-- ---------------------------------------------------------------------
INSERT INTO empresa_gestao (id_empresa, nome, nif, email, telefone, morada, estado, plano) VALUES
    (1, 'Silva Gestao de Condominios, Lda.', '501234567', 'geral@silvagestao.pt',
        '220123456', 'Rua das Flores, 100, 4000-001 Porto', 'ATIVA', 'BUSINESS');

-- ---------------------------------------------------------------------
--  Utilizadores  (password = 'admin123' -> hash BCrypt)
-- ---------------------------------------------------------------------
INSERT INTO utilizador (id_utilizador, nome, email, password, ativo, id_empresa) VALUES
    (1, 'Administrador do Sistema', 'admin@condomanager.com',
        '$2b$10$JlcKmsgpblek9yYQgtGfi.2rjnUzW6hIjCSzLAHnKj6ZPNk9HZYGe', 1, NULL),
    (2, 'Joana Silva', 'gestor@silvagestao.pt',
        '$2b$10$JlcKmsgpblek9yYQgtGfi.2rjnUzW6hIjCSzLAHnKj6ZPNk9HZYGe', 1, 1);

-- Atribuicao de perfis
INSERT INTO utilizador_perfil (id_utilizador, id_perfil) VALUES
    (1, 1),   -- admin -> ADMIN_SISTEMA
    (2, 2);   -- Joana -> GESTOR

-- Permissoes granulares do admin: acesso total (todas as funcionalidades x acoes)
INSERT INTO utilizador_permissao (id_utilizador, funcionalidade, acao)
SELECT 1, f.nome, a.nome
FROM
    (SELECT 'EMPRESAS' nome UNION SELECT 'CONDOMINIOS' UNION SELECT 'CONDOMINOS'
     UNION SELECT 'UTILIZADORES' UNION SELECT 'ATAS' UNION SELECT 'PAGAMENTOS'
     UNION SELECT 'REUNIOES' UNION SELECT 'VOTACOES' UNION SELECT 'DOCUMENTOS'
     UNION SELECT 'MENSAGENS') f
CROSS JOIN
    (SELECT 'CRIAR' nome UNION SELECT 'EDITAR' UNION SELECT 'APAGAR' UNION SELECT 'CONSULTAR') a;

-- Gestor: consultar/criar/editar nas funcionalidades operacionais (exemplo)
INSERT INTO utilizador_permissao (id_utilizador, funcionalidade, acao) VALUES
    (2, 'CONDOMINIOS', 'CONSULTAR'), (2, 'CONDOMINIOS', 'EDITAR'),
    (2, 'CONDOMINOS',  'CONSULTAR'), (2, 'CONDOMINOS',  'CRIAR'), (2, 'CONDOMINOS', 'EDITAR'),
    (2, 'PAGAMENTOS',  'CONSULTAR'), (2, 'PAGAMENTOS',  'CRIAR'),
    (2, 'REUNIOES',    'CONSULTAR'), (2, 'REUNIOES',    'CRIAR'), (2, 'REUNIOES', 'EDITAR'),
    (2, 'DOCUMENTOS',  'CONSULTAR'), (2, 'DOCUMENTOS',  'CRIAR'),
    (2, 'MENSAGENS',   'CONSULTAR'), (2, 'MENSAGENS',   'CRIAR');

-- ---------------------------------------------------------------------
--  Condominio
-- ---------------------------------------------------------------------
INSERT INTO condominio (id_condominio, nome, morada, orcamento_anual, id_empresa) VALUES
    (1, 'Condominio Edificio Sol Nascente', 'Av. da Liberdade, 250, 4000-100 Porto', 24000.00, 1);

-- ---------------------------------------------------------------------
--  Edificio
-- ---------------------------------------------------------------------
INSERT INTO edificio (id_edificio, nome, morada, num_pisos, id_condominio) VALUES
    (1, 'Bloco A', 'Av. da Liberdade, 250, 4000-100 Porto', 4, 1);

-- ---------------------------------------------------------------------
--  Fracoes  (permilagem soma 1000)
-- ---------------------------------------------------------------------
INSERT INTO fracao (id_fracao, numero, piso, permilagem, tipologia, id_edificio, id_condominio) VALUES
    (1, '1A', 0, 250.000, 'T2', 1, 1),
    (2, '1B', 1, 250.000, 'T3', 1, 1),
    (3, '2A', 2, 250.000, 'T2', 1, 1),
    (4, '2B', 3, 250.000, 'T3', 1, 1);

-- ---------------------------------------------------------------------
--  Condominos
-- ---------------------------------------------------------------------
INSERT INTO condomino (id_condomino, nome, nif, email, telefone, tipo, id_fracao) VALUES
    (1, 'Antonio Costa',   '210123456', 'antonio.costa@email.pt', '910000001', 'PROPRIETARIO', 1),
    (2, 'Maria Fernandes', '210234567', 'maria.f@email.pt',       '910000002', 'PROPRIETARIO', 2),
    (3, 'Rui Tavares',     '210345678', 'rui.tavares@email.pt',   '910000003', 'INQUILINO',    3),
    (4, 'Ana Lopes',       '210456789', 'ana.lopes@email.pt',     '910000004', 'PROPRIETARIO', 4);

-- ---------------------------------------------------------------------
--  Despesas
-- ---------------------------------------------------------------------
INSERT INTO despesa (id_despesa, descricao, valor, data, categoria, estado, id_condominio) VALUES
    (1, 'Manutencao do elevador',        180.00, '2026-01-15', 'Manutencao', 'PAGA',     1),
    (2, 'Limpeza das zonas comuns',      120.00, '2026-02-01', 'Limpeza',    'PAGA',     1),
    (3, 'Seguro multirriscos do predio', 450.00, '2026-03-10', 'Seguros',    'APROVADA', 1),
    (4, 'Reparacao da iluminacao',        75.50, '2026-04-05', 'Manutencao', 'PENDENTE', 1);

-- ---------------------------------------------------------------------
--  Quotas (mensais por fracao)
-- ---------------------------------------------------------------------
INSERT INTO quota (id_quota, mes, ano, valor, estado, id_fracao) VALUES
    (1, 1, 2026, 50.00, 'PAGA',     1),
    (2, 1, 2026, 50.00, 'PAGA',     2),
    (3, 1, 2026, 50.00, 'ATRASO',   3),
    (4, 1, 2026, 50.00, 'PENDENTE', 4),
    (5, 2, 2026, 50.00, 'PAGA',     1),
    (6, 2, 2026, 50.00, 'PENDENTE', 2);

-- ---------------------------------------------------------------------
--  Pagamentos
-- ---------------------------------------------------------------------
INSERT INTO pagamento (id_pagamento, valor, data, metodo, estado, id_quota) VALUES
    (1, 50.00, '2026-01-08', 'MBWAY',         'CONFIRMADO', 1),
    (2, 50.00, '2026-01-10', 'TRANSFERENCIA', 'CONFIRMADO', 2),
    (3, 50.00, '2026-02-09', 'MBWAY',         'CONFIRMADO', 5);

-- ---------------------------------------------------------------------
--  Ocorrencias
-- ---------------------------------------------------------------------
INSERT INTO ocorrencia (id_ocorrencia, titulo, descricao, estado, prioridade, data_registo, id_condomino) VALUES
    (1, 'Infiltracao na garagem', 'Mancha de humidade no teto do piso -1.', 'EM_ANALISE', 'ALTA',  '2026-03-02 09:30:00', 1),
    (2, 'Lampada fundida no hall', 'Luz do hall do 2.o andar nao funciona.', 'ABERTA',     'BAIXA', '2026-04-04 18:10:00', 3);

-- ---------------------------------------------------------------------
--  Documentos
-- ---------------------------------------------------------------------
INSERT INTO documento (id_documento, nome, tipo, ficheiro, data_upload, id_condominio) VALUES
    (1, 'Regulamento do Condominio', 'REGULAMENTO', 'docs/regulamento.pdf', '2026-01-05 10:00:00', 1),
    (2, 'Orcamento 2026',            'ORCAMENTO',   'docs/orcamento_2026.pdf', '2026-01-05 10:05:00', 1);

-- ---------------------------------------------------------------------
--  Reuniao + Ata + Votacao + Votos
-- ---------------------------------------------------------------------
INSERT INTO reuniao (id_reuniao, assunto, data, hora, local, estado, id_condominio) VALUES
    (1, 'Assembleia Geral Ordinaria 2026', '2026-03-20', '19:00:00', 'Sala de condominio - Bloco A', 'REALIZADA', 1);

INSERT INTO ata (id_ata, titulo, descricao, data_reuniao, ficheiro, arquivada, id_condominio) VALUES
    (1, 'Ata da AG Ordinaria 2026', 'Aprovacao de contas e orcamento para 2026.', '2026-03-20', 'docs/ata_ag_2026.pdf', 0, 1);

INSERT INTO votacao (id_votacao, tema, data_inicio, data_fim, estado, id_reuniao, id_condominio) VALUES
    (1, 'Aprovacao do orcamento de 2026', '2026-03-20 19:30:00', '2026-03-20 20:00:00', 'ENCERRADA', 1, 1);

INSERT INTO voto (id_voto, resposta, id_votacao, id_condomino) VALUES
    (1, 'SIM',       1, 1),
    (2, 'SIM',       1, 2),
    (3, 'ABSTENCAO', 1, 3),
    (4, 'NAO',       1, 4);

-- ---------------------------------------------------------------------
--  Mensagens
-- ---------------------------------------------------------------------
INSERT INTO mensagem (id_mensagem, assunto, conteudo, data_envio, destino, id_condomino, id_condominio) VALUES
    (1, 'Convocatoria da Assembleia Geral',
        'Convocam-se todos os condominos para a AG a realizar a 20/03/2026.',
        '2026-03-05 11:00:00', 'TODOS', NULL, 1),
    (2, 'Quota em atraso',
        'Lembramos que a quota de Janeiro se encontra por liquidar.',
        '2026-02-15 09:00:00', 'CONDOMINO', 3, 1);

-- ---------------------------------------------------------------------
--  Historico (auditoria)
-- ---------------------------------------------------------------------
INSERT INTO historico (id_historico, utilizador, operacao, data_hora) VALUES
    (1, 'admin@condomanager.com', 'Criou condominio "Edificio Sol Nascente"', '2026-01-05 09:00:00'),
    (2, 'gestor@silvagestao.pt',  'Registou pagamento da quota #1',           '2026-01-08 14:22:00');

-- =====================================================================
--  FIM DOS DADOS DE EXEMPLO
-- =====================================================================
