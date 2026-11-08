# Registro de uso de Inteligência Artificial

Este arquivo registra as interações com ferramentas de IA Generativa que
contribuíram **substancialmente** para os artefatos do trabalho, conforme
exigido no enunciado.

Não são registradas interações usadas exclusivamente para correções
ortográficas, configuração de IDE e afins.

## Como registrar

Cada uso relevante gera uma seção nova, em ordem cronológica, com os campos:

| Campo | Descrição |
| --- | --- |
| Responsável | integrante que realizou a interação |
| Atividade | atividade do trabalho associada |
| Ferramenta | ChatGPT, Claude, Copilot etc. |
| Prompt/instrução | prompt ou instrução utilizada |
| Resultado | breve descrição da resposta da IA |
| Decisão | o que foi aceito, alterado ou rejeitado |
| Validação | como o resultado foi verificado |

Quando viável, a conversa completa, os prompts e as configurações são salvos em
[`transcricoes/`](transcricoes/) e referenciados na entrada.

Para testes gerados ou melhorados com auxílio de IA, preserve também:

1. a solução inicialmente produzida pela IA (ex.: `transcricoes/<id>-v0-<Classe>Test.java`);
2. a solução final, após revisão, no diretório de testes do projeto;
3. a descrição das alterações feitas na revisão.

> O simples uso de IA não é mérito. O que é avaliado é a capacidade de verificar
> a resposta, identificar erros e casos ausentes, melhorar a solução e
> apresentar evidências da qualidade final.

---

## AI-001 — Estruturação do Plano de Teste e seleção da classe sob teste do módulo Caixa

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-19 |
| **Responsável** | Eloyse Fernanda |
| **Atividade** | Entrega 1 — elaboração do Plano de Teste e definição do escopo |
| **Ferramenta** | Claude (Claude Code — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | Solicitação de elaboração do Plano de Teste da Entrega 1, a partir da leitura do modelo `TemplatePlanoDeTeste.doc`, contemplando a definição do escopo, a seleção da classe sob teste do módulo Caixa. |
| **Resultado** | A IA leu o enunciado do template da disciplina (`TemplatePlanoDeTeste.doc`), produzindo: (a) mapeamento entre a estrutura anterior do `plano-de-teste.md` e a estrutura do template; (b) reescrita completa do `docs/plano-de-teste.md` na estrutura do template. |
| **Decisão** | **Aceito:** a estrutura de seções conforme o template; o conteúdo das seções 1.2, 2.1, 2.3, 2.4, 2.5, 3, 4.1, 4.2 e 5. **Alterado:** os exemplos genéricos do template original (Windows 8, MS Exchange, Office 2013) foram substituídos pelo ambiente real do projeto (JDK 8, MySQL 8, Docker Compose); o conteúdo sobre riscos, que não tem seção própria no template, foi incorporado à seção 2.4. **Deixado em aberto:** a divisão de classes dos demais integrantes e as datas do cronograma (seção 2.6). |
| **Validação** | O conteúdo do template `.doc` foi extraído e conferido integralmente antes da redação, garantindo que todas as 5 seções e 12 subseções do modelo da disciplina estivessem presentes. A estrutura resultante foi conferida seção a seção contra o sumário do template. |
| **Transcrição** | Não preservada |

---

## AI-002 — Geração dos testes unitários de `CaixaService` com JUnit + Mockito

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-19 |
| **Responsável** | Eloyse Fernanda |
| **Atividade** | Entrega 1 — implementação dos testes unitários da classe sob teste do módulo Caixa |
| **Ferramenta** | Claude (Claude Code — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | "Crie testes unitários para o CaixaService com junit + mockito" |
| **Resultado** | A IA leu `CaixaService`, `CaixaRepository`, `Caixa`, `Usuario`, o singleton `Aplicacao`, os filtros `CaixaFilter`/`BancoFilter` e o `pom.xml` para identificar as versões disponíveis (JUnit 4 e Mockito 2, via `spring-boot-starter-test` 2.0.2). Gerou `src/test/java/net/originmobi/pdv/CaixaServiceTest.java` com 28 casos de teste usando `@RunWith(MockitoJUnitRunner.Silent.class)`, `@Mock` para `CaixaRepository`, `UsuarioService` e `CaixaLancamentoService`, `@InjectMocks` no serviço, `@Captor` para inspecionar o `Caixa` e o `CaixaLancamento` enviados às dependências e `ExpectedException` para as regras de negócio que lançam erro. Cobertura: `cadastro` (9 casos), `fechaCaixa` (5 casos) e os 13 métodos de consulta. |
| **Decisão** | **Aceito:** o arquivo de teste na íntegra — passou na primeira execução, sem necessidade de revisão corretiva. **Alterado:** nada *nesta interação*; a suíte foi posteriormente revisada e reescrita em [AI-003](#ai-003--revisão-crítica-da-suíte-migração-para-junit-5-e-isolamento-das-dependências), de modo que a solução inicial da IA está preservada em [`transcricoes/AI-002-v0-CaixaServiceTest.java`](transcricoes/AI-002-v0-CaixaServiceTest.java) e **não** corresponde mais à versão em `src/test/java/`. **Observações registradas para as próximas entregas:** (a) o singleton `Aplicacao` só lê o usuário do `SecurityContextHolder` na primeira construção e mantém a instância em cache estático pela JVM inteira — o `@Before` popula o contexto antes de qualquer teste, mas um futuro teste que precise de outro usuário na mesma JVM esbarrará nesse cache; (b) `MockitoJUnitRunner.Silent` foi usado porque vários casos compartilham o stub de `usuarios.buscaUsuario` no `@Before`, que nem todos exercitam. |
| **Validação** | Execução real da suíte com `JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn test -Dtest=CaixaServiceTest`: **Tests run: 28, Failures: 0, Errors: 0, Skipped: 0** (relatório em `target/surefire-reports/net.originmobi.pdv.CaixaServiceTest.txt`). As mensagens de erro e as descrições padrão (`Caixa diário`, `Cofre`, `Banco`) asseridas nos testes foram conferidas caractere a caractere contra o código-fonte de `CaixaService`. Notou-se durante a execução que o projeto não compila com o JDK 26 (padrão da máquina) por ser `source 1.8`, e que o `./mvnw` do repositório está quebrado (sem permissão de execução e sem o jar do wrapper). |
| **Transcrição** | Conversa completa em [`transcricoes/AI-002-testes-caixaservice.md`](transcricoes/AI-002-testes-caixaservice.md). **Solução inicial da IA (v0, 28 casos)** preservada em [`transcricoes/AI-002-v0-CaixaServiceTest.java`](transcricoes/AI-002-v0-CaixaServiceTest.java), extraída do commit `872d528`. A solução final, após a revisão descrita em AI-003, está em `src/test/java/net/originmobi/pdv/CaixaServiceTest.java`. |

---

## AI-003 — Revisão crítica da suíte, migração para JUnit 5 e isolamento das dependências


| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-20 |
| **Responsável** | a revisão e os pontos de melhoria foram identificados por Eloyse Fernanda; |
| **Atividade** | Entrega 1 — "melhorar e aumentar os casos de testes unitários, isolando suas dependências" |
| **Ferramenta** | Claude (Claude Code — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | Sequência de instruções da integrante, **partindo de pontos que ela identificou lendo a suíte v0**: (1) "atualize o junit4 para 5 mantendo os casos de testes existentes e depois troque o runwith mockitojunitrunner por extendwith mockito, faça o uso de asserts tbm. Não use o mockito silent.class nem lenient. além disso, reavalie os testes"; (2) "use o mockedstatic ao invés desse securitycontext". Transcrição completa em `transcricoes/`. |
| **Resultado** | A IA migrou o `pom.xml` (JUnit 5, `mockito-junit-jupiter`, `mockito-inline`, Surefire 2.22.2) e reescreveu `CaixaServiceTest`: `@ExtendWith(MockitoExtension.class)`, `assertThrows` no lugar de `ExpectedException`, asserções mais específicas (`assertSame`, `assertNotNull`, `assertNull`), verificação dos ramos não tomados (`never()`), `verifyNoInteractions` nos caminhos de falha rápida e `mockStatic(Aplicacao.class)` no lugar do `SecurityContextHolder`. A suíte passou de 28 para 29 casos. |
| **Decisão** | **Aceito:** a migração para JUnit 5, a troca do `ExpectedException` por `assertThrows` com conferência da mensagem, o reforço das asserções e o `mockStatic`. **Rejeitado:** a organização em classes `@Nested` proposta pela IA — optou-se por manter a suíte plana, com seções em comentário e prefixo no nome do método. **Alterado pela integrante, sobre a proposta inicial da IA:** o arranjo do usuário, que a IA havia deixado num `@BeforeEach` global, foi movido para helpers chamados explicitamente apenas pelos testes que dependem dele. |
| **Validação** | Execução real da suíte: `JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -Dtest=CaixaServiceTest test` → **Tests run: 29, Failures: 0, Errors: 0, Skipped: 0**. Durante a migração dois defeitos reais foram flagrados pela execução e corrigidos: (a) `MockitoException: Mockito cannot mock this class`, causado pela versão antiga de Byte Buddy herdada do Spring Boot 2.0 — resolvido fixando `byte-buddy.version` em 1.11.13; (b) uma asserção incorreta sobre `CaixaLancamento.getCaixa()`, que devolve `Optional<Caixa>` e não `Caixa`. «anexar print / trecho do relatório do surefire como evidência» |
| **Transcrição** | comparação v0 × final: [`transcricoes/AI-002-v0-CaixaServiceTest.java`](transcricoes/AI-002-v0-CaixaServiceTest.java) × `src/test/java/net/originmobi/pdv/CaixaServiceTest.java`; descrição das alterações em [`transcricoes/AI-003-alteracoes-v0-para-final.md`](transcricoes/AI-003-alteracoes-v0-para-final.md). |

---

## AI-004 — Seleção da classe sob teste e planejamento dos casos de teste unitários de `VendaService`

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-22 |
| **Responsável** | Alexandre Porto |
| **Atividade** | Entrega 1 — seleção da classe sob teste do módulo Venda e planejamento dos casos de teste unitários, dos testes manuais e das modificações no Plano de Teste |
| **Ferramenta** | Kiro CLI (Claude Sonnet 4.6 — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | "Leia o arquivo Trabalho - Qualidade e Teste.pdf e guarde em seu contexto, ele é a fonte da verdade sobre tudo que precisará ser feito. [...] Faça um planejamento para realizar a minha parte do trabalho (Alexandre). Só deve ser considerado a entrega 1 no momento. Procure uma classe para criar os casos de testes que seja diferente do caso feito pela Eloyse. Ela fez o CaixaServiceTest. Me explique pq essa classe escolhida atende todos os requisitos apresentados no documento para ela ser escolhida. Vale ressaltar que ela deve ter uma complexidade ciclomática de pelo menos 10. Inclua no plano todas as modificações que precisam ser feitas no docs/plano-de-teste.md." |
| **Resultado** | A IA leu o enunciado (PDF indexado via knowledge base), todos os services do projeto (`VendaService`, `RecebimentoService`, `PagarService`, `CartaoLancamentoService`, `ProdutoService`, `AjusteService`), a estrutura de testes existente e o `plano-de-teste.md`. Produziu: (a) justificativa detalhada da escolha de `VendaService` como classe sob teste (não é CRUD, complexidade ciclomática estimada ≥ 20 com ~32 pontos de decisão, domínio diferente de `CaixaService`); (b) plano de 10 tasks cobrindo implementação dos testes unitários (≥ 18 casos sobre todos os métodos públicos), testes manuais do módulo Venda, atualizações no `plano-de-teste.md` e no `docs/README.md`, e registro no AI-LOG. |
| **Decisão** | **Aceito:** a escolha de `VendaService` como classe sob teste; a estrutura do plano em 10 tasks; os casos de teste propostos para cada método. **A definir pelo Alexandre:** aprovação ou ajuste do plano antes de iniciar a implementação; escolha de quais casos de teste manuais executar primeiro. **Deixado em aberto:** campos "Decisão" e "Validação" desta entrada, a serem preenchidos após execução. |
| **Validação** | A ser preenchido após execução das tasks e confirmação de que a suíte passa. |
| **Transcrição** | Não preservada |

---

## AI-005 — Implementação da Task 1 (plano-de-teste.md) e Task 2 (VendaServiceTest.java — estrutura base e testes de abreVenda)

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-22 |
| **Responsável** | Alexandre Porto |
| **Atividade** | Entrega 1 — atualização do Plano de Teste com responsabilidades do Alexandre e implementação da estrutura base de `VendaServiceTest` com os casos de teste do método `abreVenda` |
| **Ferramenta** | Kiro CLI (Claude Sonnet 4.6 — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | (1) "Realize a task 1: Atualizar docs/plano-de-teste.md com as responsabilidades do Alexandre."; (2) "Antes de prosseguir para as proximas tasks, faça uma modificação importante. Você atuará como um testador. Um profissional que deve buscar por erros na aplicação. A demo não pode ser os testes passarem, mas sim os testes cobrirem as funcionalidades. A cobertura dos testes deve ser a maior possível. Falhas em teste são esperadas."; (3) "Sim, prossiga para a task 2 — Implementar VendaServiceTest.java — estrutura base e testes de abreVenda" |
| **Resultado** | **Task 1:** preenchimento de três seções do `plano-de-teste.md`: (a) linha do Alexandre na tabela de classes sob teste, com módulo Venda, classe `VendaService` e justificativa completa; (b) linha de funcionalidades de testes manuais com "Fechamento de venda"; (c) entrada 0.2 no Registro de Mudanças. **Task 2:** criação de `src/test/java/net/originmobi/pdv/VendaServiceTest.java` com infraestrutura completa (11 mocks, `MockedStatic<Aplicacao>`, helpers `dadoUsuarioAutenticado`/`novaVenda`/`vendaExistente`) e 11 casos de teste para `abreVenda` (7 para venda nova, incluindo um caso que documenta defeito de exceção silenciada; 4 para venda existente). Identificado e documentado um defeito real: no ramo de venda nova, `save` lança exceção mas o `catch` a descarta via `e.getStackTrace()`, retornando `null` ao chamador sem qualquer sinalização de falha. Arquivo compila com `BUILD SUCCESS` verificado via `mvn test-compile`. |
| **Decisão** | **Aceito:** mudança de perspectiva de testador (asserções pelo contrato esperado, não pelo comportamento atual); estrutura de mocks seguindo o padrão de `CaixaServiceTest`; uso de `assertThrows` para documentar defeito (o teste falha intencionalmente, evidenciando o bug). **Alterado:** uso da forma de 2 argumentos em `assertThrows` (sem mensagem inline) para compatibilidade com inferência de tipos do compilador Java 8. **Rejeitado:** nenhum item rejeitado nesta sessão. |
| **Validação** | Compilação verificada: `mvn test-compile -Dmaven.resources.skip=true` → `BUILD SUCCESS`, `VendaServiceTest.class` (11K) gerado em `target/test-classes/`. Nota: o skip de recursos é necessário porque `target/classes` contém arquivos pertencentes a `root` (artefatos de build anterior via Docker) que bloqueiam o passo de cópia de recursos. O código Java compila sem erros. |
| **Transcrição** | Conversa preservada em [`transcricoes/AI-005-task1-task2-VendaServiceTest.md`](transcricoes/AI-005-task1-task2-VendaServiceTest.md). |

---

## AI-006 — Implementação das Tasks 3-7 (VendaServiceTest — busca, addProduto, removeProduto, fechaVenda, qtdAbertos)

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-22 |
| **Responsável** | Alexandre Porto |
| **Atividade** | Entrega 1 — implementação dos casos de teste unitários para os métodos `busca`, `addProduto`, `removeProduto`, `fechaVenda` (guards e caminhos felizes) e `qtdAbertos` de `VendaService` |
| **Ferramenta** | Kiro CLI (Claude Sonnet 4.6 — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | "Agora realize as tasks de 3 a 7. Ao final, crie o AI-006 em docs/ai/AI-LOG.md e faça o novo documento de transcrição AI-006" |
| **Resultado** | Adicionados ~20 novos casos de teste ao `VendaServiceTest.java`, cobrindo: (a) `busca` — 3 casos (filtro por código, situação ABERTA, situação FECHADA); (b) `addProduto` — 3 casos incluindo defeito de exceção silenciada; (c) `removeProduto` — 3 casos incluindo defeito crítico de `return` dentro de `try` ignorado; (d) `fechaVenda` guards — 5 casos (venda fechada, valor zero, valor negativo, caixa fechado, sem cliente); (e) `fechaVenda` caminhos felizes — 6 casos (à vista dinheiro: lançamento caixa, retorno sucesso, movimentação estoque, fechamento com FECHADA; cartão débito; cartão crédito); (f) `fechaVenda` a prazo — 1 caso (geração de parcela com sequência=1); (g) `qtdAbertos` — 1 caso. Total acumulado na suíte: ~30 casos. Identificados e documentados 3 novos defeitos além do já registrado em AI-005. BUILD SUCCESS confirmado (`VendaServiceTest.class` de 26K). |
| **Decisão** | **Aceito:** cobertura de todos os métodos públicos; uso de strings literais (`"DIN"`, `"CARTDEB"`, `"CARTCRED"`) em vez do enum `TituloTipo` para evitar conflito de nomes com a entidade JPA homônima; remoção dos stubs incorretos de `when(receberServ.cadastrar(...)).thenReturn(...)` (método `void`). **Defeitos documentados como testes que falham intencionalmente:** ver seção de transcrição. **A definir pelo Alexandre:** criação dos GitHub Issues para os defeitos encontrados. |
| **Validação** | `mvn test-compile -Dmaven.resources.skip=true` → `BUILD SUCCESS`. `VendaServiceTest.class` (26K) em `target/test-classes/`. |
| **Transcrição** | [`transcricoes/AI-006-tasks3-7-VendaServiceTest.md`](transcricoes/AI-006-tasks3-7-VendaServiceTest.md) |

---

## AI-007 — Geração da suíte de testes unitários inicial para AjusteService

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-22 |
| **Responsável** | Natalia de Abreu Lamas |
| **Atividade** | Entrega 1 — implementação dos testes unitários da classe AjusteService |
| **Ferramenta** | Gemini |
| **Prompt/instrução** | Requisitada a criação da suíte inicial de testes unitários em Java com JUnit 5 e Mockito para cobrir os métodos da classe `AjusteService`. |
| **Resultado** | A IA gerou a classe de teste inicial `AjusteServiceTest.java` com 4 cenários básicos cobrindo chamadas simples de processamento e remoção de ajustes de estoque. |
| **Decisão** | **Aceito:** a estrutura base da classe de teste com `@InjectMocks` e `@Mock`. **Ajustado:** o código foi salvo como versão v0 em [`transcricoes/AI-007-v0-AjusteServiceTest.java`](transcricoes/AI-007-v0-AjusteServiceTest.java) para posterior refatoração e ampliação de cobertura. |
| **Validação** | Verificação do código gerado, identificando ausência de cobertura para exceções nos blocos `try/catch` e asserções nas chamadas do `ProdutoService`. |
| **Transcrição** | Prompts em [`transcricoes/AI-007-prompts-AjusteService.md`](transcricoes/AI-007-prompts-AjusteService.md); solução v0 em [`transcricoes/AI-007-v0-AjusteServiceTest.java`](transcricoes/AI-007-v0-AjusteServiceTest.java). |

---

## AI-008 — Refatoração, cobertura de exceções e finalização de AjusteServiceTest

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-22 |
| **Responsável** | Natalia de Abreu Lamas |
| **Atividade** | Entrega 1 — revisão crítica e ampliação de cobertura dos testes unitários de `AjusteService` |
| **Ferramenta** | Gemini |
| **Prompt/instrução** | "Analise a classe AjusteService e identifique se os testes cobrem todos os fluxos de exceção, os blocos try/catch e a verificação do serviço de estoque (ProdutoService)." |
| **Resultado** | A IA refatorou a suíte para 7 casos de teste, adicionando verificações para `produtoService.ajusteEstoque(...)`, cobertura dos lançamentos de exceção ao tentar alterar estoque ou deletar registros com falha, teste do método `busca` e anotações `@DisplayName`. |
| **Decisão** | **Aceito:** inclusão dos cenários de exceção e verificações do Mockito. **Alterado:** correção do pacote de importação para `net.originmobi.pdv` e remoção de caracteres de sintaxe inválidos (`};;`). |
| **Validação** | A suíte final em `src/test/java/net/originmobi/pdv/AjusteServiceTest.java` compila e cobre todos os cenários principais e fluxos de exceção da classe `AjusteService`. |
| **Transcrição** | Relatório de alterações em [`transcricoes/AI-008-alteracoes-v0-para-final-AjusteService.md`](transcricoes/AI-008-alteracoes-v0-para-final-AjusteService.md). |

---

## AI-009 — Elaboração e estruturação dos Casos de Teste Manuais do módulo Ajuste de Estoque

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-22 |
| **Responsável** | Natalia de Abreu Lamas |
| **Atividade** | Entrega 1 — especificação dos casos de teste manuais e validação de regras de negócio de `AjusteService` |
| **Ferramenta** | Gemini |
| **Prompt/instrução** | Solicitação de estruturação de casos de teste manuais para o módulo de Ajuste de Estoque, cobrindo o fluxo principal de processamento de entrada e o cenário de bloqueio de remoção de ajustes já processados. |
| **Resultado** | A IA produziu a especificação formatada dos casos de teste `CT-AJU-001` (Processamento com Sucesso) e `CT-AJU-002` (Bloqueio de Remoção de Ajuste Processado), incluindo pré-condições, massa de dados, passos a passos detalhados e resultados esperados. |
| **Decisão** | **Aceito:** a estrutura dos cenários e as mensagens de validação alinhadas ao comportamento real da aplicação. **Alterado:** inclusão explícita dos parâmetros de justificativa de inventário e checagem de atualização do saldo do produto. |
| **Validação** | Verificação das regras de negócio diretamente no código de `AjusteService.java` para garantir correspondência exata entre os resultados esperados no teste manual e as mensagens retornadas pela aplicação. |
| **Transcrição** | Não preservada |

---

## AI-010 — Execução e documentação dos testes manuais de `VendaService`, revisão da suíte e registro de defeitos

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-09-23 |
| **Responsável** | Alexandre Porto |
| **Atividade** | Entrega 1 — revisão dos testes unitários de `VendaService`, planejamento e documentação dos testes manuais de fechamento de venda, registro dos defeitos encontrados e organização dos artefatos |
| **Ferramenta** | Kiro CLI (Claude Sonnet 4.6 — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | Sequência de instruções do integrante: (1) analisar o escopo no PDF, verificar se os testes automatizados de `VendaService` representam o comportamento esperado e explicar como fazer e documentar os testes manuais e quais executar; (2) mapear os caminhos pela interface; (3) diagnosticar erros observados durante a execução manual ("Zero length string", título vazio, estoque que não incrementa, cartão que não fecha); (4) documentar os defeitos e converter o relatório `.odt` em `.md` estruturado, embutindo as evidências (prints); (5) ajustar numeração/escopo dos casos, remover o `.odt` e referenciar os artefatos nos índices. |
| **Resultado** | A IA leu o PDF do enunciado, `VendaService`, `VendaController`, `CartaoLancamentoService`, `AjusteService`, `AjusteProdutoService`, `ProdutoService`/`ProdutoRepository`, os templates de venda/título, o `venda.js` e as migrações Flyway. Produziu: (a) revisão crítica da suíte `VendaServiceTest` apontando lacunas (pagamento composto, desconto≠acréscimo, soma de parcelas em `avistaDinheiro`); (b) diagnóstico de causa raiz de 4 defeitos; (c) fichas de defeito em `docs/defeitos/` (DEF-VENDA-001 "Zero length string" por título vazio; DEF-VENDA-002 cartão não fecha por `NullPointerException` em título sem máquina; DEF-VENDA-003 desconto/acréscimo trocados na ordem dos parâmetros de `aprazo`/`avistaDinheiro`; DEF-ESTOQUE-001 trigger de banco que só subtrai e ignora o tipo, impedindo entrada de estoque); (d) relatório `docs/relatorios/relatorio-testes-manuais-vendaservice.md` com 10 casos de teste estruturados (esperado × obtido, status, evidências embutidas); (e) remoção do `.odt` antigo e do lock; (f) referências nas tabelas de `docs/README.md` e do README da raiz. |
| **Decisão** | **Aceito:** o diagnóstico das causas raiz (confirmadas por leitura do código/migrações); a estrutura de casos de teste e as fichas de defeito; a substituição do `.odt` por `.md`. **Alterado pelo integrante:** os cenários 6–10 marcados como executados; o antigo CT-11 (estoque insuficiente) renumerado para CT-VENDA-10 e o CT-10 (pagamento composto) retirado do escopo; evidências reais do CT-06 (desconto 1,00 / acréscimo 0,95) anexadas e valores da ficha DEF-VENDA-003 alinhados a elas. **Sinalizado pela IA e mantido:** os cartões (CT-03/CT-04) permanecem como *Falhou* porque as evidências mostram "No message available" (venda não fecha), coerente com DEF-VENDA-002. **A definir pelo Alexandre:** abrir os GitHub Issues dos defeitos e cadastrar ao menos um caso no TestLink. |
| **Validação** | Causas raiz confirmadas por inspeção direta do código-fonte e das migrações (ex.: `Long.decode("")` em `fechaVenda`; `getMaquina()` nulo em `CartaoLancamentoService`; ordem de parâmetros invertida entre chamada e assinatura; trigger `atualiza_produto_estoque_AFTER_INSERT` sem tratar `tipo` e com guarda `@qtd <= @qtd_estoque`). Cada caso de teste manual foi confrontado com a evidência (print) correspondente; os valores de desconto/acréscimo do CT-06 foram lidos diretamente das imagens. **Não foi possível** executar a suíte automatizada neste ambiente (sem `javac`/Maven; wrapper sem jar) — a verificação da suíte permanece a ser feita via container (`docker compose exec pdv-app mvn -Dtest=VendaServiceTest test`). |
| **Transcrição** | Não preservada |

## AI-011 — Isolamento de dependências com Mockito e ampliação de cobertura em `AjusteServiceTest`

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-11-04 |
| **Responsável** | Natalia de Abreu Lamas |
| **Atividade** | Entrega 2 — isolamento de dependências (Mocks) e expansão da suíte de testes unitários da classe `AjusteService` para atendimento ao critério todas-arestas (80%+) e testes de mutação |
| **Ferramenta** | Gemini |
| **Prompt/instrução** | "Analise a estrutura do `AjusteServiceTest` da Entrega 1 e indique como aplicar `@Mock` e `@InjectMocks` com JUnit 5 para isolar a dependência do `ProdutoService` e do `AjusteRepository`. Sugira também os cenários de teste necessários para cobrir os fluxos de exceção dos blocos try/catch e o ramo de quantidade negativa (saída de estoque) no método `processar`." |
| **Resultado** | A IA apresentou a estrutura de migração para `@ExtendWith(MockitoExtension.class)`, demonstrando a substituição dos acessos diretos por stubs de mock (`when(...).thenReturn(...)` e `doThrow(...)`). Além disso, mapeou os cenários faltantes para cobertura completa do método `processar` (movimentação de `SAIDA` e falha no `ajusteRepository.save`) e dos métodos de consulta `lista` e `novo`. |
| **Decisão** | **Aceito:** a aplicação das anotações do Mockito para isolamento das dependências e a inclusão dos cenários de teste para cobrir a ramificação negativa do operador ternário (`qtd_alteracao > 0 ? ENTRADA : SAIDA`) e as exceções no repositório. **Alterado pela integrante:** mantida a nomenclatura original da variável injetada (`ajusteService`) para garantir a consistência com os artefatos da Entrega 1; ajustada a assinatura e os stubs dos cenários de paginação (`Pageable`/`Page`). |
| **Validação** | Verificação direta da suíte em `src/test/java/net/originmobi/pdv/service/AjusteServiceTest.java`. A integrante conferiu o isolamento das dependências (garantindo que o banco de dados não é acionado) e validou que todos os desvios condicionais e tratamentos de exceção da classe `AjusteService` possuem asserções equivalentes com `assertThrows` e `verify`. |
| **Transcrição** | Não preservada |

---

## AI-012 — Entrega 2: isolamento do BCrypt, valores-limite e arestas restantes em `CaixaServiceTest`

| Campo | Conteúdo |
| --- | --- |
| **Data** | 2026-11-08 |
| **Responsável** | Eloyse Fernanda |
| **Atividade** | Entrega 2 — "melhorar e aumentar os casos de testes unitários, isolando suas dependências" (classe `CaixaService`) |
| **Ferramenta** | Claude (Claude Code — agente com acesso de leitura e escrita ao repositório local) |
| **Prompt/instrução** | A integrante pediu para começar a Entrega 2 pela melhoria dos testes unitários isolando as dependências, restrito ao `CaixaService`. A IA analisou a suíte e o relatório JaCoCo e propôs 7 itens; a integrante aprovou. |
| **Resultado** | Na branch `entrega2/caixaservice-testes-unitarios`: (1) `BCryptPasswordEncoder`, instanciado dentro de `fechaCaixa`, isolado com `mockConstruction` — o teste não executa mais o BCrypt real e verifica que a senha é comparada com o hash do usuário logado; (2) asserções de data reforçadas (`data_cadastro` = hoje; `data_fechamento` dentro do intervalo da execução) no lugar de `assertNotNull`; (3) novo caso "Abertura de banco" (aresta da linha 90 não coberta); (4) filtro de banco com data vazia (aresta da linha 196); (5) valores-limite da abertura: -0,01 e -1 rejeitados, 0 sem lançamento, 0,01 com lançamento; (6) teste de descrição padrão parametrizado (3 tipos × descrição vazia/preenchida), substituindo um teste que juntava 3 cenários; (7) COFRE/BANCO não consultam caixa aberto. Suíte: 29 → 41 casos. |
| **Decisão** | **Aceito pela integrante:** os 7 itens propostos. **Adiado:** três defeitos identificados por leitura do código (`fechaCaixa` com caixa inexistente → `NoSuchElementException`; senha `null` → `NullPointerException`; `listaBancosAbertosTipoFilterBanco` ignora o tipo quando não há data) — ficam para avaliação e registro em issues. **Não coberto, por decisão:** a aresta falsa do `else if (BANCO)` em `cadastro` (linha 62) é inalcançável, pois `CaixaTipo` só tem CAIXA, COFRE e BANCO. «completar com alterações feitas pela integrante na revisão» |
| **Validação** | Execução real em container Java 8 (`docker run ... maven:3.8.8-eclipse-temurin-8 mvn -Dtest=CaixaServiceTest test`): **Tests run: 41, Failures: 0, Errors: 0, Skipped: 0**. JaCoCo em `CaixaService`: arestas 41/44 (93,2%) → **43/44 (97,7%)**, instruções 407/407. |
| **Transcrição** | solução inicial da IA: [`transcricoes/AI-012-v0-CaixaServiceTest.java`](transcricoes/AI-012-v0-CaixaServiceTest.java); versão final: `src/test/java/net/originmobi/pdv/CaixaServiceTest.java`. |
