# Registro de Uso de Inteligência Artificial

Este arquivo registra o uso de ferramentas de inteligência artificial no trabalho, as decisões tomadas pela equipe e as formas de validação aplicadas. As respostas da IA foram tratadas como apoio: o código, os comandos, os resultados e as alterações no repositório foram revisados pela equipe antes da integração.

## Interação 001 — Seleção do projeto

- **Data:** 19/09/2026
- **Responsável:** Pedro Campos Camargos
- **Ferramenta:** ChatGPT
- **Atividade:** Análise comparativa dos repositórios disponibilizados pela professora.
- **Prompt resumido:** Analisar os repositórios da organização `repo-software-testing-courses` e indicar o projeto mais adequado aos requisitos do trabalho.
- **Resultado obtido:** O PDV foi recomendado por ser uma aplicação web Java com regras de negócio, interface adequada a testes funcionais e classes candidatas para testes unitários.
- **Decisão da equipe:** Selecionar o PDV como sistema sob teste e criar o fork `cmrgsPedro/pdv-qa`.
- **Validação:** A equipe confirmou no código-fonte e na execução local que a aplicação possuía as tecnologias e funcionalidades identificadas.

## Interação 002 — Avaliação e divisão das classes

- **Data:** 19/09/2026
- **Responsável:** Arthur Jardim Antunes Ferreira
- **Ferramenta:** ChatGPT
- **Atividade:** Identificação de classes não CRUD com regras de negócio.
- **Prompt resumido:** Identificar quatro classes adequadas para a divisão do trabalho entre os integrantes.
- **Resultado obtido:** Foram sugeridas `VendaService`, `CaixaService`, `RecebimentoService` e `NotaFiscalItemService`.
- **Decisão da equipe:** Dividir as classes entre Pedro, Arthur, Rodrigo e João Paulo, respectivamente.
- **Validação:** O grupo inspecionou os métodos e as dependências das quatro classes e confirmou a existência de validações, decisões e efeitos colaterais relevantes. A medição formal de complexidade ciclomática ainda deverá ser registrada quando executada.

## Interação 003 — Preparação do ambiente Docker

- **Data:** 20/09/2026
- **Responsável:** Pedro Campos Camargos
- **Ferramenta:** ChatGPT
- **Atividade:** Diagnóstico de falha na construção da imagem da aplicação.
- **Prompt resumido:** Analisar o erro `openjdk:8-jdk-slim: not found` apresentado por `docker compose up --build`.
- **Resultado obtido:** Foi identificada a indisponibilidade da imagem-base antiga e recomendada uma imagem Maven com Eclipse Temurin 8.
- **Decisão da equipe:** Atualizar somente o ambiente de construção, preservando Java 8 e o código de produção.
- **Validação:** A imagem foi construída, os contêineres iniciaram e o sistema foi acessado localmente em `http://localhost:8080`. A correção foi integrada pelo PR `#1`.

## Interação 004 — Planejamento inicial da Entrega 1

- **Data:** 20/09/2026
- **Responsável:** Arthur Jardim Antunes Ferreira
- **Ferramenta:** ChatGPT
- **Atividade:** Organização dos artefatos prioritários para a primeira entrega.
- **Prompt resumido:** Separar os requisitos mínimos da Entrega 1 das atividades previstas para etapas posteriores.
- **Resultado obtido:** Foram priorizados o Plano de Testes, a divisão das classes e funcionalidades, os casos de teste, o registro de IA e a apresentação.
- **Decisão da equipe:** Preparar inicialmente a documentação e, após consultar a experiência de uma turma anterior, incluir também a implementação dos testes unitários em JUnit e Mockito.
- **Validação:** A estrutura inicial da entrega foi revisada e integrada pelo PR `#2`.

## Interação 005 — Análise do `pom.xml` e escolha das tecnologias de teste

- **Data:** 20/09/2026
- **Responsável:** Arthur Jardim Antunes Ferreira, Pedro Campos Camargos
- **Ferramenta:** ChatGPT
- **Atividade:** Verificação das versões e dependências adequadas para testes unitários.
- **Prompt resumido:** Analisar principalmente o `pom.xml` e recomendar tecnologias compatíveis com o projeto, sem criar testes de integração.
- **Resultado obtido:** Foram mantidos Java 8, JUnit 4 e `spring-boot-starter-test`. Foi identificada incompatibilidade do Mockito transitivo antigo com JDKs atuais.
- **Decisão da equipe:** Fixar `mockito.version` em `3.12.4` e `byte-buddy.version` em `1.11.13`, sem iniciar o contexto Spring e sem acessar banco de dados.
- **Validação:** A alteração foi revisada e integrada à `master` pelo PR `#3` (`build: atualiza dependencias dos testes unitarios`).

## Interação 006 — Geração das suítes unitárias

- **Data:** 20/09/2026
- **Responsável:** Pedro Campos Camargos, Arthur Jardim Antunes Ferreira, Rodrigo Lima de Albuquerque e João Paulo Alves da Silveira
- **Ferramenta:** ChatGPT, Claude Code
- **Atividade:** Elaboração de testes unitários isolados para as quatro classes selecionadas.
- **Prompt resumido:** Criar testes somente unitários, utilizando JUnit 4 e Mockito, cobrindo os comportamentos das classes sem alterar o código de produção.
- **Resultado obtido:** Foram preparados 88 métodos de teste: 28 para `VendaService`, 22 para `CaixaService`, 21 para `RecebimentoService` e 17 para `NotaFiscalItemService`.
- **Decisão da equipe:** Manter uma suíte por integrante e simular repositórios e serviços externos com Mockito. O teste existente `PdvApplicationTests`, baseado em `@SpringBootTest`, não foi considerado parte da suíte unitária isolada.
- **Validação:** Os arquivos foram conferidos contra as assinaturas reais das classes e executados no ambiente de preparação antes da distribuição à equipe.

## Interação 007 — Cobertura e limitações estruturais

- **Data:** 20/09/2026
- **Responsável:** João Paulo Alves da Silveira
- **Ferramenta:** Claude Code
- **Atividade:** Execução das suítes e análise do relatório JaCoCo.
- **Prompt resumido:** Executar os testes, medir a cobertura das quatro classes e explicar qualquer trecho que não pudesse ser exercitado unitariamente.
- **Resultado obtido:** Na validação de preparação, os 88 testes foram aprovados, com 100% de linhas, instruções e métodos nas quatro classes selecionadas.
- **Limitações identificadas:** Permaneceram duas decisões de branch estruturalmente inalcançáveis: uma condição redundante em `CaixaService`, limitada pelos valores do enum, e a verificação `codtitulo == null` em `RecebimentoService`, precedida pelo desempacotamento do `Long`.
- **Decisão da equipe:** Não modificar o código de produção apenas para aumentar a métrica de branches; registrar tecnicamente as limitações.
- **Validação:** As contagens de testes foram novamente confirmadas nos quatro arquivos presentes na `master`. A equipe deve conservar a saída da execução integrada final e o relatório JaCoCo gerado na versão entregue.

## Interação 008 — Diagnóstico da execução de `RecebimentoServiceTest`

- **Data:** 20/09/2026
- **Responsável:** Rodrigo Lima de Albuquerque
- **Ferramenta:** ChatGPT
- **Atividade:** Investigação de `NoClassDefFoundError: LPessoaService;` durante a execução do teste.
- **Prompt resumido:** Analisar uma execução em que apareciam duas classes chamadas `RecebimentoServiceTest`, uma delas no pacote incorreto `service.notafiscal`.
- **Resultado obtido:** A classe correta executou 21 testes sem falhas; a falha veio de um `.class` antigo existente em `target/`. A busca no código-fonte confirmou a existência de apenas um `RecebimentoServiceTest.java`.
- **Decisão da equipe:** Executar Maven com `clean` antes do teste e manter somente `src/test/java/net/originmobi/pdv/service/RecebimentoServiceTest.java`.
- **Validação:** O arquivo correto foi integrado pelo PR `#7`. Também foi orientado colocar o parâmetro `-Dtest` entre aspas no PowerShell quando houver uma lista separada por vírgulas.

## Limites e responsabilidade da equipe

- A IA auxiliou na análise, geração inicial, revisão e diagnóstico, mas não substituiu a conferência do código-fonte nem a execução dos testes.
- Os testes gerados foram revisados e enviados em Pull Requests separados, preservando a responsabilidade de cada integrante.
- Métricas obtidas antes dos merges devem ser reproduzidas na `master` final antes de serem apresentadas como evidência definitiva.
- Alterações no código de produção não foram realizadas apenas para satisfazer métricas de cobertura.
