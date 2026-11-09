# DEF-CAIXA-001 — Fechar caixa já fechado exibe alerta vazio

| Campo | Valor |
| --- | --- |
| ID | DEF-CAIXA-001 |
| Módulo | Caixa |
| Classe sob teste | `net.originmobi.pdv.service.CaixaService` (método `fechaCaixa`) |
| Funcionalidade | Fechamento de caixa (testes manuais) |
| Caso de teste relacionado | TestLink 458-3 — Abertura e fechamento do caixa |
| Responsável pelo achado | Eloyse Fernanda |
| Data | 08/11/2026 |
| Severidade | Média (o usuário não recebe nenhum retorno e a tela fica presa no carregamento; os dados não são corrompidos) |
| Status | Aberto |

## Pré-condição que expõe o defeito

A tela **Gerenciar Caixa** só verifica se o caixa está fechado **no
carregamento da página**: o campo oculto `data_fechamento`
(`templates/caixa/gerenciar.html`) faz a página exibir "FECHADO" e bloquear os
botões. Se o caixa for fechado depois que a página já foi carregada — em outra
aba, ou por outro operador —, o botão **Fechar** continua habilitado.

Acessar diretamente `/caixa/gerenciar/{codigo}` de um caixa fechado **não**
reproduz o defeito, porque nesse caso a página já carrega bloqueada
([evidência](../evidencias/caixa/DEF-CAIXA-001-bloqueio-ao-carregar.png)).

## Passos para reproduzir

1. Logado como `gerente`, abrir um caixa e entrar em **Gerenciar Caixa**.
2. Copiar a URL (`/caixa/gerenciar/{codigo}`) e abri-la numa segunda aba.
3. Na aba 1, clicar em **Fechar**, informar a senha `123` e confirmar.
   O sistema exibe "Caixa fechado com sucesso".
4. Na aba 2, **sem recarregar**, clicar em **Fechar**, informar a senha `123`
   e confirmar.

## Resultado obtido

O navegador exibe um alerta **vazio** ("localhost:8080 diz" sem texto) e o
indicador de carregamento do modal continua girando. No log do servidor aparece
apenas a linha `Caixa já esta fechado`, impressa via `System.out`.

## Resultado esperado

Exibir a mensagem de negócio "Caixa já esta fechado" (que o serviço já produz)
e encerrar o indicador de carregamento.

## Causa raiz (análise de código)

- `CaixaService.fechaCaixa` lança corretamente
  `RuntimeException("Caixa já esta fechado")`.
- `CaixaController.fecha` envolve a chamada num `try/catch (Exception e)` que
  apenas faz `System.out.println(e.getMessage())` e devolve `mensagem`, que
  continua com o valor inicial `""`.
- `static/js/caixa/caixa.js` (handler `.btn-fechar-caixa`) recebe a resposta
  HTTP 200 com corpo vazio e faz `alert(e)`, exibindo o alerta em branco.

O mesmo caminho engole as demais exceções de `fechaCaixa` — por exemplo, a
falha ao salvar ("Ocorreu um erro ao fechar o caixa, chame o suporte"), que
também chegaria ao usuário como um alerta vazio.

## Correção sugerida

No `catch` do `CaixaController.fecha`, devolver a mensagem da exceção
(`mensagem = e.getMessage();`) em vez de apenas imprimi-la. A tela
**Gerenciar Caixa** também deveria recarregar o status após o fechamento, para
não manter o botão habilitado.

## Evidência

- [x] Alerta vazio na segunda aba:
  [`DEF-CAIXA-001-alerta-vazio.png`](../evidencias/caixa/DEF-CAIXA-001-alerta-vazio.png)
- [x] Página carregada já fechada bloqueia o botão:
  [`DEF-CAIXA-001-bloqueio-ao-carregar.png`](../evidencias/caixa/DEF-CAIXA-001-bloqueio-ao-carregar.png)

## Rastreamento

- [x] Abrir Issue correspondente no GitHub e referenciar aqui: [#15](https://github.com/elojas2/trabalho-pdv-2026.1/issues/15).
