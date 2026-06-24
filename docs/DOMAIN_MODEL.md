# DOMAIN MODEL

## Agregado Raiz Principal
EMPRESA_GESTAO
Representa uma empresa cliente da plataforma SaaS.

É responsável por:
- Condomínios
- Utilizadores
- Configurações

---

# Agregado Condomínio
Condominio

Contém:
- Edifícios
- Frações
- Reuniões
- Documentos
- Despesas
- Ocorrências

Regras:
Um condomínio pertence sempre a uma única empresa.

---

# Agregado Edifício
Edificio

Representa um bloco físico.
Pode conter várias frações.

---

# Agregado Fração
Fracao

Representa:
- Apartamento
- Loja
- Garagem
- Arrecadação

Regras:
Uma fração pertence a um único condomínio.
Uma fração pode possuir vários condóminos.
Uma fração pode gerar várias quotas.

---

# Agregado Condómino
Condomino

Tipos:
- Proprietário
- Inquilino

Regras:
Um condómino deve estar associado a uma fração.
Um proprietário pode votar.
Um inquilino pode ter permissões limitadas.

---

# Agregado Utilizador
Utilizador
Representa uma conta de acesso.

Possui:
- Email
- Password
- Perfis

Regras:
Email único.
Password cifrada com BCrypt.
Conta pode estar ativa ou inativa.

---

# Agregado Perfil
Perfil
Responsável pela autorização.

Perfis base:
ADMIN_SISTEMA
GESTOR_EMPRESA
FUNCIONARIO
ADMIN_CONDOMINIO
CONDOMINO

---

# Agregado Financeiro

## Quota
Mensalidade emitida para uma fração.

Estados:
PENDENTE
PAGO
ATRASADO
ANULADO

---

## Pagamento
Representa o pagamento de uma quota.

Métodos:
TRANSFERENCIA
MBWAY
MULTIBANCO
PAYPAL
DINHEIRO

---

## Despesa
Despesa associada ao condomínio.

Categorias:
MANUTENCAO
LIMPEZA
SEGURO
AGUA
ELETRICIDADE
OUTROS

---

# Agregado Reuniões

## Reunião
Assembleia do condomínio.
Estados:
AGENDADA
REALIZADA
CANCELADA

---

## Ata
Documento oficial da reunião.
Uma reunião pode gerar uma ata.

---

# Agregado Votações

## Votação
Associada a uma reunião.

Define:
Tema
Data início
Data fim

---

## Voto

Associado a:
- Condómino
- Votação

Respostas:
SIM
NAO
ABSTENCAO

---

# Agregado Comunicação

## Mensagem
Sistema interno de comunicação.

Tipos:
Individual
Grupo
Broadcast

---

# Agregado Documentos

## Documento

Documentos do condomínio.
Exemplos:
Regulamentos
Atas
Contratos
Orçamentos
Faturas

---

# Agregado Ocorrências

## Ocorrência
Pedido ou incidente registado.

Estados:
ABERTA
EM_ANALISE
EM_EXECUCAO
CONCLUIDA
CANCELADA

Prioridades:
BAIXA
MEDIA
ALTA
URGENTE

---

# Agregado Auditoria

## Histórico

Regista:
- Login
- Logout
- CRUD
- Aprovações
- Alterações

Regras:
Não pode ser alterado.
Não pode ser apagado.
Serve para auditoria completa do sistema.

---

# Multi-Tenant

Regra Fundamental:
Nenhum utilizador pode aceder a dados de outra empresa.
Todas as consultas devem filtrar:

id_empresa

Obrigatório em:
- Condomínios
- Frações
- Condóminos
- Utilizadores
- Quotas
- Pagamentos
- Despesas
- Documentos
- Ocorrências
