# Cobertura estrutural (JaCoCo)

Critério da Entrega 2: ao menos **80% de cobertura no critério todas-arestas**
(branch) nas classes sob teste.

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
