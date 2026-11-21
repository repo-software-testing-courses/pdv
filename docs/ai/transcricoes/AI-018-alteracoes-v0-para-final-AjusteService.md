# Alterações da v0 para a versão final — AjusteServiceTest (Entrega 2)

Referente à entrada [AI-018](../AI-LOG.md#ai-018--entrega-2-ampliação-dos-testes-unitários-de-ajusteservice).

- **Versão de partida (antes da IA nesta etapa):** a suíte de 12 casos de
  04/11/2026 (AI-011), no pacote `net.originmobi.pdv.service`, em
  `src/test/java/net/originmobi/pdv/AjusteServiceTest.java`.
- **Solução inicial da IA (v0, 19 casos):**
  [`AI-018-v0-AjusteServiceTest.java`](AI-018-v0-AjusteServiceTest.java).
- **Versão final:** `src/test/java/net/originmobi/pdv/AjusteServiceTest.java`.

## O que a v0 trouxe em relação à suíte de 12 casos

1. **Pacote corrigido** para `net.originmobi.pdv`, coerente com a pasta do
   arquivo e com as demais suítes do grupo.
2. **Usuário logado isolado** em `novo()`: `mockStatic(Aplicacao.class)` no
   lugar da dependência do `SecurityContextHolder`; o teste passou a conferir,
   com `ArgumentCaptor`, o status A PROCESSAR, o usuário e a data gravados.
3. **Valores-limite** de `qtd_alteracao` (1 → ENTRADA e 0 → SAIDA) e um caso
   com vários produtos (laço `for` com 3 itens, ENTRADA e SAIDA misturadas).
4. **Falha no meio do laço:** o segundo produto falha e o terceiro não é
   processado; o ajuste não é gravado.
5. **Asserções sobre o que é gravado:** observação, data de processamento e
   origem exata da movimentação ("Referente ao ajuste de estoque 1").
6. **Busca com código inexistente** e `lista` conferida com `assertSame` e
   `never()` no ramo não tomado.
7. **Teste de defeito** `processar_CodigoInexistente_DeveLancarErroTratado`,
   marcado com `@Tag("defeito")` (issue
   [#28](https://github.com/elojas2/trabalho-pdv-2026.1/issues/28)).

## Alterações da v0 para a versão final

| Alteração | Motivo |
| --- | --- |
| `hoje()` passou a usar `LocalDate.now(ZoneId.of("America/Sao_Paulo"))` (e o import de `ZoneId`) | depois da correção do Sonar (AI-019), o `AjusteService` calcula a data no fuso de São Paulo; o container de testes usa UTC, e entre 21h e meia-noite as datas divergiriam, gerando falha falsa |

Nenhum caso foi removido ou reescrito: os 19 casos da v0 permaneceram.

## Verificação feita pela integrante

- Execução da suíte em container JDK 8: 19 casos, 1 falha (a intencional,
  `@Tag("defeito")`), antes e depois da correção do Sonar.
- Medição da suíte da Entrega 1 (7 casos) para comparação: 7/10 arestas (70%),
  contra 10/10 (100%) da versão final.
- PIT: 17/18 (94%) com o código original — o sobrevivente foi analisado como
  equivalente (`System.out.println`) — e 17/17 (100%) após o Sonar.
  