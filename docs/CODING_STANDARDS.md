# Coding Standards

## Objetivo
Garantir consistência, legibilidade, manutenção e qualidade do código.

---

# Linguagem
Java 21
Obrigatório.
Não utilizar versões anteriores sem necessidade justificada.

---

# Convenções de Nomenclatura

## Classes
PascalCase

Exemplos:
EmpresaGestao
Condominio
Fracao
Condomino
Pagamento
Ata

---

## Interfaces
Prefixo I não é permitido.

Exemplo:
CondominioService

Não:
ICondominioService

---

## Métodos
camelCase

Exemplos:
criarCondominio()
atualizarCondomino()
consultarPagamentos()

---

## Variáveis
camelCase

Exemplos:
nomeCondominio
valorQuota
dataPagamento

---

## Constantes
UPPER_CASE

Exemplos:
MAX_TENTATIVAS_LOGIN
TEMPO_EXPIRACAO_JWT

---

# Organização do Código
Uma classe por ficheiro.
Cada classe deve possuir apenas uma responsabilidade.
Seguir o princípio SRP (Single Responsibility Principle).

---

# Arquitetura Obrigatória
Controller
↓
Service
↓
Repository
↓
Database

Controllers:
Responsáveis apenas por receber e devolver dados.

Services:
Responsáveis pelas regras de negócio.

Repositories:
Responsáveis pelo acesso aos dados.

---

# DTO Pattern
Obrigatório.
Nunca devolver entidades JPA diretamente para o frontend.

Utilizar:
CreateDTO
UpdateDTO
ResponseDTO

---

# Validação
Obrigatória.

Utilizar:
jakarta.validation

Exemplos:
@NotNull
@NotBlank
@Size
@Email

---

# Tratamento de Exceções
Obrigatório.

Utilizar:
@RestControllerAdvice
GlobalExceptionHandler

Nunca utilizar:
printStackTrace()

---

# Logging
Obrigatório.

Utilizar:
SLF4J

Exemplo:
private static final Logger logger =
LoggerFactory.getLogger(...);

Nunca utilizar:
System.out.println()

---

# Lombok

Permitido.

Utilizar:
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

Evitar excesso de código boilerplate.

---

# JPA / Hibernate
Utilizar FetchType.LAZY por defeito.
Evitar FetchType.EAGER.

Utilizar:
@OneToMany
@ManyToOne
@OneToOne
@ManyToMany

---

# Segurança
Passwords:
BCrypt

Autenticação:
JWT

Autorização:
Spring Security
RBAC

---

# Testes
Obrigatórios.
JUnit 5
Mockito

Cobertura mínima:
70%

---

# Código Limpo
Seguir:
SOLID
DRY
KISS

Métodos:
Máximo recomendado:
30 linhas

Classe:
Máximo recomendado:
300 linhas

---

# Comentários
Comentar apenas:
- Regras de negócio complexas
- Algoritmos complexos
- Integrações externas
Não comentar código óbvio.

---

# Git
Commits obrigatórios por funcionalidade.

Formato:
feat:
fix:
refactor:
docs:
test:
