# DEF-AJUSTE-001 — Ajuste de saída aumenta o saldo do produto

| Campo | Valor |
| --- | --- |
| ID | DEF-AJUSTE-001 |
| Módulo | Estoque / Ajuste |
| Origem | Trigger de banco `atualiza_produto_estoque_AFTER_INSERT` (`V1__cria_estrutura_inicial.sql`) |
| Encontrado por | Natalia de Abreu Lamas (teste de integração `AjusteServiceIT` e teste manual CT-AJU-02) |
| Data | 21/11/2026 |
| Severidade | Alta (uma saída de estoque aumenta o saldo, deixando o estoque inconsistente) |
| Status | Aberto (não corrigido) |
| Issue | [#27](https://github.com/elojas2/trabalho-pdv-2026.1/issues/27) |

## Passos para reproduzir

1. Acessar **Produtos → Ajuste Estoque** e criar um novo ajuste.
2. Adicionar o produto **Picolé** (controla estoque = SIM) com quantidade **-3**.
3. Clicar em **Processar** e confirmar.
4. Conferir o saldo do produto.

## Resultado obtido

Mensagem "Ajuste realizado com sucesso" e o saldo do Picolé **sobe 3**
(de 0 para 3 no teste manual; de 3 para 6 no Selenium).

## Resultado esperado

Uma saída não pode aumentar o saldo: o saldo deveria diminuir 3, ou o sistema
deveria recusar a saída por falta de estoque.

## Causa raiz

A `AjusteService` registra corretamente a movimentação como **SAÍDA**, mas o
trigger `atualiza_produto_estoque_AFTER_INSERT` sempre calcula
`saldo - quantidade`, sem considerar o tipo. Como a quantidade da saída é
negativa, `saldo - (-3)` vira `saldo + 3`. É a mesma causa do
[DEF-ESTOQUE-001](DEF-ESTOQUE-001-entrada-nao-aumenta-saldo.md) (#14), vista
pelo lado da saída.

## Evidências

- Teste de integração: `AjusteServiceIT#processar_Saida_DeveDiminuirSaldo` (`@Tag("defeito")`)
- Teste manual: CT-AJU-02 em [`testes-manuais-ajusteservice.md`](../relatorios/testes-manuais-ajusteservice.md)
  ([ajuste montado](../evidencias/ajuste/CT-AJU-02-ajuste-montado.png),
  [processado](../evidencias/ajuste/CT-AJU-02-processado.png),
  [saldo depois](../evidencias/ajuste/CT-AJU-02-saldo-depois.png))
- Teste de sistema: cenário 3 do `AjusteSeleniumIT`

## Correção sugerida

A mesma do DEF-ESTOQUE-001: o trigger deve somar nas ENTRADAS e subtrair nas
SAÍDAS, usando o valor absoluto da quantidade.
