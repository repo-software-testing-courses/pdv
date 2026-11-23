# DEF-AJUSTE-002 — Processar ajuste inexistente lança erro técnico sem mensagem tratada

| Campo | Valor |
| --- | --- |
| ID | DEF-AJUSTE-002 |
| Módulo | Estoque / Ajuste |
| Origem | `AjusteService.processar` |
| Encontrado por | Natalia de Abreu Lamas (teste unitário `AjusteServiceTest` e Postman) |
| Data | 21/11/2026 |
| Severidade | Média (não corrompe dados, mas o usuário recebe uma mensagem técnica) |
| Status | Aberto (não corrigido) |
| Issue | [#28](https://github.com/elojas2/trabalho-pdv-2026.1/issues/28) |

## Passos para reproduzir

1. Com o sistema no ar e logado, enviar `POST /ajustes/processar` com
   `codajuste=999999` (código que não existe).

## Resultado obtido

Erro 500 com a mensagem técnica **"No value present"**
(`NoSuchElementException`).

## Resultado esperado

Uma mensagem tratada para o usuário, por exemplo `AjusteException`
("Ajuste não encontrado"), como já acontece nas outras falhas do método.

## Causa raiz

Em `AjusteService.processar`, o `Optional` retornado por `findById` é lido com
`.get()` sem verificar antes se o ajuste existe:

```java
if (ajuste.map(Ajuste::getStatus).get().equals(AjusteStatus.PROCESSADO))
```

## Evidências

- Teste unitário: `AjusteServiceTest#processar_CodigoInexistente_DeveLancarErroTratado` (`@Tag("defeito")`)
- Teste de sistema (Postman): requisição `[defeito #28]` da coleção
  [`Ajuste.postman_collection.json`](../../src/test/postman/Ajuste.postman_collection.json)
  ([print](../relatorios/teste-postman/ajuste-defeito-28.png))

## Correção sugerida

```java
Ajuste ajuste = ajustes.findById(codajuste)
        .orElseThrow(() -> new AjusteException("Ajuste não encontrado"));
```