# Planejamento das Issues — Defeitos de `VendaService` (e impedimento de Estoque)

Este documento consolida os defeitos encontrados em **duas frentes** e os
organiza em issues prontas para abrir no GitHub:

1. **Testes unitários** (`src/test/java/net/originmobi/pdv/VendaServiceTest.java`)
   — casos que falham intencionalmente, com o defeito documentado em comentário.
2. **Testes manuais** (`docs/relatorios/testes-manuais-vendaservice.md`)
   — falhas observadas pela interface, com evidências em
   `docs/evidencias/venda/`.

> Rastreador oficial: **GitHub Issues**. Cada ficha em `docs/defeitos/` deve
> receber o link da issue correspondente após a abertura.

## Convenções sugeridas

- **Título:** `[VendaService] <resumo curto>` (ou `[Estoque]`).
- **Labels:** `bug`; severidade (`sev:alta` / `sev:media`); origem
  (`origem:teste-unitario` / `origem:teste-manual`); módulo (`modulo:venda` /
  `modulo:estoque`).
- **Corpo padrão:** Descrição · Passos para reproduzir · Comportamento atual ·
  Comportamento esperado · Causa raiz (arquivo/linha) · Evidência ·
  Correção sugerida · Referências (teste/ficha).
- **Milestone:** `Entrega 1` (registro) — a correção pode ficar para a Entrega 2.

## Mapa de defeitos → issues

| # | Origem | Defeito | Método/Arquivo | Severidade | Ficha | Issue |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | Unitário | `abreVenda` engole exceção do `save` e retorna `null` | `VendaService.abreVenda` | Alta | — | [#7](https://github.com/elojas2/trabalho-pdv-2026.1/issues/7) |
| 2 | Unitário | `addProduto` engole exceção e retorna `"ok"` | `VendaService.addProduto` | Alta | — | [#8](https://github.com/elojas2/trabalho-pdv-2026.1/issues/8) |
| 3 | Unitário | `removeProduto` sempre retorna `"ok"` (return dentro do try ignorado) | `VendaService.removeProduto` | Média | — | [#9](https://github.com/elojas2/trabalho-pdv-2026.1/issues/9) |
| 4 | Unitário | `fechaVenda` fecha a venda N vezes (chamada dentro do laço) | `VendaService.fechaVenda` | Média | — | [#10](https://github.com/elojas2/trabalho-pdv-2026.1/issues/10) |
| 5 | Manual | Fechamento sem título lança `"Zero length string"` | `VendaService.fechaVenda` / `VendaController.fechar` | Alta | DEF-VENDA-001 | [#11](https://github.com/elojas2/trabalho-pdv-2026.1/issues/11) |
| 6 | Manual | Cartão débito/crédito não fecha a venda (NPE) | `CartaoLancamentoService.lancamento` | Alta | DEF-VENDA-002 | [#12](https://github.com/elojas2/trabalho-pdv-2026.1/issues/12) |
| 7 | Manual | Desconto e acréscimo aplicados trocados | `VendaService.aprazo` / `avistaDinheiro` | Alta | DEF-VENDA-003 | [#13](https://github.com/elojas2/trabalho-pdv-2026.1/issues/13) |
| 8 | Manual | Entrada de estoque não aumenta o saldo (trigger) | `V1__cria_estrutura_inicial.sql` | Alta | DEF-ESTOQUE-001 | [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14) |

---

## ISSUE 1 — `abreVenda` engole exceção do `save` e retorna `null`

- **Título:** `[VendaService] abreVenda silencia exceção do save e retorna null`
- **Labels:** `bug`, `sev:alta`, `origem:teste-unitario`, `modulo:venda`

**Descrição**
No cadastro de uma venda nova, se `vendas.save(venda)` lança exceção, o bloco
`catch` a descarta com `e.getStackTrace()` (sem log, sem rethrow). O método
retorna o código da venda, que é `null` porque o `save` falhou. O chamador não
tem como distinguir sucesso de falha.

**Passos para reproduzir**
1. Cenário em que `save` falha (ex.: violação de constraint/DB indisponível).
2. Chamar `abreVenda` com uma venda nova (`codigo == null`).

**Comportamento atual**
Exceção suprimida; retorno `null`; o fluxo do controller segue como se tivesse
salvo (`redirect:/venda/null`).

**Comportamento esperado**
Propagar `RuntimeException` (ou retornar erro tratável) para que o chamador
sinalize a falha ao usuário e evite navegação inválida.

**Causa raiz**
`VendaService.abreVenda` — bloco `try { vendas.save(venda); } catch (Exception e) { e.getStackTrace(); }`.

**Correção sugerida**
No `catch`, logar e relançar `throw new RuntimeException("Erro ao abrir venda", e);`.

**Referências**
- Teste: `VendaServiceTest#abreVenda_vendaNova_saveComExcecao_devePropagarErro` (falha intencional).

---

## ISSUE 2 — `addProduto` engole exceção e retorna `"ok"`

- **Título:** `[VendaService] addProduto retorna "ok" mesmo quando salvar falha`
- **Labels:** `bug`, `sev:alta`, `origem:teste-unitario`, `modulo:venda`

**Descrição**
Quando `vendaProdutos.salvar(...)` lança exceção, o `catch` a descarta com
`e.getStackTrace()` e o método segue até `return "ok"`, informando sucesso
falso. O produto não é adicionado, mas a interface indica que foi.

**Passos para reproduzir**
1. Venda ABERTA.
2. Forçar falha em `vendaProdutos.salvar(...)`.
3. Chamar `addProduto`.

**Comportamento atual**
Retorna `"ok"`; produto não persistido.

**Comportamento esperado**
Propagar exceção ou retornar mensagem de erro distinguível de sucesso.

**Causa raiz**
`VendaService.addProduto` — `try { vendaProdutos.salvar(vendaProduto); } catch (Exception e) { e.getStackTrace(); }` seguido de `return "ok";`.

**Correção sugerida**
Relançar no `catch` ou retornar string de erro; o controller deve tratar.

**Referências**
- Teste: `VendaServiceTest#addProduto_salvarComExcecao_devePropagarErro` (falha intencional).

---

## ISSUE 3 — `removeProduto` sempre retorna `"ok"` (return dentro do try é ignorado)

- **Título:** `[VendaService] removeProduto retorna "ok" para venda fechada`
- **Labels:** `bug`, `sev:media`, `origem:teste-unitario`, `modulo:venda`

**Descrição**
Para uma venda FECHADA, o método deveria retornar `"Venda fechada"`. Porém, o
`return "Venda fechada"` está no `else` dentro do bloco `try`, e o fluxo ainda
alcança o `return "ok"` fora do `try`. Na prática, remover produto de venda
fechada retorna `"ok"`, e o controller interpreta como sucesso.

**Passos para reproduzir**
1. Venda FECHADA.
2. Chamar `removeProduto(posicao, codVenda)`.

**Comportamento atual**
Retorna `"ok"`.

**Comportamento esperado**
Retornar `"Venda fechada"` e não remover o produto.

**Causa raiz**
`VendaService.removeProduto` — estrutura `try/else return` com `return "ok"` final fora do controle de fluxo do `else`.

**Correção sugerida**
Reestruturar o controle de fluxo para que o retorno de "Venda fechada" encerre o
método (ex.: retornar direto na verificação de situação antes do bloco de remoção).

**Referências**
- Teste: `VendaServiceTest#removeProduto_vendaFechada_deveRetornarMensagemErro` (falha intencional).

---

## ISSUE 4 — `fechaVenda` fecha a venda N vezes (chamada dentro do laço)

- **Título:** `[VendaService] fechaVenda chama vendas.fechaVenda dentro do loop de pagamento`
- **Labels:** `bug`, `sev:media`, `origem:teste-unitario`, `modulo:venda`

**Descrição**
A chamada a `vendas.fechaVenda(...)` está **dentro** do laço
`for (formaPagar)`. Com pagamento único o efeito é o mesmo, mas com múltiplas
formas de pagamento (ex.: `00/30`) a venda é fechada N vezes, gerando
atualizações redundantes e potencial inconsistência.

**Passos para reproduzir**
1. Fechar venda com forma de pagamento composta (mais de um elemento em `formaPagar`).
2. Observar múltiplas chamadas de fechamento.

**Comportamento atual**
`vendas.fechaVenda` executado uma vez por iteração do laço.

**Comportamento esperado**
Fechar a venda **uma única vez**, após processar todas as formas de pagamento.

**Causa raiz**
`VendaService.fechaVenda` — bloco `vendas.fechaVenda(...)` posicionado dentro do `for`.

**Correção sugerida**
Mover a chamada de fechamento e a `movimentaEstoque` para **depois** do laço.

**Referências**
- Teste: `VendaServiceTest#fechaVenda_aVistaDinheiro_deveChamarFechaVendaComSituacaoFechada` (comentário documenta o risco; a verificação usa `times(1)`).
- Relaciona-se ao cenário manual pendente de pagamento composto.

---

## ISSUE 5 — Fechamento sem título lança "Zero length string"

- **Título:** `[VendaService] Fechar venda sem título selecionado lança "Zero length string"`
- **Labels:** `bug`, `sev:alta`, `origem:teste-manual`, `modulo:venda`

**Descrição**
Ao fechar a venda sem um título selecionado, o campo `titulos` chega vazio e
`Long.decode("")` lança `NumberFormatException: Zero length string`, exposta
crua ao usuário.

**Passos para reproduzir** — ver ficha DEF-VENDA-001.

**Comportamento atual**
Alerta "Zero length string"; venda não fecha.

**Comportamento esperado**
Mensagem de validação amigável; venda permanece aberta.

**Causa raiz**
`VendaService.fechaVenda` → `tituloService.busca(Long.decode(titulos[i]))` com
`titulos[i]` vazio; `VendaController.fechar` faz `"".split(",")` → `[""]`.

**Correção sugerida**
Validar `titulos`/`vlParcelas` vazios antes de decodificar e lançar mensagem de
negócio.

**Evidência**
`docs/evidencias/venda/Evidencia 2.png`

**Referências**
- Ficha: `docs/defeitos/DEF-VENDA-001-zero-length-string.md`
- Caso: CT-VENDA-02.

---

## ISSUE 6 — Cartão débito/crédito não fecha a venda (NullPointerException)

- **Título:** `[VendaService] Fechamento no cartão falha com NullPointerException (título sem máquina)`
- **Labels:** `bug`, `sev:alta`, `origem:teste-manual`, `modulo:venda`

**Descrição**
No fechamento com título de cartão, `CartaoLancamentoService.lancamento` acessa
`titulo.get().getMaquina().getTaxa_debito()`. Como o campo Máquina está
comentado no formulário de título, títulos de cartão têm `maquina == null` e
ocorre `NullPointerException`; a venda não fecha ("No message available").

**Passos para reproduzir** — ver ficha DEF-VENDA-002.

**Comportamento atual**
Venda não fecha; erro "No message available".

**Comportamento esperado**
Venda fechada com lançamento de cartão registrado.

**Causa raiz**
`CartaoLancamentoService.lancamento` — `getMaquina()` nulo; formulário
`templates/titulo/form.html` com campo Máquina comentado.

**Correção sugerida**
Reabilitar/validar o campo Máquina para títulos de cartão; validar `getMaquina()`
nulo com mensagem de negócio.

**Evidência**
`docs/evidencias/venda/Evidencia 6.png` (débito),
`Evidencia 4.png` (crédito).

**Referências**
- Ficha: `docs/defeitos/DEF-VENDA-002-cartao-nao-fecha.md`
- Casos: CT-VENDA-03, CT-VENDA-04.

---

## ISSUE 7 — Desconto e acréscimo aplicados trocados

- **Título:** `[VendaService] Desconto e acréscimo são trocados no cálculo do valor`
- **Labels:** `bug`, `sev:alta`, `origem:teste-manual`, `modulo:venda`

**Descrição**
As chamadas a `aprazo`/`avistaDinheiro` passam os argumentos na ordem
`desc, acre`, enquanto as assinaturas declaram `(..., Double acre, Double desc)`.
Os papéis ficam invertidos e o valor final fica incorreto quando
desconto ≠ acréscimo.

**Passos para reproduzir** — ver ficha DEF-VENDA-003 (produtos 3,50; desconto
1,00; acréscimo 0,95; esperado 3,45; obtido 3,50).

**Comportamento atual**
Valor final gravado incorreto (R$ 3,50).

**Comportamento esperado**
`(produtos + acréscimo) − desconto` = R$ 3,45.

**Causa raiz**
`VendaService.fechaVenda` (ordem dos argumentos) × assinaturas de
`aprazo`/`avistaDinheiro` (ordem dos parâmetros).

**Correção sugerida**
Alinhar a ordem dos parâmetros entre chamada e assinatura; adicionar teste
unitário com desconto ≠ acréscimo.

**Evidência**
`docs/evidencias/venda/Evidencia desc-acresc 1.png` e
`Evidencia desc-acresc 2.png`.

**Referências**
- Ficha: `docs/defeitos/DEF-VENDA-003-desconto-acrescimo-trocados.md`
- Caso: CT-VENDA-06.

---

## ISSUE 8 — Entrada de estoque não aumenta o saldo (trigger de banco)

- **Título:** `[Estoque] Processar ajuste de entrada não aumenta o saldo (trigger só subtrai)`
- **Labels:** `bug`, `sev:alta`, `origem:teste-manual`, `modulo:estoque`

**Descrição**
O trigger `atualiza_produto_estoque_AFTER_INSERT` sempre subtrai
(`qtd_estoque - qtd`), ignorando o `tipo` (ENTRADA/SAÍDA), e a guarda
`IF(@qtd <= @qtd_estoque)` impede o primeiro ajuste (saldo inicial 0). Entrada
de estoque nunca aumenta o saldo.

> **Nota:** módulo de responsabilidade da Natalia. Registrado aqui como
> **impedimento** dos testes de venda (produto controlado fica sem saldo).
> Coordenar a abertura da issue com a responsável.

**Passos para reproduzir** — ver ficha DEF-ESTOQUE-001.

**Comportamento atual**
Saldo permanece 0 após processar entrada.

**Comportamento esperado**
Saldo = saldo anterior + quantidade da entrada.

**Causa raiz**
`V1__cria_estrutura_inicial.sql` — trigger sem tratar `tipo` e com guarda
inadequada.

**Correção sugerida**
Reescrever o trigger para somar em ENTRADA e subtrair em SAÍDA (ou centralizar a
regra na aplicação).

**Evidência**
`docs/evidencias/venda/Evidencia 10.png` (venda bloqueada por
estoque insuficiente, consequência do saldo não incrementado).

**Referências**
- Ficha: `docs/defeitos/DEF-ESTOQUE-001-entrada-nao-aumenta-saldo.md`
- Caso: CT-VENDA-10.

---

## Ordem de abertura sugerida

1. Issues 5, 6, 7 (defeitos de venda confirmados por evidência manual) — maior valor de demonstração.
2. Issue 8 (impedimento de estoque) — coordenar com a Natalia.
3. Issues 1–4 (defeitos estruturais expostos pelos testes unitários).

Após abrir cada issue, colar o link na seção **Rastreamento** da ficha
correspondente em `docs/defeitos/` e na coluna de defeito do relatório de testes
manuais.
