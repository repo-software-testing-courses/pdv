# PDV QA — Qualidade e Teste de Software

Repositório da **Entrega 1** do trabalho prático da disciplina de Qualidade e Teste de Software.

O trabalho utiliza dois sistemas, conforme autorizado pela professora:

- **PDV**: base dos testes unitários sobre regras de negócio em Java.
- **webapp-1**: base dos testes manuais e dos testes de sistema registrados no TestLink.

## Equipe e responsabilidades

| Integrante | Testes unitários no PDV | Teste manual no webapp-1 |
|---|---|---|
| Pedro Campos Camargos | `VendaService` | U2 — Assistir ao trailer do filme mais assistido |
| Arthur Jardim Antunes Ferreira | `CaixaService` | U1 — Visualizar comentários de uma série |
| Rodrigo Lima de Albuquerque | `RecebimentoService` | U4 — Editar dados do perfil e gerenciar listas |
| João Paulo Alves da Silveira | `NotaFiscalItemService` | U3 — Pesquisar série por gênero e verificar o retorno |

Além dos casos individuais, o grupo cadastrou e executou no TestLink o caso **VU-2 — Cadastrar, ativar e acessar uma conta**.

## Escopo da Entrega 1

### Testes unitários — PDV

Foram implementados **88 testes unitários** com JUnit 4 e Mockito, distribuídos entre quatro classes de serviço:

| Classe | Integrante | Quantidade de testes | Código | Documentação |
|---|---|---:|---|---|
| `VendaService` | Pedro | 28 | [VendaServiceTest.java](src/test/java/net/originmobi/pdv/service/VendaServiceTest.java) | [Testes_Unitarios_VendaService_Pedro.docx](docs/casos-de-testes/pedro-camargos/Testes_Unitarios_VendaService_Pedro.docx) |
| `CaixaService` | Arthur | 22 | [CaixaServiceTest.java](src/test/java/net/originmobi/pdv/service/CaixaServiceTest.java) | [Testes_Unitarios_CaixaService_Arthur.docx](docs/casos-de-testes/arthur-jardim/Testes_Unitarios_CaixaService_Arthur.docx) |
| `RecebimentoService` | Rodrigo | 21 | [RecebimentoServiceTest.java](src/test/java/net/originmobi/pdv/service/RecebimentoServiceTest.java) | [Testes_Unitarios_RecebimentoService_Rodrigo.docx](docs/casos-de-testes/rodrigo-lima/Testes_Unitarios_RecebimentoService_Rodrigo.docx) |
| `NotaFiscalItemService` | João Paulo | 17 | [NotaFiscalItemServiceTest.java](src/test/java/net/originmobi/pdv/service/notafiscal/NotaFiscalItemServiceTest.java) | [Testes_Unitarios_NotaFiscalItemService_JoaoPaulo.docx](docs/casos-de-testes/joao-paulo-alves/Testes_Unitarios_NotaFiscalItemService_JoaoPaulo.docx) |

Na execução registrada pela equipe, todos os 88 testes passaram. O relatório JaCoCo deve ser consultado como evidência da cobertura e não deve ser substituído apenas pela quantidade de casos.

### Testes manuais — webapp-1

| ID | Responsável | Funcionalidade | Resultado |
|---|---|---|---|
| U1 | Arthur | Visualizar comentários de uma série | Aprovado |
| U2 | Pedro | Assistir ao trailer do filme mais assistido | Aprovado |
| U3 | João Paulo | Pesquisar série por gênero e verificar o retorno | Aprovado |
| U4 | Rodrigo | Editar dados do perfil e gerenciar listas | Reprovado em 3 passos |
| VU-2 | Grupo | Cadastrar, ativar e acessar uma conta | Reprovado |

Evidências:

- [Caso manual — Arthur](docs/casos-de-testes/arthur-jardim/caso-teste-manual-arthur.pdf)
- [Caso manual — Pedro](docs/casos-de-testes/pedro-camargos/caso-teste-manual-pedro.pdf)
- [Caso manual — João Paulo](docs/casos-de-testes/joao-paulo-alves/caso-teste-manual-joao.pdf)
- [Caso manual — Rodrigo](docs/casos-de-testes/rodrigo-lima/caso-teste-manual-rodrigo.pdf)
- [Relatório do TestLink](docs/casos-de-testes/testlink/TestLink%20QeT.pdf)

### Defeitos encontrados

| Issue | Origem | Resumo |
|---|---|---|
| [#8](https://github.com/cmrgsPedro/pdv-qa/issues/8) | VU-2 | Falha no envio do e-mail de autorização |
| [#9](https://github.com/cmrgsPedro/pdv-qa/issues/9) | U4 | Campo de nome não editável |
| [#10](https://github.com/cmrgsPedro/pdv-qa/issues/10) | U4 | Alteração do e-mail não é salva corretamente |
| [#11](https://github.com/cmrgsPedro/pdv-qa/issues/11) | U4 | Falha no botão de adicionar filme/série à lista |

Todos os defeitos foram registrados neste repositório para manter a rastreabilidade entre execução, evidência e issue: [consultar issues](https://github.com/cmrgsPedro/pdv-qa/issues).

## Como executar o PDV

### Pré-requisitos

- Git;
- Docker Desktop com Docker Compose v2;
- portas `8080`, `3306` e `5005` disponíveis.

Clone o fork do grupo e acesse a pasta do projeto:

```sh
git clone https://github.com/cmrgsPedro/pdv-qa.git
cd pdv-qa
```

Suba a aplicação:

```sh
docker compose up --build
```

Após a inicialização, acesse <http://localhost:8080> com as credenciais:

```text
Usuário: gerente
Senha: 123
```

Para encerrar os contêineres:

```sh
docker compose down
```

## Como executar os testes unitários

Com o Docker em funcionamento, execute somente as quatro suítes da entrega:

```sh
docker compose run --rm --no-deps pdv-app mvn clean test "-Dtest=VendaServiceTest,CaixaServiceTest,RecebimentoServiceTest,NotaFiscalItemServiceTest"
```

O relatório de cobertura é gerado em:

```text
target/site/jacoco/index.html
```

## Documentação da entrega

- [Plano de Testes](https://docs.google.com/document/d/1UhuZOBFoVzsn5rlR_CHp07ZcGQoJGE8A_z19Ww1Lje4/edit?usp=sharing)
- [Registro de uso de IA](docs/ai/AI-LOG.md)
- [Pull requests do projeto](https://github.com/cmrgsPedro/pdv-qa/pulls?q=is%3Apr)
- Apresentação: será adicionada ao repositório após a consolidação dos slides.

## Repositórios utilizados

| Sistema | Repositório do grupo/origem | Uso no trabalho |
|---|---|---|
| PDV | [Fork do grupo](https://github.com/cmrgsPedro/pdv-qa) · [Projeto original](https://github.com/repo-software-testing-courses/pdv) | Testes unitários |
| webapp-1 | [Projeto original](https://github.com/repo-software-testing-courses/webapp-1) | Testes manuais e TestLink |

## Tecnologias e ferramentas

- Java 8, Spring Boot, Maven, JUnit 4, Mockito e JaCoCo;
- MySQL 8, Flyway, Docker e Docker Compose;
- TestLink para gerenciamento e execução do caso de teste de sistema;
- GitHub para versionamento, revisão por pull request, documentação e registro de defeitos.
