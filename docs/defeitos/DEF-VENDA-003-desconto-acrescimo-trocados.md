# DEF-VENDA-003 — Desconto e acréscimo aplicados trocados no valor da parcela

| Campo | Valor |
| --- | --- |
| ID | DEF-VENDA-003 |
| Módulo | Venda |
| Classe sob teste | `net.originmobi.pdv.service.VendaService` (métodos `aprazo` e `avistaDinheiro`) |
| Funcionalidade | Fechamento de venda com desconto e/ou acréscimo |
| Encontrado por | Alexandre Porto |
| Data | 23/09/2026 |
| Severidade | Média/Alta (valor financeiro incorreto) |
| Status | Aberto |

## Passos para reproduzir

1. Abrir venda com produtos totalizando **R$ 3,50**.
2. No modal de pagamento, informar **Desconto = 1,00** e **Acréscimo = 0,95**.
3. Fechar a venda (à vista em dinheiro).

## Resultado obtido

O sistema exibe "Venda finalizada com sucesso", mas o valor final gravado
permanece **R$ 3,50** (venda FECHADA com R$ 3,50). Esperava-se **R$ 3,45**.

## Resultado esperado

Valor final = `(produtos + acréscimo) − desconto` = `(3,50 + 0,95) − 1,00` =
**3,45**.

## Causa raiz (análise de código)

No `fechaVenda`, os valores unitários são calculados corretamente:

```java
Double desc = desconto / vlParcelas.length;   // desconto
Double acre = acrescimo / vlParcelas.length;   // acréscimo
```

Mas as chamadas aos métodos auxiliares passam os argumentos **na ordem
`desc, acre`**:

```java
qtdVezes = avistaDinheiro(vlprodutos, vlParcelas, formaPagar, qtdVezes, i, desc, acre);
...
sequencia = aprazo(vlprodutos, vlParcelas, dataAtual, formaPagar, qtdVezes, sequencia, receber, i, desc, acre);
```

Enquanto as assinaturas dos métodos declaram os dois últimos parâmetros como
**`Double acre, Double desc`** (invertidos):

```java
private int aprazo(..., int i, Double acre, Double desc) { ... }
private int avistaDinheiro(..., int i, Double acre, Double desc) { ... }
```

Ou seja, o que é passado como desconto é recebido como acréscimo e vice-versa.
Dentro dos métodos o cálculo é `(valor + acre) − desc`. Com os papéis trocados,
o valor final da venda não corresponde ao esperado quando desconto ≠ acréscimo —
confirmado em teste manual (esperado R$ 3,45; obtido R$ 3,50). Com
desconto = acréscimo (ou ambos zero) o defeito fica mascarado.

> Observação: este defeito também não é coberto pelos testes unitários atuais,
> pois todos usam desconto/acréscimo = 0. Recomenda-se adicionar caso unitário
> com desconto ≠ acréscimo para fixar o comportamento após a correção.

## Correção sugerida

Alinhar a ordem dos parâmetros entre a chamada e a assinatura (renomear/reordenar
para `..., Double desc, Double acre`) e revisar o cálculo interno
`(valor + acre) − desc` para garantir a semântica correta.

## Evidências

- `docs/evidencias/venda/Evidencia desc-acresc 1.png` — modal com desconto 1,00 e acréscimo 0,95, "Venda finalizada com sucesso".
- `docs/evidencias/venda/Evidencia desc-acresc 2.png` — pedido FECHADA com valor R$ 3,50.

## Rastreamento

- [x] Abrir Issue correspondente no GitHub e referenciar aqui: [#13](https://github.com/elojas2/trabalho-pdv-2026.1/issues/13).
