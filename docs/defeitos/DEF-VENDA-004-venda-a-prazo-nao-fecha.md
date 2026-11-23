# DEF-VENDA-004 — Venda a prazo não fecha (insert da parcela sem `data_alteracao`)

| Campo | Valor |
| --- | --- |
| ID | DEF-VENDA-004 |
| Módulo | Venda |
| Classe sob teste | `net.originmobi.pdv.service.VendaService` (método `aprazo`) → `ParcelaRepository.gerarparcela` |
| Funcionalidade | Fechamento de venda a prazo (formas "A Prazo" e "Uma entrada + 30") |
| Encontrado por | Alexandre Porto, pelo teste de sistema `VendaSeleniumIT` (CT-VENDA-04 não tinha sido executado manualmente) |
| Data | 23/11/2026 |
| Severidade | Alta (nenhuma venda a prazo fecha) |
| Status | Aberto |

## Passos para reproduzir

1. Abrir um pedido com cliente e um produto que não controle estoque.
2. Em **Gerar Venda**, escolher a forma **A Prazo** e um título (ex.: Dinheiro).
3. Clicar em **Pagar**.

## Resultado esperado

"Venda finalizada com sucesso": o pedido passa a FECHADA e é gerada uma parcela com
vencimento em 30 dias.

## Resultado obtido

O alerta mostra **"No message available"** e o pedido continua ABERTA (a transação
é desfeita). O mesmo ocorre com a forma "Uma entrada + 30", que também gera
parcela a prazo.

## Causa raiz

O log da aplicação mostra o erro do banco, escondido da tela:

```
insert into parcela (valor_total, valor_desconto, valor_acrescimo, valor_recebido, valor_restante, receber_codigo, quitado, sequencia, data_cadastro, data_vencimento) values (...)
SQL Error: 1364, SQLState: HY000
Field 'data_alteracao' doesn't have a default value
```

- A migração `V1__cria_estrutura_inicial.sql` declara
  `data_alteracao TIMESTAMP NOT NULL` na tabela `parcela` **sem `DEFAULT`**
  (as outras 30 tabelas que têm essa coluna usam `DEFAULT CURRENT_TIMESTAMP`;
  além de `parcela`, só `mva` está sem).
- O `insert` nativo de `ParcelaRepository.gerarparcela` não informa `data_alteracao`.
- No MySQL 8.0 (`explicit_defaults_for_timestamp = ON`, verificado na 8.0.46), o
  banco não preenche essa coluna sozinho e recusa o `insert`.
- `VendaService.aprazo` captura a exceção, descarta a causa (`e.getMessage();` sem
  uso) e lança uma exceção **sem mensagem**, por isso a tela mostra o texto
  genérico "No message available". É o mesmo padrão de exceção silenciada dos
  defeitos [#7](https://github.com/elojas2/trabalho-pdv-2026.1/issues/7) e
  [#8](https://github.com/elojas2/trabalho-pdv-2026.1/issues/8).

## Correção sugerida

- Declarar `DEFAULT CURRENT_TIMESTAMP` em `parcela.data_alteracao`, em uma nova
  migração Flyway (não alterar a V1), ou informar a coluna no `insert`.
- Em `aprazo`, relançar a exceção com a causa e uma mensagem de negócio.

## Evidências

- Teste de sistema `VendaSeleniumIT#aPrazoComCliente_fecha` (`@Tag("defeito")`):
  `expected: <Venda finalizada com sucesso> but was: <No message available>`
  ([relatório](../relatorios/testes-sistema-selenium.md)).
- Log da aplicação com o `SQL Error: 1364` descrito acima.

## Rastreamento

- [ ] Abrir issue correspondente no GitHub e referenciar aqui.
