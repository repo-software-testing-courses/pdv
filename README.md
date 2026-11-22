# PDV QA — Qualidade e Teste de Software

Repositório da **Entrega 1 e Entrega 2** do trabalho prático da disciplina de Qualidade e Teste de Software.

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
cd pdv
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

---

## Escopo da Entrega 2

Para a segunda entrega do trabalho da disciplina de Qualidade e Teste de Software, este repositório passou por uma reorganização e passou a incluir o projeto **Webapp-1**, além de separar os projetos em pastas diferentes.

## Objetivos alcançados

- Aprimoramento dos casos de teste unitários das classes anteriores, para atingir a meta de 80% de cobertura proposta pela professora.
- Revisão dos testes pelos membros da equipe, verificando se cada teste fazia sentido para o código testado.
- Testes de integração realizados com Postman.
- Testes de sistema E2E implementados, seguindo os testes manuais propostos anteriormente para o projeto Webapp-1.

## Como executar o Webapp-1

### Pré-requisitos

- Git
- Docker Desktop com Docker Compose v2
- Portas `8080`, `3306` e `5005` disponíveis

### Passo a passo

1. Clone o fork do grupo e acesse a pasta do projeto:

```sh
git clone https://github.com/cmrgsPedro/pdv-qa.git
cd pdv-qa/webapp-1/docker
```

2. Suba a aplicação (a primeira execução pode demorar, pois baixa as imagens e compila o projeto):

```sh
docker compose up --build
```

3. Após a inicialização, acesse <http://localhost:8080>.

4. Para encerrar os contêineres:

```sh
docker compose down
```

## Testes unitários após as melhorias — PDV
 
Foram acrescentados testes para eliminar mutantes sobreviventes e cobrir decisões ainda não exercitadas. Os testes da Entrega 1 foram mantidos.
 
| Classe | Integrante | Testes (Entrega 1 → Entrega 2) | Escore de mutação (Pitest) |
|---|---|---|---|
| `VendaService` | Pedro | 28 → xx | xxxxx |
| `CaixaService` | Arthur | 22 → 27 | 52/54 (96%) |
| `RecebimentoService` | Rodrigo | 21 → xx | xxxxx |
| `NotaFiscalItemService` | João Paulo | 17 → xx | xxxxx |
 
Os mutantes que sobreviveram são equivalentes (por exemplo, remoção de `System.out.println`, ou variáveis que nunca são lidas).
 
### Como gerar os relatórios
 
```sh
cd pdv-qa/pdv
mvn clean test "-Dtest=*ServiceTest"          # cobertura: target/site/jacoco/index.html
mvn org.pitest:pitest-maven:mutationCoverage  # mutação:  target/pit-reports/
```
 
O `PdvApplicationTests` carrega o contexto Spring e exige o MySQL ativo; por isso a execução acima roda apenas as suítes `*ServiceTest`.
 
## Testes de sistema (E2E) — webapp-1
 
Os testes usam Selenium (JUnit 5) e ficam em
[`webapp-1/TrackOrJargh/src/test/java/com/trackorjargh/e2e/`](webapp-1/TrackOrJargh/src/test/java/com/trackorjargh/e2e/).
 
### Automação dos casos manuais da Entrega 1
 
| Caso manual | Responsável | Teste automatizado |
|---|---|---|
| U1 — Visualizar comentários de uma série | Arthur | `TestE2EU1VisualizarComentarios` |

 
### Testes adicionais
 
| Teste | O que verifica |
|---|---|
| `TestE2ERegisterAndLogin` | Cadastro de usuário, ativação da conta e login (relacionado ao caso VU-2 do TestLink) |

 
### Como executar os testes E2E
 
**Pré-requisitos:** Docker Desktop, Java 8 e Maven. Portas `8000` e `3306` livres.
 
1. Suba o webapp-1:
```sh
cd pdv-qa/webapp-1/docker
docker compose up --build
```
 
Confirme que <http://localhost:8000> abre o TrackOrJargh.
 
2. Em outro terminal, rode os testes (requer o Google Chrome instalado):
```sh
cd pdv-qa/webapp-1/TrackOrJargh
mvn test -Dtest="TestE2E*"
```
 
**Sem Chrome instalado**, use um navegador em contêiner:
 
```sh
docker run -d -p 4444:4444 --shm-size=2g selenium/standalone-chrome
$env:ET_EUS_API=http://localhost:4444/wd/hub
$env:SUT_URL=http://host.docker.internal:8000/
mvn test -Dtest="TestE2E*"
```
 
No PowerShell, defina as variáveis com `$env:ET_EUS_API = "http://localhost:4444/wd/hub"` e `$env:SUT_URL = "http://host.docker.internal:8000/"`.
 
 
## Testes de integração
 
Link para o Postman e para o relatório de execução.
 