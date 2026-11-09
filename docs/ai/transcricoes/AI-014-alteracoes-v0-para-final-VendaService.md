# AI-014 — Alterações de `VendaServiceTest`: da v0 (Entrega 1) à versão final (Entrega 2)

Comparação entre:

- **v0:** [`AI-014-v0-VendaServiceTest.java`](AI-014-v0-VendaServiceTest.java) — suíte da Entrega 1 (34 casos; 2 falham de propósito documentando defeitos).
- **final:** [`AI-014-final-VendaServiceTest.java`](AI-014-final-VendaServiceTest.java) — suíte da Entrega 2 (43 casos), cópia de `src/test/java/net/originmobi/pdv/VendaServiceTest.java`.

## Objetivo da revisão

Atender ao item 6 da Entrega 2 para `VendaService`: técnica estrutural
(≥ 80% todas-arestas) e baseada em defeitos (≥ 80% de escore de mutação, PIT),
**sem corrigir nenhum defeito do código de produção**.

## Alterações realizadas

1. **Marcação dos testes de defeito com `@Tag("defeito")`.**
   Os 2 testes que falham de propósito (`abreVenda_vendaNova_saveComExcecao_devePropagarErro`
   e `addProduto_salvarComExcecao_devePropagarErro`) receberam `@Tag("defeito")`
   e foi adicionado o import `org.junit.jupiter.api.Tag`.
   Motivo: o PIT exige suíte verde ("Mutation testing requires a green suite");
   a tag permite **excluir** esses testes apenas da medição de mutação, sem
   removê-los. Eles continuam no `mvn test` e no JaCoCo como evidência dos
   defeitos. A exclusão é feita no `pom.xml` via `<excludedGroups>defeito</excludedGroups>`.

2. **9 novos casos de teste para elevar o escore de mutação** (65% → 91%),
   usando `ArgumentCaptor` para asserir valores calculados e de retorno:
   - `busca` (2): asserem o `Page` retornado (matam `NullReturnVals`).
   - `lista` (1): cobre o método antes sem cobertura (mata `EmptyObjectReturnVals`; métodos 10/11 → 11/11).
   - `fechaVenda` a prazo com 2 parcelas (1): captura `valor_parcela` e a `sequencia` (matam `MathMutator`, `IncrementsMutator`, `PrimitiveReturns`).
   - `fechaVenda` à vista dinheiro (1): captura o valor do `CaixaLancamento`.
   - `fechaVenda` captura `vlFinal` passado a `vendas.fechaVenda` (1).
   - `fechaVenda` captura o `Receber` com `vlTotal` (1).
   - `fechaVenda` verifica `setPagamentotipo` na venda (1).
   - `fechaVenda` à vista não gera parcela (1): distingue o ramo "00".
   - Imports adicionados: `assertSame`, `times`, `java.util.List`,
     `ArgumentCaptor`, `Page`, `Parcela`.

3. **Nenhuma alteração no código de produção.** Os defeitos permanecem.

## Ponto crítico de corretude (verificado)

As asserções dos novos testes refletem o **comportamento ATUAL** do código,
inclusive o defeito de troca desconto/acréscimo (DEF-VENDA-003): nos métodos
`aprazo`/`avistaDinheiro` os parâmetros `acre`/`desc` estão invertidos em
relação à chamada. Por isso os valores esperados foram calculados segundo o que
o código realmente faz, não segundo a regra correta. Exemplos:

- a prazo, 2 parcelas de 50, desconto 10, acréscimo 4 → `desc=5`, `acre=2` →
  `valor_parcela = (50 + 5) - 2 = 53,0`.
- à vista, parcela 50, desconto 10, acréscimo 4 → `valor_lancamento = (50 + 10) - 4 = 56,0`.
- `vlFinal` e `vlTotal` (não passam pelos métodos trocados) = `(50 + 4) - 10 = 44,0`.

Isso é necessário porque um teste de mutação precisa **passar** na suíte
original. A denúncia do defeito permanece exclusivamente nos testes
`@Tag("defeito")`.

## Resultado verificado (container Docker, JDK 8 + Maven)

- `mvn -Dtest=VendaServiceTest test`: 43 testes, 2 falhas (apenas os de defeito).
- **JaCoCo (VendaService):** todas-arestas 34/38 = **89,5%**; instruções 90,1%;
  linhas 84,9%; métodos 11/11.
- **PIT (VendaService):** 66 mutações, 60 mortas = **91%**; test strength 95%.
- 6 mutantes sobreviventes, todos equivalentes/sem efeito observável
  (prints em `catch`, `qtdVezes` sem efeito, laço que preenche um `HashMap`
  nunca consumido). Detalhe no relatório de mutação.
