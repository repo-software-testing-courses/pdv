# PDV — Sistema de ERP web

Sistema de ERP/ponto de venda desenvolvido em Java com Spring Boot, usado como
objeto de estudo no trabalho prático da disciplina **Qualidade e Teste**.

Este repositório é um fork do projeto original
[originmobi/pdv](https://github.com/originmobi/pdv), acrescido dos artefatos de
teste e qualidade produzidos pelo grupo.

## Equipe

| Integrante | GitHub | Responsabilidades | Classe sob teste |
| --- | --- | --- | --- |
| Eloyse Fernanda | [@elojas2](https://github.com/elojas2) | Testes unitários e manuais | `CaixaService` |
| Natalia de Abreu Lamas | [@natalialamas73](https://github.com/natalialamas73) | Testes unitários e manuais | `AjusteService` |
| Alexandre Porto | [@AlexPortoNascimento](https://github.com/AlexPortoNascimento) | Testes unitários e manuais | `VendaService` |

## Entregas

| Entrega | Peso | Prazo | Status |
| --- | --- | --- | --- |
| Entrega 1 | 3 | 23/09/2026 | concluída |
| Entrega 2 | 5 | 11/11/2026 | em andamento |

## Entrega 2 — artefatos por requisito

| Requisito | CaixaService (Eloyse) | VendaService (Alexandre) | AjusteService (Natalia) |
| --- | --- | --- | --- |
| Testes unitários com dependências isoladas | [`CaixaServiceTest`](src/test/java/net/originmobi/pdv/CaixaServiceTest.java) — 43 casos (Mockito, `mockStatic`, `mockConstruction`) | [`VendaServiceTest`](src/test/java/net/originmobi/pdv/VendaServiceTest.java) — 43 casos (Mockito, `mockStatic`, `ArgumentCaptor`; 2 defeitos: [#7](https://github.com/elojas2/trabalho-pdv-2026.1/issues/7), [#8](https://github.com/elojas2/trabalho-pdv-2026.1/issues/8)) | [`AjusteServiceTest`](src/test/java/net/originmobi/pdv/AjusteServiceTest.java) — 19 casos (Mockito, `mockStatic`; 1 defeito: [#28](https://github.com/elojas2/trabalho-pdv-2026.1/issues/28)) |
| Testes de integração | [`CaixaServiceIT`](src/test/java/net/originmobi/pdv/CaixaServiceIT.java) — 12 casos, MySQL real (2 defeitos: [#24](https://github.com/elojas2/trabalho-pdv-2026.1/issues/24), [#25](https://github.com/elojas2/trabalho-pdv-2026.1/issues/25)) | [`VendaServiceIT`](src/test/java/net/originmobi/pdv/VendaServiceIT.java) — 4 casos, MySQL real (inclui o trigger do total da venda) | [`AjusteServiceIT`](src/test/java/net/originmobi/pdv/AjusteServiceIT.java) — 7 casos, MySQL real (2 defeitos: [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14), [#27](https://github.com/elojas2/trabalho-pdv-2026.1/issues/27)) |
| Estrutural — todas-arestas ≥ 80% (JaCoCo) | **100%** (38/38) — [relatório](docs/relatorios/cobertura-jacoco/) | **89,5%** (34/38) — [relatório](docs/relatorios/cobertura-jacoco/) | **100%** (10/10) — [relatório](docs/relatorios/cobertura-jacoco/) |
| Baseada em defeitos — mutação ≥ 80% (PIT) | **100%** (55/55) — [relatório](docs/relatorios/mutacao-pit/) | **98%** (62/63) — [relatório](docs/relatorios/mutacao-pit/) | **100%** (17/17) — [relatório](docs/relatorios/mutacao-pit/) |
| Funcional — partições de equivalência e valores-limite | [Google Docs](https://docs.google.com/document/d/1seAIKsU1c7LtvKjygsobeBPUuppfPHea4utl7ZjaEUY/edit?usp=sharing) (documento do grupo) | ← | ← |
| Inspeção de código (Sonar) — antes e depois | **21 → 1** problemas — [relatório](docs/relatorios/inspecao-sonar/) | **26 → 3** problemas  — [relatório](docs/relatorios/inspecao-sonar/) | **13 → 1** problemas — [relatório](docs/relatorios/inspecao-sonar/) |
| Testes de sistema (Selenium) | [`CaixaSeleniumIT`](src/test/java/net/originmobi/pdv/sistema/CaixaSeleniumIT.java) — 8 cenários (1 expõe o defeito [#24](https://github.com/elojas2/trabalho-pdv-2026.1/issues/24)) | [`VendaSeleniumIT`](src/test/java/net/originmobi/pdv/sistema/VendaSeleniumIT.java) — 8 cenários (3 expõem os defeitos [#12](https://github.com/elojas2/trabalho-pdv-2026.1/issues/12), [#13](https://github.com/elojas2/trabalho-pdv-2026.1/issues/13) e [DEF-VENDA-004](docs/defeitos/DEF-VENDA-004-venda-a-prazo-nao-fecha.md)) — [relatório](docs/relatorios/testes-sistema-selenium.md) | [`AjusteSeleniumIT`](src/test/java/net/originmobi/pdv/sistema/AjusteSeleniumIT.java) — 6 cenários (2 expõem os defeitos [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14) e [#27](https://github.com/elojas2/trabalho-pdv-2026.1/issues/27)) e [`AjusteEstoque.side`](src/test/selenium/AjusteEstoque.side) — Selenium IDE, complementar — [relatório](docs/relatorios/testes-sistema-selenium.md) |
| Requisito não funcional (segurança e desempenho) — Postman | [`Caixa.postman_collection.json`](src/test/postman/Caixa.postman_collection.json) — 14 requisições, 40 verificações (1 expõe o defeito [#15](https://github.com/elojas2/trabalho-pdv-2026.1/issues/15)) — [prints](docs/relatorios/teste-postman/) | pendente | [`Ajuste.postman_collection.json`](src/test/postman/Ajuste.postman_collection.json) — 18 requisições, 50 verificações (1 expõe o defeito [#28](https://github.com/elojas2/trabalho-pdv-2026.1/issues/28)) — [prints](docs/relatorios/teste-postman/) |
| ISO/IEC 25010 | [Google Docs](https://docs.google.com/document/d/1bvbjeAyXI9Dq8uSdElpbEgbwPHDD9hZkLoyM-19vwG8/edit?usp=sharing) (documento do grupo) | ← | ← |
| Registro de uso de IA | [AI-012, AI-013, AI-016, AI-017, AI-021, AI-023](docs/ai/AI-LOG.md) | [AI-014, AI-015, AI-024, AI-025](docs/ai/AI-LOG.md) | [AI-011, AI-018, AI-019, AI-020, AI-022](docs/ai/AI-LOG.md) |

## Sumário

- [Equipe](#equipe)
- [Entregas](#entregas)
- [Entrega 2 — artefatos por requisito](#entrega-2--artefatos-por-requisito)
- [Documentos editáveis (Google Docs)](#documentos-editáveis-google-docs)
- [Artefatos no repositório](#artefatos-no-repositório)
- [Defeitos registrados](#defeitos-registrados)
- [Recursos do sistema](#recursos-do-sistema)
- [Tecnologias](#tecnologias)
- [Executando com Docker (recomendado)](#executando-com-docker-recomendado)
- [Executando localmente](#executando-localmente)
- [Acesso ao sistema](#acesso-ao-sistema)
- [Testes e métricas](#testes-e-métricas)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Licença](#licença)

## Documentos editáveis (Google Docs)

Os documentos de texto são elaborados no Google Docs, com todos os integrantes
logados, para que a colaboração individual fique registrada no histórico de
versões.

| Documento | Link |
| --- | --- |
| Plano de Teste | [Google Docs](https://docs.google.com/document/d/1b40d0Y7q8RmNOTqsJ-dV7mlSIBAJlqY2Q8F_PGl12M0/edit?usp=sharing) |
| Casos de Teste | [Google Docs](https://docs.google.com/document/d/1cpsUYGmLNs6N2mYzDVKepaeZyxAlKNn6bANoqa1tXVU/edit?usp=sharing) |
| Teste funcional (partições e valores-limite) | [Google Docs](https://docs.google.com/document/d/1seAIKsU1c7LtvKjygsobeBPUuppfPHea4utl7ZjaEUY/edit?usp=sharing) |
| Importância das características de qualidade (ISO/IEC 25010) | [Google Docs](https://docs.google.com/document/d/1bvbjeAyXI9Dq8uSdElpbEgbwPHDD9hZkLoyM-19vwG8/edit?usp=sharing) |
| Relatório de inspeção de código | [`docs/relatorios/inspecao-sonar/`](docs/relatorios/inspecao-sonar/) (no repositório) |
| Apresentação parcial (Entrega 1) | [Google Slides](https://docs.google.com/presentation/d/1Vy5m1Brl0lf9eM_LRIxNzgr0ZdlR_FlJAifwIouKT-4/edit?usp=sharing) |

## Artefatos no repositório

Todos os artefatos estão na branch principal (`master`), sob o diretório
[`docs/`](docs/).

| Artefato | Local |
| --- | --- |
| Plano de Teste (cópia versionada) | [`docs/plano-de-teste.md`](docs/plano-de-teste.md) |
| Casos de Teste (exportação do TestLink) | [`docs/casos-de-teste-testlink.pdf`](docs/casos-de-teste-testlink.pdf) |
| Código dos testes automatizados | [`src/test/java/`](src/test/java/) |
| Testes de integração — `VendaService` (banco real + triggers) | [`src/test/java/net/originmobi/pdv/VendaServiceIT.java`](src/test/java/net/originmobi/pdv/VendaServiceIT.java) |
| Testes de integração — `CaixaService` (banco real + trigger) | [`src/test/java/net/originmobi/pdv/CaixaServiceIT.java`](src/test/java/net/originmobi/pdv/CaixaServiceIT.java) |
| Testes de integração — `AjusteService` (banco real + trigger de estoque) | [`src/test/java/net/originmobi/pdv/AjusteServiceIT.java`](src/test/java/net/originmobi/pdv/AjusteServiceIT.java) |
| Cobertura estrutural (JaCoCo) — `CaixaService`, `VendaService` e `AjusteService` | [`docs/relatorios/cobertura-jacoco/`](docs/relatorios/cobertura-jacoco/) |
| Teste de mutação (PIT) — `CaixaService`, `VendaService` e `AjusteService` | [`docs/relatorios/mutacao-pit/`](docs/relatorios/mutacao-pit/) |
| Inspeção de código (SonarQube) — `CaixaService` e `AjusteService` | [`docs/relatorios/inspecao-sonar/`](docs/relatorios/inspecao-sonar/) |
| Relatório de testes manuais — `VendaService` | [`docs/relatorios/testes-manuais-vendaservice.md`](docs/relatorios/testes-manuais-vendaservice.md) |
| Relatório de testes manuais — `AjusteService` | [`docs/relatorios/testes-manuais-ajusteservice.md`](docs/relatorios/testes-manuais-ajusteservice.md) |
| Testes de sistema (Selenium) — `CaixaService` | [`src/test/java/net/originmobi/pdv/sistema/CaixaSeleniumIT.java`](src/test/java/net/originmobi/pdv/sistema/CaixaSeleniumIT.java) |
| Testes de sistema (Selenium) — `VendaService` | [`src/test/java/net/originmobi/pdv/sistema/VendaSeleniumIT.java`](src/test/java/net/originmobi/pdv/sistema/VendaSeleniumIT.java) e [`docs/relatorios/testes-sistema-selenium.md`](docs/relatorios/testes-sistema-selenium.md) |
| Testes de sistema (Selenium) — `AjusteService` | [`src/test/java/net/originmobi/pdv/sistema/AjusteSeleniumIT.java`](src/test/java/net/originmobi/pdv/sistema/AjusteSeleniumIT.java) e [`docs/relatorios/testes-sistema-selenium.md`](docs/relatorios/testes-sistema-selenium.md) (teste complementar em Selenium IDE: [`src/test/selenium/`](src/test/selenium/)) |
| Testes de sistema e não funcionais (Postman) — `CaixaService` | [`src/test/postman/Caixa.postman_collection.json`](src/test/postman/Caixa.postman_collection.json) e prints em [`docs/relatorios/teste-postman/`](docs/relatorios/teste-postman/). Rodar pelo Runner do Postman (*Import* → *Run collection*) ou `npx newman run src/test/postman/Caixa.postman_collection.json`, com o sistema no ar |
| Testes de sistema e não funcionais (Postman) — `AjusteService` | [`src/test/postman/Ajuste.postman_collection.json`](src/test/postman/Ajuste.postman_collection.json) e prints em [`docs/relatorios/teste-postman/`](docs/relatorios/teste-postman/). Rodar pelo Runner do Postman (*Import* → *Run collection*) ou `npx newman run src/test/postman/Ajuste.postman_collection.json`, com o sistema no ar |
| Fichas de defeitos | [`docs/defeitos/`](docs/defeitos/) |
| Evidências (prints dos testes manuais) | [`docs/evidencias/`](docs/evidencias/) |
| **Registro de uso de IA** | [`docs/ai/AI-LOG.md`](docs/ai/AI-LOG.md) |
| Transcrições e versões iniciais (v0) geradas por IA | [`docs/ai/transcricoes/`](docs/ai/transcricoes/) |
| Slides das apresentações | [`docs/apresentacoes/`](docs/apresentacoes/) |

## Defeitos registrados

O rastreador oficial é o [GitHub Issues](https://github.com/elojas2/trabalho-pdv-2026.1/issues);
as fichas documentam a análise e as evidências.

| Defeito | Módulo | Título | Ficha | Issue |
| --- | --- | --- | --- | --- |
| DEF-VENDA-001 | Venda | Fechamento sem título lança "Zero length string" | [ficha](docs/defeitos/DEF-VENDA-001-zero-length-string.md) | [#11](https://github.com/elojas2/trabalho-pdv-2026.1/issues/11) |
| DEF-VENDA-002 | Venda | Cartão débito/crédito não fecha a venda (NPE) | [ficha](docs/defeitos/DEF-VENDA-002-cartao-nao-fecha.md) | [#12](https://github.com/elojas2/trabalho-pdv-2026.1/issues/12) |
| DEF-VENDA-003 | Venda | Desconto e acréscimo aplicados trocados (o lançamento no caixa sai com valor errado) | [ficha](docs/defeitos/DEF-VENDA-003-desconto-acrescimo-trocados.md) | [#13](https://github.com/elojas2/trabalho-pdv-2026.1/issues/13) |
| DEF-VENDA-004 | Venda | Venda a prazo não fecha (`insert` da parcela sem `data_alteracao`) | [ficha](docs/defeitos/DEF-VENDA-004-venda-a-prazo-nao-fecha.md) | a abrir |
| DEF-CAIXA-001 | Caixa | Fechar caixa já fechado exibe alerta vazio | [ficha](docs/defeitos/DEF-CAIXA-001-fechamento-duplicado-mensagem-vazia.md) | [#15](https://github.com/elojas2/trabalho-pdv-2026.1/issues/15) |
| DEF-ESTOQUE-001 | Estoque / Ajuste | Entrada de estoque não aumenta o saldo (trigger só subtrai) | [ficha](docs/defeitos/DEF-ESTOQUE-001-entrada-nao-aumenta-saldo.md) | [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14) |
| DEF-AJUSTE-001 | Estoque / Ajuste | Ajuste de saída aumenta o saldo do produto | [ficha](docs/defeitos/DEF-AJUSTE-001-saida-aumenta-saldo.md) | [#27](https://github.com/elojas2/trabalho-pdv-2026.1/issues/27) |
| DEF-AJUSTE-002 | Ajuste | Processar ajuste inexistente lança "No value present" sem mensagem tratada | [ficha](docs/defeitos/DEF-AJUSTE-002-ajuste-inexistente-sem-mensagem.md) | [#28](https://github.com/elojas2/trabalho-pdv-2026.1/issues/28) |