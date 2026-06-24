# DATABASE_SCHEMA

## Objetivo
Definir a estrutura física da base de dados MySQL 8 para o Sistema de Gestão de Condomínios SaaS.

---

# Convenções

## Chaves Primárias
Todas as tabelas:
BIGINT AUTO_INCREMENT

Exemplo:
id_empresa
id_condominio

---

## Chaves Estrangeiras
Formato:
id_<tabela>

Exemplos:
id_empresa
id_condominio
id_fracao

---

## Auditoria
Todas as tabelas devem conter:
created_at DATETIME
updated_at DATETIME

Opcional:
created_by BIGINT
updated_by BIGINT

---

# Tabela: empresa_gestao
| Campo | Tipo |
|---------|---------|
| id_empresa | BIGINT PK |
| nome | VARCHAR(150) |
| nif | VARCHAR(20) |
| email | VARCHAR(150) |
| telefone | VARCHAR(30) |
| morada | VARCHAR(255) |
| estado | ENUM |
| created_at | DATETIME |
| updated_at | DATETIME |

Relacionamentos:
empresa_gestao (1:N) condominio
empresa_gestao (1:N) utilizador

---

# Tabela: condominio
| Campo | Tipo |
|---------|---------|
| id_condominio | BIGINT PK |
| nome | VARCHAR(150) |
| morada | VARCHAR(255) |
| orcamento_anual | DECIMAL(12,2) |
| id_empresa | BIGINT FK |
| created_at | DATETIME |
| updated_at | DATETIME |

Relacionamentos:
condominio (N:1) empresa_gestao
condominio (1:N) edificio
condominio (1:N) fracao
condominio (1:N) reuniao
condominio (1:N) documento

---

# Tabela: edificio
| Campo | Tipo |
|---------|---------|
| id_edificio | BIGINT PK |
| id_empresa BIGINT NOT NULL
| nome | VARCHAR(100) |
| bloco | VARCHAR(50) |
| numero_pisos | INT |
| id_condominio | BIGINT FK |
| created_at | DATETIME |
| updated_at | DATETIME |

Relacionamentos:
edificio (N:1) condominio
edificio (1:N) fracao

---

# Tabela: fracao
| Campo | Tipo |
|---------|---------|
| id_fracao | BIGINT PK |
| id_empresa BIGINT NOT NULL
| numero | VARCHAR(20) |
| tipologia | VARCHAR(20) |
| permilagem | DECIMAL(8,4) |
| area_m2 | DECIMAL(8,2) |
| id_condominio | BIGINT FK |
| id_edificio | BIGINT FK |
| created_at | DATETIME |
| updated_at | DATETIME |

Relacionamentos:
fracao (N:1) condominio
fracao (N:1) edificio
fracao (1:N) quota
fracao (1:N) condomino

---

# Tabela: condomino
| Campo | Tipo |
|---------|---------|
| id_condomino | BIGINT PK |
| id_empresa BIGINT NOT NULL
| nome | VARCHAR(150) |
| nif | VARCHAR(20) |
| email | VARCHAR(150) |
| telefone | VARCHAR(30) |
| tipo | ENUM(PROPRIETARIO,INQUILINO) |
| id_fracao | BIGINT FK |
| created_at | DATETIME |
| updated_at | DATETIME |

---

# Tabela: utilizador
| Campo | Tipo |
|---------|---------|
| id_utilizador | BIGINT PK |
| nome | VARCHAR(150) |
| email | VARCHAR(150) |
| password | VARCHAR(255) |
| ativo | BOOLEAN |
| id_empresa | BIGINT FK |
| created_at | DATETIME |
| updated_at | DATETIME |

---

# Tabela: perfil
| Campo | Tipo |
|---------|---------|
| id_perfil | BIGINT PK |
| nome | VARCHAR(50) |
| descricao | VARCHAR(255) |

Perfis:
ADMIN_SISTEMA
GESTOR_EMPRESA
FUNCIONARIO
ADMIN_CONDOMINIO
CONDOMINO

---

# Tabela: utilizador_perfil
Tabela intermédia Many-To-Many
| Campo | Tipo |
|---------|---------|
| id_utilizador | BIGINT FK |
| id_perfil | BIGINT FK |

PK composta
(id_utilizador,id_perfil)

---

# Tabela: quota
| Campo | Tipo |
|---------|---------|
| id_quota | BIGINT PK |
| id_empresa BIGINT NOT NULL
| mes | INT |
| ano | INT |
| valor | DECIMAL(10,2) |
| estado | ENUM |
| id_fracao | BIGINT FK |
| created_at | DATETIME |
| updated_at | DATETIME |

Estados:
PENDENTE
PAGO
ATRASADO
ANULADO

---

# Tabela: pagamento
| Campo | Tipo |
|---------|---------|
| id_pagamento | BIGINT PK |
| id_empresa BIGINT NOT NULL
| valor | DECIMAL(10,2) |
| data_pagamento | DATETIME |
| metodo | VARCHAR(50) |
| estado | VARCHAR(50) |
| id_quota | BIGINT FK |

---

# Tabela: despesa
| Campo | Tipo |
|---------|---------|
| id_despesa | BIGINT PK |
| id_empresa BIGINT NOT NULL
| descricao | VARCHAR(255) |
| categoria | VARCHAR(100) |
| valor | DECIMAL(10,2) |
| data_despesa | DATE |
| id_condominio | BIGINT FK |

---

# Tabela: reuniao
| Campo | Tipo |
|---------|---------|
| id_reuniao | BIGINT PK |
| id_empresa BIGINT NOT NULL
| data | DATE |
| hora | TIME |
| local | VARCHAR(255) |
| estado | VARCHAR(50) |
| id_condominio | BIGINT FK |

---

# Tabela: ata
| Campo | Tipo |
|---------|---------|
| id_ata | BIGINT PK |
| id_empresa BIGINT NOT NULL
| titulo | VARCHAR(200) |
| descricao | TEXT |
| data_reuniao | DATE |
| ficheiro | VARCHAR(255) |
| id_reuniao | BIGINT FK |

---

# Tabela: votacao
| Campo | Tipo |
|---------|---------|
| id_votacao | BIGINT PK |
| id_empresa BIGINT NOT NULL
| tema | VARCHAR(255) |
| data_inicio | DATETIME |
| data_fim | DATETIME |
| id_reuniao | BIGINT FK |

---

# Tabela: voto
| Campo | Tipo |
|---------|---------|
| id_voto | BIGINT PK |
| id_empresa BIGINT NOT NULL
| resposta | ENUM(SIM,NAO,ABSTENCAO) |
| id_votacao | BIGINT FK |
| id_condomino | BIGINT FK |

---

# Tabela: mensagem
| Campo | Tipo |
|---------|---------|
| id_mensagem | BIGINT PK |
| id_empresa BIGINT NOT NULL
| assunto | VARCHAR(200) |
| conteudo | TEXT |
| data_envio | DATETIME |
| id_utilizador_origem | BIGINT FK |
| id_utilizador_destino | BIGINT FK |

---

# Tabela: documento
| Campo | Tipo |
|---------|---------|
| id_documento | BIGINT PK |
| id_empresa BIGINT NOT NULL
| nome | VARCHAR(255) |
| tipo | VARCHAR(100) |
| ficheiro | VARCHAR(255) |
| id_condominio | BIGINT FK |

---

# Tabela: ocorrencia
| Campo | Tipo |
|---------|---------|
| id_ocorrencia | BIGINT PK |
| id_empresa BIGINT NOT NULL
| titulo | VARCHAR(255) |
| descricao | TEXT |
| estado | VARCHAR(50) |
| prioridade | VARCHAR(50) |
| id_condominio | BIGINT FK |
| id_condomino | BIGINT FK |

---

# Tabela: historico
| Campo | Tipo |
|---------|---------|
| id_historico | BIGINT PK |
| id_empresa BIGINT NOT NULL
| utilizador | VARCHAR(150) |
| operacao | VARCHAR(255) |
| data_hora | DATETIME |
