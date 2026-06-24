# LEGAL_RULES — Regime da Propriedade Horizontal (PT)

> Regras legais que o sistema deve refletir, extraídas de
> *"O Código do Condomínio — Guia Estrutural da Propriedade Horizontal"*
> (Código Civil PT, Art.º 1420.º e seguintes; alterações da **Lei 8/2022**).
> Fonte: documento fornecido pelo utilizador em 2026-06-22.
> Resolve os itens **D** (quórum/maiorias) e **I** (legislação) de `SPEC.md`.

---

## 1. Estrutura legal (Art.º 1420.º e 1421.º)

### Dualidade da propriedade
Cada fração funde dois direitos inseparáveis:
- **Fração autónoma** — propriedade exclusiva (apartamento, loja, garagem).
  Condição legal: deve ter saída própria para parte comum ou via pública.
- **Compropriedade** — quota-parte indivisível das partes comuns.
  Não se pode alienar a fração sem a parte comum, nem renunciar à parte comum para evitar despesas.

### Título Constitutivo
Documento fundador do condomínio. Define a composição do prédio, a **permilagem de cada fração**
e o fim a que cada fração se destina.
**Só pode ser alterado por unanimidade.**

> **Implicação para o sistema:** a permilagem é um dado mestre da fração, fixado no título
> constitutivo. Alterações à permilagem ou ao fim da fração exigem deliberação por unanimidade.

### Partes comuns (Art.º 1421.º)
- **Necessariamente comuns:** solo, alicerces, pilares, colunas, paredes-mestras; cobertura/telhado;
  entradas, vestíbulos, escadas e corredores de uso comum; redes gerais de água, eletricidade, gás e comunicações.
- **Presumivelmente comuns:** ascensores; pátios e jardins anexos; garagens e lugares de estacionamento
  não alocados no título.
- O título pode alocar partes comuns a uso exclusivo de um condómino, mas a conservação estrutural
  mantém-se, regra geral, responsabilidade coletiva.

---

## 2. Permilagem — a chave mestra (Art.º 1424.º)

A regra **não** é "uma pessoa, um voto" nem "divisão por igual".
O poder de decisão e o peso financeiro estão ancorados na **permilagem** (‰, base 1000).

- **Poder de voto:** cada fração vale `permilagem` votos em 1000 possíveis.
- **Peso financeiro (quota):**
  ```
  quota_anual  = orcamento_anual × (permilagem / 1000)
  quota_mensal = quota_anual / 12
  ```

> **Refinamento ao item B de `SPEC.md`:** a permilagem é expressa em **milésimos (0–1000)**,
> não como fração decimal. A fórmula correta é `× permilagem / 1000`.
> O campo `fracao.permilagem DECIMAL(8,4)` deve guardar o valor em milésimos (ex.: `85.0000` = 85‰).

### Exceção legal (Art.º 1424.º)
Serviços específicos (ex.: elevadores) podem ser pagos **apenas por quem deles usufrui**, ou
divididos em **partes iguais** — mas exige aprovação por **2/3 sem oposição** em sede de regulamento.

> **Implicação:** o cálculo de quotas deve suportar, além do rateio por permilagem, regras de
> exceção por serviço/fração (fruição). Fora do MVP, mas a estrutura de dados deve permitir.

---

## 3. Fundo Comum de Reserva e seguro

- **Fundo Comum de Reserva:** conta-poupança **obrigatória por lei** para obras de conservação
  ou reparação extraordinária.
- **Regra dos 10%:** cada condómino contribui com **≥ 10% da sua quota regular** para este fundo.
- **Reposição:** se o fundo for usado para outro fim, deve ser reposto em **máx. 12 meses** (Lei 8/2022).
- **Seguro contra incêndio:** obrigatório para partes comuns e frações. O administrador deve
  ativá-lo e cobrar o prémio caso o condómino não o faça.

> **Implicação para o sistema:** a quota emitida deve poder decompor-se em
> *quota corrente* + *contribuição para fundo de reserva (≥10%)*.

---

## 4. Assembleia de condóminos — cronologia (Lei 8/2022)

| Momento | Ação | Regras |
|---|---|---|
| **-10 dias** | Convocatória | Carta registada **ou e-mail com recibo de receção**. Deve conter: dia, hora, local, ordem de trabalhos e indicação de assuntos que exijam unanimidade. |
| **Dia 0** | Reunião | Verificação do quórum → discussão e **votação nominal** → redação e assinatura da ata. |
| **+30 dias (máx)** | Notificação aos ausentes | Obrigatório comunicar deliberações aos ausentes por carta registada ou correio eletrónico. |
| **+90 dias** | Assentimento tácito | Os ausentes têm 90 dias após a receção para comunicar discordância por escrito. **O silêncio vale como aprovação.** |

> **Implicação para F/G de `SPEC.md`:** o e-mail deixa de ser opcional — a convocatória, a
> notificação de deliberações e a declaração de dívida são legalmente admissíveis/exigíveis por
> correio eletrónico. Sobe a prioridade do módulo de comunicação/email.

---

## 5. Quórum e funcionamento (Lei 8/2022)

- **1.ª convocatória (regra dos 50%):** delibera com presença de condóminos cujos votos representem
  **mais de 50% do capital investido** (permilagem).
- **Solução rápida (atraso de 30 min):** se não houver 50% mas houver **≥ 25% do capital**, a assembleia
  pode reunir 30 minutos depois, no mesmo local, **se previsto na convocatória**.
- **2.ª convocatória:** na falta de quórum, nova reunião **1 semana depois** (mesma hora/local),
  deliberando validamente com apenas **25% do capital**.
- **Representação:** condóminos podem fazer-se representar pelo administrador ou por outro condómino
  via **procuração assinada** (desde que não haja conflito de interesses).

---

## 6. Matriz de deliberações e maiorias (item D — RESOLVIDO)

> Esta é a regra a implementar no motor de contagem de votações.
> O peso de cada voto é a **permilagem** da fração, não o nº de pessoas.

| Tipo de maioria | Limiar (sobre permilagem/capital) | Aplica-se a |
|---|---|---|
| **Maioria simples** | > 50% dos votos **presentes** | Eleição do administrador; aprovação do orçamento anual e contas; reparações ordinárias |
| **Sem oposição (veto)** | Maioria de capital **e zero votos contra** (abstenções permitidas) | Alteração da distribuição de despesas; proibições no regulamento; divisão de frações |
| **2/3 do valor total** | ≥ 66,6% do **capital total** do edifício | Obras de inovação; alteração da linha arquitetónica/estética |
| **Unanimidade** | 100% do capital (pode formar-se com 2/3 dos presentes + assentimento tácito dos ausentes) | Alteração do título constitutivo; alteração do fim de uma fração; reconstrução total |

> **Implicação para o sistema:** a entidade `votacao` deve ter um campo
> `tipo_maioria` (MAIORIA_SIMPLES, SEM_OPOSICAO, DOIS_TERCOS, UNANIMIDADE) e o encerramento
> calcula o resultado somando a **permilagem** dos votos, aplicando o limiar correspondente.
> Para unanimidade/2-3, o denominador é o **capital total**; para maioria simples, o **capital presente**.

---

## 7. Manutenção e obras (Art.º 1427.º)

| Categoria | Quem decide | Regra |
|---|---|---|
| Reparações indispensáveis/urgentes | Administrador (ratifica em assembleia extraordinária); na ausência, qualquer condómino | Eliminação imediata de vícios com danos graves/risco |
| Conservação extraordinária | Assembleia (maioria simples) | **Regra dos 3 orçamentos**: administrador obrigado a apresentar ≥ 3 orçamentos diferentes |
| Obras de inovação | Assembleia (maioria 2/3) | Quem não concorda, por norma, não paga; mobilidade condicionada tem regras de facilitação |

---

## 8. Dívidas e conflitos

### Incumprimento de quotas
- **Declaração de dívida (Art.º 1424.º-A):** para vender uma fração, o condómino exige ao administrador
  uma declaração escrita de dívidas, a emitir em **10 dias úteis**.
- **Processo de injunção:** procedimento rápido de cobrança; as quotas gozam de **privilégio creditório imobiliário**.
- **Ata como título executivo:** a ata que estipula quotas e montantes tem força legal imediata para cobrança.

### Impugnação de deliberações
Condóminos que não aprovaram uma deliberação ilegal podem:
- exigir convocação de assembleia extraordinária para revogação;
- **30 dias:** recorrer a centro de arbitragem;
- **60 dias:** propor ação judicial de anulação.

> **Implicação para o sistema:** suportar emissão de **declaração de dívida** (relatório), marcação
> de quotas em **atraso** com antiguidade, e a ata como documento com estado/força executiva.

---

## 9. Funções legais do administrador (Art.º 1436.º)

- **Operacional:** atos conservatórios e de manutenção; regular uso das coisas comuns; intervir em urgências.
- **Administrativo:** convocar assembleia; **executar deliberações em ≤ 15 dias úteis**; guardar documentos e atas.
- **Financeiro:** elaborar orçamento anual; cobrar receitas e a quota-parte; verificar o Fundo de Reserva e prestar contas.
- **Jurídico/Representação:** representar o condomínio em juízo; garantir seguro contra incêndios;
  emitir declarações de dívida em **10 dias úteis**.

---

## 10. Direitos e deveres do condómino

- **Direitos:** usar a fração e partes comuns; participar e votar; consultar atas/orçamentos/contas/regulamento;
  fazer obras na fração sem alterar estrutura ou linha arquitetónica.
- **Deveres:** pagar a quota e contribuir para o Fundo de Reserva (≥10%); conservar a fração; manter seguro
  de incêndio; respeitar o fim estipulado e o sossego/estética do prédio.
