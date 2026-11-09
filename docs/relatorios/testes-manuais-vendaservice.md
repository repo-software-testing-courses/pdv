# Relatório de Testes Manuais — VendaService

**Módulo:** Venda
**Classe sob teste:** `net.originmobi.pdv.service.VendaService`
**Funcionalidade:** Fechamento de venda (à vista em dinheiro, cartão débito/crédito e a prazo)
**Responsável:** Alexandre Porto
**Data de execução:** 23/09/2026
**Ambiente:** Docker (`docker compose up -d`), aplicação em <http://localhost:8080>, usuário `gerente`

> Técnica: teste funcional (caixa-preta), nível de sistema, executado pela
> interface web. Os resultados esperados refletem a **regra de negócio**, não o
> comportamento atual do código; divergências são registradas como defeitos.

## Pré-condições gerais

- Ambiente no ar e login efetuado (smoke test: acessar o módulo Venda).
- Título(s) cadastrado(s) em `/titulos/form` conforme o tipo do cenário
  (Dinheiro/DIN, Cartão Débito/CARTDEB, Cartão Crédito/CARTCRED).
- Produto cadastrado. Para os cenários que dependem de saldo, ver
  [DEF-ESTOQUE-001](../defeitos/DEF-ESTOQUE-001-entrada-nao-aumenta-saldo.md)
  (entrada de estoque não aumenta o saldo — usar produto "Controla estoque = NÃO"
  como contorno).
- Cliente cadastrado (obrigatório nos cenários a prazo).
- Caixa aberto (obrigatório nos cenários à vista em dinheiro).

## Resumo da execução

| ID | Cenário | Status | Defeito |
| --- | --- | --- | --- |
| CT-VENDA-01 | À vista em dinheiro, caixa aberto, com título | Passou | — |
| CT-VENDA-02 | À vista em dinheiro, sem título selecionado | Falhou | [DEF-VENDA-001](../defeitos/DEF-VENDA-001-zero-length-string.md) |
| CT-VENDA-03 | À vista cartão débito | Falhou | [DEF-VENDA-002](../defeitos/DEF-VENDA-002-cartao-nao-fecha.md) |
| CT-VENDA-04 | À vista cartão crédito | Falhou | [DEF-VENDA-002](../defeitos/DEF-VENDA-002-cartao-nao-fecha.md) |
| CT-VENDA-05 | A prazo sem cliente | Passou | — |
| CT-VENDA-06 | Fechamento com desconto e acréscimo diferentes | Falhou | [DEF-VENDA-003](../defeitos/DEF-VENDA-003-desconto-acrescimo-trocados.md) |
| CT-VENDA-07 | À vista em dinheiro com caixa fechado | Passou | — |
| CT-VENDA-08 | Venda sem valor / sem produtos | Passou | — |
| CT-VENDA-09 | Fechar venda já fechada | Passou | — |
| CT-VENDA-10 | Estoque insuficiente | Passou | — |

## Casos de teste detalhados

### CT-VENDA-01 — À vista em dinheiro, caixa aberto, com título

- **Pré-condições:** caixa aberto; título do tipo DIN cadastrado; venda aberta com produto.
- **Passos:** modal de pagamento → forma à vista (`00`) → título Dinheiro → Pagar.
- **Dados:** valor produtos R$ 3,50; desconto 0; acréscimo 0.
- **Resultado esperado:** venda FECHADA; lançamento de entrada no caixa; mensagem "Venda finalizada com sucesso".
- **Resultado obtido:** "Venda finalizada com sucesso"; venda passa a FECHADA.
- **Status:** Passou.
- **Evidências:**
  - ![Venda finalizada com sucesso](../evidencias/venda/Evidencia%201.png)
  - ![Pedido FECHADA](../evidencias/venda/Evidencia%205.png)

### CT-VENDA-02 — À vista em dinheiro, sem título selecionado

- **Pré-condições:** caixa aberto; venda aberta; **nenhum título selecionado** no modal.
- **Passos:** modal de pagamento → forma à vista → (sem título) → Pagar.
- **Resultado esperado:** mensagem de validação amigável ("selecione a forma/título de pagamento"); venda permanece ABERTA.
- **Resultado obtido:** erro técnico **"Zero length string"**; venda não fecha.
- **Status:** Falhou → **DEF-VENDA-001**.
- **Evidência:**
  - ![Zero length string](../evidencias/venda/Evidencia%202.png)

### CT-VENDA-03 — À vista cartão débito

- **Pré-condições:** título do tipo CARTDEB cadastrado; venda aberta com produto.
- **Passos:** modal → forma à vista → título Débito → Pagar.
- **Resultado esperado:** venda FECHADA; lançamento de cartão registrado.
- **Resultado obtido:** **a venda não fecha**; erro "No message available" (NullPointerException).
- **Status:** Falhou → **DEF-VENDA-002** (título de cartão sem máquina associada).
- **Evidência:**
  - ![Cartão débito - No message available](../evidencias/venda/Evidencia%206.png)

### CT-VENDA-04 — À vista cartão crédito

- **Pré-condições:** título do tipo CARTCRED cadastrado; venda aberta com produto.
- **Passos:** modal → forma à vista → título Crédito → Pagar.
- **Resultado esperado:** venda FECHADA; lançamento de cartão registrado.
- **Resultado obtido:** **a venda não fecha**; erro "No message available" (NullPointerException).
- **Status:** Falhou → **DEF-VENDA-002**.
- **Evidência:**
  - ![Cartão crédito - No message available](../evidencias/venda/Evidencia%204.png)

### CT-VENDA-05 — A prazo sem cliente

- **Pré-condições:** venda aberta **sem cliente**; forma a prazo.
- **Passos:** modal → forma a prazo → Pagar.
- **Resultado esperado:** bloqueio com mensagem "Venda sem cliente, verifique".
- **Resultado obtido:** "Venda sem cliente, verifique"; venda bloqueada.
- **Status:** Passou.
- **Evidência:**
  - ![Venda sem cliente, verifique](../evidencias/venda/Evidencia%203.png)

### CT-VENDA-06 — Fechamento com desconto e acréscimo diferentes

- **Pré-condições:** venda aberta com produtos totalizando R$ 3,50.
- **Passos:** modal → desconto 1,00 e acréscimo 0,95 → Pagar.
- **Dados:** produtos 3,50; desconto 1,00; acréscimo 0,95.
- **Resultado esperado:** valor final = (3,50 + 0,95) − 1,00 = **R$ 3,45**.
- **Resultado obtido:** o sistema exibe "Venda finalizada com sucesso", mas o valor final gravado permanece **R$ 3,50** (venda FECHADA com R$ 3,50).
- **Status:** Falhou → **DEF-VENDA-003** (desconto/acréscimo trocados nos métodos auxiliares).
- **Evidências:**
  - ![Desconto 1,00 e acréscimo 0,95 - venda finalizada](../evidencias/venda/Evidencia%20desc-acresc%201.png)
  - ![Pedido FECHADA com valor 3,50](../evidencias/venda/Evidencia%20desc-acresc%202.png)

### CT-VENDA-07 — À vista em dinheiro com caixa fechado

- **Pré-condições:** **caixa fechado**; venda aberta com produto; título Dinheiro.
- **Passos:** modal → forma à vista → título Dinheiro → Pagar.
- **Resultado esperado:** erro "nenhum caixa aberto"; venda permanece ABERTA.
- **Resultado obtido:** "nenhum caixa aberto"; venda não fecha.
- **Status:** Passou.
- **Evidência:**
  - ![nenhum caixa aberto](../evidencias/venda/Evidencia%207.png)

### CT-VENDA-08 — Venda sem valor / sem produtos

- **Pré-condições:** venda aberta com valor total R$ 0,00.
- **Passos:** modal → Pagar.
- **Resultado esperado:** "Venda sem valor, verifique".
- **Resultado obtido:** "Venda sem valor, verifique".
- **Status:** Passou.
- **Evidência:**
  - ![Venda sem valor, verifique](../evidencias/venda/Evidencia%208.png)

### CT-VENDA-09 — Fechar venda já fechada

- **Pré-condições:** venda já FECHADA.
- **Passos:** reabrir a venda e tentar Pagar novamente.
- **Resultado esperado:** venda permanece FECHADA; nova ação de fechamento não é permitida.
- **Resultado obtido:** venda permanece FECHADA (botões de ação desabilitados).
- **Status:** Passou.
- **Evidência:**
  - ![Pedido FECHADA com cliente](../evidencias/venda/Evidencia%209.png)

### CT-VENDA-10 — Estoque insuficiente

- **Pré-condições:** produto que **controla estoque** com saldo menor que o vendido.
- **Passos:** venda com o produto (ex.: Picolé R$ 6,50) → modal → forma à vista → título Dinheiro → Pagar.
- **Resultado esperado:** bloqueio informando estoque insuficiente; venda não fecha.
- **Resultado obtido:** "O produto de código 1 não tem estoque suficiente, verifique".
- **Status:** Passou.
- **Evidência:**
  - ![Estoque insuficiente](../evidencias/venda/Evidencia%2010.png)

## Defeitos encontrados

| Defeito | Título | Documento | Issue |
| --- | --- | --- | --- |
| DEF-VENDA-001 | Fechamento sem título lança "Zero length string" | [ficha](../defeitos/DEF-VENDA-001-zero-length-string.md) | [#11](https://github.com/elojas2/trabalho-pdv-2026.1/issues/11) |
| DEF-VENDA-002 | Cartão débito/crédito não fecha a venda (NPE) | [ficha](../defeitos/DEF-VENDA-002-cartao-nao-fecha.md) | [#12](https://github.com/elojas2/trabalho-pdv-2026.1/issues/12) |
| DEF-VENDA-003 | Desconto e acréscimo aplicados trocados | [ficha](../defeitos/DEF-VENDA-003-desconto-acrescimo-trocados.md) | [#13](https://github.com/elojas2/trabalho-pdv-2026.1/issues/13) |
| DEF-ESTOQUE-001 | Entrada de estoque não aumenta o saldo (impedimento) | [ficha](../defeitos/DEF-ESTOQUE-001-entrada-nao-aumenta-saldo.md) | [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14) |

## Pendências

- [x] Abrir as Issues no GitHub para cada defeito e referenciá-las nas fichas (issues [#11](https://github.com/elojas2/trabalho-pdv-2026.1/issues/11)–[#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14)).
- [ ] Cadastrar ao menos um caso (sugestão: CT-VENDA-01) no TestLink.
