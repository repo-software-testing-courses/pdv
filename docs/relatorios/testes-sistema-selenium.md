# Testes de sistema (Selenium)

Critério da Entrega 2: implementar testes de sistema considerando requisitos
funcionais, com Selenium.

## AjusteService — Natalia de Abreu Lamas

| Item | Valor |
| --- | --- |
| Ferramenta | Selenium IDE 3.17.4 (extensão do Firefox) |
| Projeto Selenium | [`src/test/selenium/AjusteEstoque.side`](../../src/test/selenium/AjusteEstoque.side) |
| Teste | `CT-AJU-01 processar ajuste de entrada` |
| Requisito funcional | processar um ajuste de estoque pela interface web |
| Resultado | **Passou** (22 comandos, 0 falhas) |

![Execução do teste no Selenium IDE](../evidencias/ajuste/selenium-CT-AJU-01-passou.png)

### O que o teste faz

Automatiza o caso de teste manual
[CT-AJU-01](testes-manuais-ajusteservice.md#ct-aju-01--processar-ajuste-de-entrada-10):

1. Sai do sistema (`/logout`) e faz login como `gerente`, para começar sempre
   do mesmo estado.
2. Acessa **Produtos → Ajuste Estoque** e cria um novo ajuste.
3. Seleciona o produto **Picolé**, clica em **Inserir** e responde `10` na
   janela de quantidade, conferindo o texto da pergunta
   (`assert prompt`: "Informe a quantidade que deseja alterar").
4. Preenche a observação e clica em **Processar**.
5. Confere a mensagem devolvida pelo sistema
   (`assert`: "Ajuste realizado com sucesso").

### Limitação da ferramenta e contorno

A versão para Chrome do Selenium IDE foi descontinuada na Chrome Web Store, por
isso foi usada a versão para Firefox. Na primeira execução, todos os comandos
que digitam (`type` e `send keys`) falharam com o erro
`n.createEvent(...).initKeyEvent is not a function`: o Selenium IDE 3.17.4
simula o teclado com uma função que o Firefox atual não tem mais. Pelo mesmo
motivo, a ferramenta não conseguia capturar as janelas de confirmação e de
mensagem (`confirm`/`alert`) e a execução travava esperando por elas.

Contornos aplicados, sem alterar o sistema:

- os campos de login e de observação são preenchidos com `execute script`
  (`document.getElementById(...).value = ...`) em vez de `type`;
- antes de clicar em **Processar**, um `execute script` responde OK à
  confirmação e guarda a mensagem final em `window.ultimoAlerta`, em vez de
  exibi-la; depois de 2 segundos (`pause`), a mensagem é lida para a variável
  `mensagem` e conferida com `assert`.

### Observação sobre o resultado

O teste passa porque confere o que a interface **informa** ("Ajuste realizado
com sucesso"). O saldo do produto **não** é conferido neste teste: como mostram
o caso manual CT-AJU-01 e o teste de integração `AjusteServiceIT`, o saldo não
aumenta depois de uma entrada (defeito
[#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14)), e esse
defeito já está documentado nesses dois testes.

### Como reproduzir

1. Subir o banco e a aplicação (ver
   [testes de integração](../../src/test/java/net/originmobi/pdv/AjusteServiceIT.java)
   e o README da raiz) e acessar <http://localhost:8080>.
2. No Firefox, instalar a extensão
   [Selenium IDE](https://addons.mozilla.org/pt-BR/firefox/addon/selenium-ide/).
3. Abrir o Selenium IDE → **Open an existing project** →
   `src/test/selenium/AjusteEstoque.side`.
4. Selecionar o teste `CT-AJU-01 processar ajuste de entrada` e clicar em
   **Run current test**.