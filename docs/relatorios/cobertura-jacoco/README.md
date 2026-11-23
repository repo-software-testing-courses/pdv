# Cobertura estrutural (JaCoCo)

Critério da Entrega 2: ao menos **80% de cobertura no critério todas-arestas**
(branch) nas classes sob teste.

## CaixaService — Eloyse Fernanda

| Métrica | Resultado |
| --- | --- |
| **Todas-arestas (branch)** | **100%** (38 de 38 arestas) |
| Instruções | 100% (353/353) |
| Linhas | 100% (80/80) |
| Métodos | 100% (18/18) |
| Complexidade coberta | 38/38 |

Evidências (relatório HTML do JaCoCo exportado em PDF):

- Resumo da classe: [`jacoco-caixaservice-resumo.pdf`](jacoco-caixaservice-resumo.pdf)
- Código linha a linha (verde: coberto; amarelo: arestas parciais; vermelho: não coberto): [`jacoco-caixaservice-detalhe.pdf`](jacoco-caixaservice-detalhe.pdf)

### Evolução

| Momento | Arestas cobertas | Casos de teste |
| --- | --- | --- |
| Entrega 1 (suíte inicial) | 41/44 (93,2%) | 29 |
| Entrega 2, testes unitários ampliados ([AI-012](../../ai/AI-LOG.md)) | 43/44 (97,7%) | 41 |
| Entrega 2, depois das correções do Sonar | **38/38 (100%)** | 43 |

A aresta que faltava na 2ª linha era o lado falso do `else if (BANCO)` em
`cadastro`, inalcançável porque `CaixaTipo` só tem CAIXA, COFRE e BANCO. Na
correção do Sonar esse encadeamento virou um `switch` com `default`, e a aresta
inalcançável deixou de existir. O total de arestas caiu de 44 para 38 porque
os `if` aninhados e os ternários foram simplificados ([relatório do
Sonar](../inspecao-sonar/)).

### Como reproduzir

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn test -Dtest=CaixaServiceTest jacoco:report
```

Relatório da classe em
`target/site/jacoco/net.originmobi.pdv.service/CaixaService.html`.

## VendaService — Alexandre Porto

| Métrica | Resultado |
| --- | --- |
| **Todas-arestas (branch)** | **89,5%** (34 de 38 arestas) |
| Instruções | 89,1% (516/579) |
| Linhas | 84,9% (107/126) |
| Métodos | 100% (13/13) |
| Complexidade coberta | 28/32 |

Evidências (relatório HTML do JaCoCo exportado em PDF):

- Resumo da classe: [`jacoco-vendaservice-resumo.pdf`](jacoco-vendaservice-resumo.pdf)
- Código linha a linha (verde: coberto; amarelo: arestas parciais; vermelho: não coberto): [`jacoco-vendaservice-detalhe.pdf`](jacoco-vendaservice-detalhe.pdf)

Medição feita só com `VendaServiceTest` (43 casos, 2 deles `@Tag("defeito")`
falhando de propósito; o JaCoCo conta a cobertura mesmo assim).

### As 4 arestas não cobertas

São validações cujo ramo de erro nenhum teste aciona (números de linha da
versão atual, depois da refatoração do Sonar):

| Linha | Método | Condição | Ramo não exercitado |
| --- | --- | --- | --- |
| 254 | `lancaAVista` | `sigla == CARTDEB \|\| sigla == CARTCRED` | título à vista de tipo diferente de dinheiro e cartão |
| 268 | `aprazo` | `vlParcela.isEmpty()` | parcela a prazo sem valor ("valor de recebimento invalido") |
| 294 | `avistaDinheiro` | `vlParcelas[i].isEmpty()` | parcela à vista sem valor ("Parcela sem valor, verifique") |
| 303 | `avistaDinheiro` | `!totalParcelas.equals(vlprodutos)` | soma das parcelas diferente do total ("Valor das parcelas diferente...") |

A meta de 80% todas-arestas é atingida com folga (89,5%). Os quatro ramos
poderiam ser cobertos com casos de teste curtos; não foram incluídos porque a
meta já estava cumprida.

### Evolução

| Momento | Arestas cobertas | Métodos | Casos de teste |
| --- | --- | --- | --- |
| Entrega 2, antes do Sonar | 34/38 (89,5%) | 11/11 | 43 |
| Entrega 2, depois do Sonar | 34/38 (89,5%) | 13/13 | 43 |

As arestas não mudaram com a refatoração. Os métodos passaram de 11 para 13
pela extração de `lancaAVista` e pelo inicializador estático do logger.

### Como reproduzir

Execução via container Docker (Maven 3.8 + JDK 8):

```sh
docker compose up -d
docker compose exec pdv-app mvn -Dtest=VendaServiceTest -Dmaven.test.failure.ignore=true test
```

O relatório é gerado em `target/site/jacoco/index.html` (classe em
`target/site/jacoco/net.originmobi.pdv.service/VendaService.html`). A flag
`-Dmaven.test.failure.ignore=true` permite gerar o relatório mesmo com os 2
testes `@Tag("defeito")` falhando de propósito. JaCoCo 0.8.11.

## AjusteService — Natalia de Abreu Lamas

| Métrica | Resultado |
| --- | --- |
| **Todas-arestas (branch)** | **100%** (10 de 10 arestas) |
| Instruções | 100% (218/218) |
| Linhas | 100% (43/43) |
| Métodos | 100% (7/7) |
| Complexidade coberta | 12/12 |

Evidências (relatório HTML do JaCoCo exportado em PDF):

- Resumo da classe: [`jacoco-ajusteservice-resumo.pdf`](jacoco-ajusteservice-resumo.pdf)
- Código linha a linha (verde: coberto; amarelo: arestas parciais; vermelho: não coberto): [`jacoco-ajusteservice-detalhe.pdf`](jacoco-ajusteservice-detalhe.pdf)

### Evolução

| Momento | Arestas cobertas | Casos de teste |
| Entrega 1 (suíte inicial, [medição](jacoco-ajusteservice-entrega1.pdf)) | 7/10 (70%) | 7 |
| Entrega 2, testes unitários ampliados | 10/10 (100%) | 19 |
| Entrega 2, depois das correções do Sonar | **10/10 (100%)** | 19 |

As 10 arestas são os dois lados do `if` de `lista`, do `if` de `processar`, do
laço `for`, do ternário que define ENTRADA ou SAÍDA (coberto nos valores-limite
1 e 0) e do `if` de `remover`. As correções do Sonar não criaram nem removeram
desvios: instruções, linhas e métodos aumentaram por causa do construtor e das
constantes estáticas (`LOG`, `ZONA`, `MSG_ERRO_PROCESSAR`). A complexidade
ciclomática é 12 pelo JaCoCo e 15 pela definição de McCabe, que conta também os
3 blocos `catch` — acima do mínimo de 10 exigido.

### Como reproduzir

Execução via container Docker (Maven 3.8 + JDK 8):

```sh
docker run --rm -v "${PWD}:/app" -v pdv-m2:/root/.m2 -w /app maven:3.8.8-eclipse-temurin-8 mvn clean test "-Dtest=AjusteServiceTest" "-Dmaven.test.failure.ignore=true"
```

Relatório da classe em
`target/site/jacoco/net.originmobi.pdv.service/AjusteService.html`. A flag
`-Dmaven.test.failure.ignore=true` permite gerar o relatório mesmo com o teste
`@Tag("defeito")` falhando de propósito. JaCoCo 0.8.11.