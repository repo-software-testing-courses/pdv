# Plano de testes do trabalho — PDV - Em Construção

**UNIVERSIDADE FEDERAL FLUMINENSE**

**Professor(a):** Vânia de Oliveira Neves

**Alunos:** Alexandre Porto, Eloyse Fernanda e Natália Abreu

> Cópia versionada no repositório. A versão editável (Google Docs), com o
> histórico de colaboração de cada integrante, está linkada no
> [README do repositório](../README.md).

---

## Sumário

1. [Introdução](#1-introdução)
   - 1.1 [Escopo](#11-escopo)
     - 1.1.1 [No escopo](#111-no-escopo)
     - 1.1.2 [Fora do escopo](#112-fora-do-escopo)
   - 1.2 [Objetivos de Qualidade](#12-objetivos-de-qualidade)
   - 1.3 [Papéis e Responsabilidades](#13-papéis-e-responsabilidades)
2. [Metodologia de Teste](#2-metodologia-de-teste)
   - 2.1 [Visão Geral](#21-visão-geral)
   - 2.2 [Fases de Teste](#22-fases-de-teste)
   - 2.3 [Critérios de Suspensão e Requisitos de Retomada](#23-critérios-de-suspensão-e-requisitos-de-retomada)
   - 2.4 [Completude do Teste](#24-completude-do-teste)
3. [Entregáveis de Teste](#3-entregáveis-de-teste)
4. [Necessidades de Recursos e Ambiente](#4-necessidades-de-recursos-e-ambiente)
   - 4.1 [Ferramentas de Teste](#41-ferramentas-de-teste)
   - 4.2 [Ambiente de Teste](#42-ambiente-de-teste)

---

## Registro de Mudanças

| Versão | Data de Mudança | Por | Descrição |
| --- | --- | --- | --- |
| 0.1 | 19/09/2026 | Eloyse Fernanda | Estruturação inicial do plano conforme template da disciplina; definição de escopo, ambiente, ferramentas e classe sob teste do módulo Caixa |
| 0.2 | 22/09/2026 | Alexandre Porto | Inclusão da classe sob teste do módulo Venda (`VendaService`), da funcionalidade de testes manuais (fechamento de venda) e planejamento dos casos de teste unitários |
| 0.3 | 22/09/2026 | Natalia de Abreu Lamas | Inclusão da classe sob teste do módulo Estoque (`AjusteService`), das funcionalidades de testes manuais e planejamento dos casos de teste unitários |

---

## 1. INTRODUÇÃO

Este documento descreve a estratégia, o processo e as metodologias de teste
adotados para o sistema **PDV**, um ERP web de ponto de venda desenvolvido em
Java com Spring Boot, utilizado como objeto de estudo na disciplina Qualidade e
Teste.

O sistema é um fork do projeto livre [originmobi/pdv](https://github.com/originmobi/pdv)
e contempla cadastro de produtos, clientes e fornecedores, controle de estoque,
comandas, vendas (inclusive com cartão), fluxo de caixa, contas a pagar e a
receber, permissões de usuários por grupo e relatórios.

O trabalho é conduzido em duas entregas incrementais. A Entrega 1 concentra o
planejamento, o projeto de casos de teste unitários e a execução de testes
manuais. A Entrega 2 amplia a suíte com isolamento de dependências, testes de
integração e de sistema, aplicação das técnicas funcional, estrutural e baseada
em defeitos, e inspeção estática do código-fonte.

O uso de ferramentas de Inteligência Artificial Generativa é permitido e está
registrado em [`docs/ai/AI-LOG.md`](ai/AI-LOG.md), conforme exigido pela
disciplina.

### 1.1 Escopo

#### 1.1.1 No escopo

Serão testadas as classes de **regra de negócio** (camada de serviço) do
sistema, selecionadas segundo os critérios do enunciado: não podem ser CRUD de
entidade e devem apresentar complexidade razoável.

| Módulo | Classe sob teste | Justificativa da escolha | Responsável |
| --- | --- | --- | --- |
| Caixa | `net.originmobi.pdv.service.CaixaService` | Regra de negócio de abertura e fechamento de caixa. Concentra a maior densidade de pontos de decisão da camada de serviço. Possui desvios encadeados por tipo de caixa (CAIXA/COFRE/BANCO), operadores ternários aninhados, normalização de valores nulos, validação de senha e blocos `try/catch`. Não é CRUD de entidade. | Eloyse Fernanda |
| Estoque | `net.originmobi.pdv.service.AjusteService` | Regra de negócio do módulo de ajuste de estoque. Responsável por processar e aplicar alterações de inventário nos produtos, atualizar saldos e controlar bloqueios de exclusão. Contém tratamento de exceções com blocos try/catch e integração com ProdutoService. Não é CRUD de entidade. | Natalia de Abreu Lamas |
| Venda | `net.originmobi.pdv.service.VendaService` | Regra de negócio do módulo de vendas. Orquestra abertura, adição/remoção de produtos e fechamento de venda com múltiplas formas de pagamento (dinheiro, cartão débito/crédito, a prazo). Contém laços, desvios encadeados sobre tipo de pagamento e título, operadores ternários, normalização de valores, guardas de validação e blocos `try/catch`. Complexidade ciclomática estimada ≥ 20. Não é CRUD de entidade. Domínio distinto do módulo Caixa já coberto por Eloyse. | Alexandre Porto |

**Funcionalidades no escopo dos testes manuais:**

| Funcionalidade | Responsável |
| --- | --- |
| Abertura e fechamento de caixa | Eloyse Fernanda |
| Processamento e validação de remoção de Ajuste de Estoque | Natalia de Abreu Lamas |
| Fechamento de venda (à vista em dinheiro, cartão débito/crédito e a prazo) | Alexandre Porto |

#### 1.1.2 Fora do escopo

Não serão objeto de teste neste trabalho:

- **Telas e fluxos de CRUD simples** (cadastro de categorias, cidades, estados,
  CST/CSOSN e demais tabelas de apoio), por não apresentarem algoritmos com
  complexidade compatível com os critérios da disciplina.
- **Emissão fiscal de NF-e junto à SEFAZ**, por exigir certificado digital e
  comunicação com ambiente externo.
- **Camada de persistência gerada pelo Spring Data JPA** (interfaces
  `*Repository` sem implementação própria), por não conter lógica escrita pela
  equipe.
- **Compatibilidade entre navegadores e testes de usabilidade**, por estarem
  fora dos objetivos da disciplina.

### 1.2 Objetivos de Qualidade

Os objetivos que a equipe pretende alcançar com este projeto de teste são:

1. **Verificar a conformidade funcional** das regras de negócio selecionadas,
   assegurando que os módulos sob teste se comportem conforme o esperado tanto
   em fluxos normais quanto em situações de exceção e valores de fronteira.
2. **Identificar e registrar defeitos** existentes no código legado antes que
   sejam propagados, documentando-os em ferramenta de rastreamento.
3. **Demonstrar uso crítico e documentado de IA generativa**, preservando a
   solução inicial gerada, a solução final revisada e a descrição das alterações
   realizadas.

### 1.3 Papéis e Responsabilidades

> _(rever)_

| Papel | Atribuições | Responsável |
| --- | --- | --- |
| Projetistas de teste | Definir as estratégias e métodos que serão utilizados durante os testes, considerando as ferramentas e recursos disponíveis. Analisar os resultados obtidos e acompanhar os problemas identificados durante a execução | TODOS |
| Desenvolvedores de teste | Criar e executar os cenários de testes definidos, garantindo que as funcionalidades sejam avaliadas conforme os requisitos estabelecidos | TODOS |
| Responsáveis pela validação | Realizar a aplicação dos testes planejados, registrar os resultados encontrados e acompanhar possíveis erros ou comportamentos inesperados. | TODOS |

---

## 2. METODOLOGIA DE TESTE

### 2.1 Visão Geral

A metodologia adotada é **iterativa e incremental**, alinhada à estrutura do
trabalho em duas entregas. Na primeira iteração são definidos o escopo e o
planejamento, projetados os primeiros casos de teste unitários e executados os
testes manuais, com registro dos defeitos encontrados. Na segunda iteração a
suíte é refinada e ampliada, incorporando isolamento de dependências, novos
níveis de teste e técnicas adicionais de projeto de casos de teste.

A escolha se justifica por dois motivos. Primeiro, o sistema sob teste é um
**projeto legado de terceiros**, sem suíte de testes prévia: o conhecimento do
domínio é construído progressivamente, e um planejamento em cascata exigiria
decisões precoces sobre código ainda não compreendido. Segundo, o formato do
trabalho já impõe dois marcos de avaliação com feedback intermediário da
professora, o que favorece o refinamento do plano entre as iterações. Alterações
de escopo decorrentes desse aprendizado serão registradas no Registro de
Mudanças.

### 2.2 Fases de Teste

| Fase | Nível | Técnica | Entrega | Descrição |
| --- | --- | --- | --- | --- |
| Teste unitário | Unidade | Funcional (caixa-preta) | 1 | Projeto e implementação de casos de teste para os métodos de regra de negócio das classes selecionadas, sem isolamento de dependências |
| Teste manual | Sistema | Funcional | 1 | Execução manual de cenários pela interface web, com registro de evidências |
| Teste unitário com isolamento | Unidade | Funcional + Estrutural | 2 | Refinamento da suíte com uso de dublês de teste (mocks/stubs) para isolar repositórios e serviços colaboradores |
| Teste de integração | Integração | Funcional | 2 | Verificação da interação entre serviços e camada de persistência |
| Teste de sistema | Sistema | Funcional | 2 | Automação de requisitos funcionais pela interface web com Selenium |
| Teste não funcional | Sistema | — | 2 | Ao menos um atributo de qualidade (desempenho ou segurança) |
| Teste estrutural | Unidade | Caixa-branca | 2 | Ampliação da suíte até 80% de cobertura no critério todas-arestas |
| Teste baseado em defeitos | Unidade | Mutação | 2 | Ampliação da suíte até 80% de escore de mutação nas mesmas classes |
| Inspeção de código | Estática | Revisão automatizada | 2 | Análise estática com SonarQube e correção dos problemas apontados |

### 2.3 Critérios de Suspensão e Requisitos de Retomada

**Critérios de suspensão** — a execução dos testes será suspensa, no todo ou em
parte, quando:

- O ambiente de teste estiver indisponível (falha na subida dos containers,
  indisponibilidade do banco de dados ou falha na aplicação das migrações
  Flyway).
- A aplicação não iniciar ou o build Maven falhar, impedindo a execução da
  suíte.
- For identificado defeito de severidade Crítica que bloqueie o acesso ao módulo
  sob teste, inviabilizando os cenários dependentes.
- Mais de 40% dos casos de teste de um módulo falharem por um mesmo defeito de
  origem comum, caracterizando retrabalho improdutivo.

**Requisitos de retomada** — os testes serão retomados quando:

- O ambiente for restabelecido e validado por um teste de fumaça (login no
  sistema e acesso ao módulo sob teste).
- O defeito bloqueante for corrigido, ou for definido um contorno documentado
  que permita a execução dos cenários.
- Os casos de teste afetados forem reavaliados quanto à necessidade de revisão.

**Riscos monitorados** que podem levar à suspensão: ausência de suíte de testes
prévia no projeto legado; restrições da plataforma Java 8, que limitam versões
de bibliotecas de teste; acoplamento a componentes estáticos (por exemplo, o
singleton `Aplicacao`), que dificulta o isolamento de dependências; e
dependência de banco de dados real para cenários de integração.

### 2.4 Completude do Teste

**Critérios de entrada** — os testes só se iniciam quando:

- O ambiente estiver disponível e a aplicação acessível.
- O escopo estiver definido e aprovado, com responsáveis atribuídos.

**Critérios de saída (Entrega 1)** — o teste é considerado completo quando:

- Todos os casos de teste unitários projetados estiverem documentados, com ao
  menos uma classe por integrante.
- Todos os casos de teste manuais projetados tiverem sido executados, com
  evidências registradas.
- Ao menos um cenário estiver cadastrado e executado no TestLink.
- Todos os defeitos encontrados estiverem registrados no rastreador.
- O Plano de Teste estiver aprovado pela equipe e referenciado no README.

---

## 3. ENTREGÁVEIS DE TESTE

- Código fonte com os testes unitários;
- Plano de testes;
- Slides de apresentação;
- README no github com os links necessários das documentações;

---

## 4. NECESSIDADES DE RECURSOS E AMBIENTE

### 4.1 Ferramentas de Teste

| Finalidade | Ferramenta | Observação |
| --- | --- | --- |
| Teste unitário | JUnit | Execução via Maven (`./mvnw test`) |
| Teste de integração | Spring Boot Test | Contexto de aplicação para testes integrados |
| Isolamento de dependências | Mockito | Dublês de teste para repositórios e serviços colaboradores (Entrega 2) |
| Gerenciamento de casos de teste manuais | TestLink | Ao menos um cenário; demais casos em documento ou planilha |
| Rastreamento de defeitos | GitHub Issues | Rastreador oficial do trabalho |
| Cobertura de código | JaCoCo | Já configurado no `pom.xml`; relatório em `target/site/jacoco/` |
| Teste de mutação | PIT (pitest) | Escore de mutação (Entrega 2) |
| Análise estática / inspeção | SonarQube | Evidência antes e depois das correções (Entrega 2) |
| Teste de sistema (web) | Selenium | Automação de requisitos funcionais (Entrega 2) |
| Build e gestão de dependências | Maven | Wrapper incluído no repositório (`./mvnw`) |
| Controle de versão e colaboração | Git / GitHub | Colaboração individual avaliada pelo histórico |
| Documentação colaborativa | Google Docs | Histórico de versões evidencia a colaboração individual |
| IA generativa | Claude, ChatGPT, GitHub Copilot | Uso documentado em `docs/ai/AI-LOG.md` |

### 4.2 Ambiente de Teste

**Software necessário:**

| Componente | Versão |
| --- | --- |
| Java (JDK) | 8 |
| Maven | Wrapper incluído (`./mvnw`) |
| MySQL | 8 |
| Docker + Compose | v2 |
| Navegador | Atualizado, para os testes manuais e de sistema |

**Execução via Docker (recomendada):**

```sh
docker compose up -d
```

| Serviço | Portas | Descrição |
| --- | --- | --- |
| `pdv-app` | 8080 (aplicação), 5005 (debug JDWP) | Build Maven e execução do `.war` |
| `pdv-db` | 3306 | MySQL 8 com o banco `pdv` criado |

A aplicação fica disponível em <http://localhost:8080>. As migrações do Flyway
são aplicadas automaticamente na subida.

**Dados de acesso** (credenciais padrão do projeto original, adequadas apenas
para uso local):

| Usuário | Senha |
| --- | --- |
| `gerente` | `123` |

**Execução da suíte de testes:**

```sh
./mvnw test          # executa a suíte
./mvnw clean test    # testes + relatório de cobertura JaCoCo
```
