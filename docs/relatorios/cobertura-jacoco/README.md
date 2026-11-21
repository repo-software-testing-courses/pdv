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
| Instruções | 90,1% (526/584) |
| Linhas | 84,9% (107/126) |
| Métodos | 100% (11/11) |
| Complexidade coberta | 26/30 |

Evidências (relatório HTML do JaCoCo exportado em PDF):

- Resumo da classe: [`jacoco-vendaservice-resumo.pdf`](jacoco-vendaservice-resumo.pdf)
- Código linha a linha (verde: coberto; amarelo: arestas parciais; vermelho: não coberto): [`jacoco-vendaservice-detalhe.pdf`](jacoco-vendaservice-detalhe.pdf)

### Sobre as 4 arestas não cobertas

As 4 arestas descobertas correspondem aos ramos de **tratamento de exceção**
(`catch`) de `abreVenda` e `addProduto` — os mesmos pontos onde o código engole
a exceção (defeitos DEF-VENDA-001/relacionados). Exercitá-las exigiria forçar
exceção nos colaboradores; isso é feito pelos testes `@Tag("defeito")`, que
entram nessas arestas mas **falham de propósito** (asserem o comportamento
correto, que o código não cumpre). Como o JaCoCo contabiliza a aresta mesmo com
o teste falhando, parte dessas arestas aparece coberta; as remanescentes são os
ramos de exceção não acionados. A meta de 80% todas-arestas é atingida com folga
(89,5%). **Nenhum defeito foi corrigido.**

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
| --- | --- | --- |
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