# Inspeção de código (SonarQube for IDE)

Critério da Entrega 2: executar a ferramenta de inspeção, enviar print, resolver
os problemas de ao menos uma classe por integrante e enviar print depois das
correções.

Ferramenta: plugin **SonarQube for IDE** no Eclipse, com as regras padrão
(análise do arquivo pelo menu *SonarQube → Analyze*).

## CaixaService — Eloyse Fernanda

| | Antes | Depois |
| --- | --- | --- |
| Problemas no arquivo | **21** (1 alto, 15 médios, 3 baixos, 2 informativos) | **1** (informativo) |

### Antes

![SonarQube antes das correções em CaixaService](caixaservice-antes.png)

### Depois

![SonarQube depois das correções em CaixaService](caixaservice-depois.png)

### Problemas corrigidos

| Regra apontada pelo Sonar | Qtd | Correção |
| --- | --- | --- |
| Complexidade cognitiva 24 > 15 em `cadastro` | 1 | descrição padrão e observação de abertura extraídas para métodos com `switch` (`descricaoOuPadrao`, `observacaoAbertura`) |
| Ternário aninhado | 1 | substituído por `switch` |
| `if` aninhados que podem ser unidos | 2 | método `temData(String)` em `listarCaixas` e `listaBancosAbertosTipoFilterBanco` |
| Injeção por campo (`@Autowired`) | 3 | injeção por construtor, com campos `final` |
| Variável local esconde o campo `usuario` | 1 | os campos `descricao` e `usuario` viraram variáveis locais, o que também evita estado compartilhado entre requisições num bean singleton |
| Campo `usuario` deveria ser variável local | 1 | idem |
| Exceção genérica (`RuntimeException`) | 6 | nova `CaixaException` (subclasse de `RuntimeException`), com as mesmas mensagens |
| Uso de `System.out` | 2 | removidos (eram impressões de depuração) |
| Retorno por variável temporária | 1 | retorno direto em `buscaCaixaUsuario` |
| Ternário com condição negada | 1 | verificação de `null` direta no valor total do fechamento |
| `.now()` sem fuso horário | 1 | `LocalDate.now(ZONA)`, com `ZONA = America/Sao_Paulo` |

### Problema mantido (justificado)

**"Use the java.time API for date and time"** (informativo). A entidade `Caixa`
exige `java.sql.Date` em `setData_cadastro` e `java.sql.Timestamp` em
`setData_fechamento`. O serviço já calcula as datas com `java.time`
(`LocalDate`, `Instant`, `ZoneId`) e só converte para os tipos antigos ao
preencher a entidade. Zerar o item exigiria alterar a entidade, o mapeamento
JPA, as telas e as consultas do repositório — uma mudança de modelo de dados
fora do escopo da classe sob teste e que não deve ser feita só por causa da
inspeção.

### Efeitos das correções

- **Dois defeitos corrigidos** em `fechaCaixa`, encontrados por leitura do
  código na Entrega 2 (registrados como adiados no AI-012) e resolvidos junto
  com a refatoração:
  - senha `null` lançava `NullPointerException` → agora retorna
    "Favor, informe a senha";
  - caixa inexistente lançava `NoSuchElementException` → agora lança
    `CaixaException("Caixa não encontrado")`.

  Cada um ganhou um teste em `CaixaServiceTest`.
- **Testes:** 43 casos, 0 falhas.
- **Cobertura todas-arestas (JaCoCo):** 97,7% (43/44) → 100% (38/38). O total
  de arestas diminuiu porque os `if` aninhados e os ternários foram
  simplificados; a aresta inalcançável do `else if (BANCO)` deixou de existir.
- **Escore de mutação (PIT):** 96% (52/54) → 100% (55/55). Os dois mutantes
  sobreviventes eram as remoções dos `System.out`, que não existem mais.


## AjusteService — Natalia de Abreu Lamas

| | Antes | Depois |
| --- | --- | --- |
| Problemas no arquivo | **13** (8 médios, 2 baixos, 3 informativos) | **1** (informativo) |

Ferramenta: extensão **SonarQube for IDE** no VS Code, com as regras padrão.

### Antes

![SonarQube antes das correções em AjusteService](ajusteservice-antes.png)

### Depois

![SonarQube depois das correções em AjusteService](ajusteservice-depois.png)

### Problemas corrigidos

| Regra apontada pelo Sonar | Qtd | Correção |
| --- | --- | --- |
| Injeção por campo (`@Autowired`) — java:S6813 | 2 | injeção por construtor, com campos `final` |
| Exceção genérica (`RuntimeException`) — java:S112 | 5 | nova `AjusteException` (subclasse de `RuntimeException`), com as mesmas mensagens |
| Nome de variável fora do padrão — java:S117 | 2 | `qtd_alteracao` → `qtdAlteracao`; `origem_operacao` → `origemOperacao` |
| Uso de `System.out` — java:S106 | 1 | substituído por logger (`LOG.error`), registrando produto, ajuste e exceção |
| `.now()` sem fuso horário — java:S8688 | 2 | `LocalDate.now(ZONA)`, com `ZONA = America/Sao_Paulo` |

### Problema mantido (justificado)

**"Use the java.time API for date and time"** (informativo). A entidade `Ajuste`
exige `java.sql.Date` em `setData_processamento` e no construtor
(`data_cadastro`). O serviço já calcula as datas com `java.time` (`LocalDate`,
`ZoneId`) e só converte para o tipo antigo ao preencher a entidade. Zerar o item
exigiria alterar a entidade, o mapeamento JPA e as telas — mesma justificativa
adotada em `CaixaService`.

### Efeitos das correções

- **Comportamento preservado:** as mensagens de erro são as mesmas e
  `AjusteException` é subclasse de `RuntimeException`, então o contrato com o
  controller não mudou. Os 19 casos de `AjusteServiceTest` foram executados
  novamente com o mesmo resultado (1 falha intencional, do teste
  `@Tag("defeito")`).
- **Cobertura todas-arestas (JaCoCo):** mantida em 100% (10/10)
  ([relatório](../cobertura-jacoco/)).
- **Escore de mutação (PIT):** 94% (17/18) → 100% (17/17). O mutante
  sobrevivente era a remoção do `System.out.println(e)`, que não existe mais
  ([relatório](../mutacao-pit/)).
- **Defeito não corrigido:** `processar` com código de ajuste inexistente
  continua lançando `NoSuchElementException`. Não foi apontado pelo Sonar e está
  registrado em issue, documentado pelo teste `@Tag("defeito")`.