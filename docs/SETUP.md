# Setup do ambiente — COS482 (grupo)

Sistema escolhido: **pdv** (ERP web em Java 8 / Spring Boot 2.0 + Thymeleaf 3 + MySQL 8 + Flyway), fork de
`repo-software-testing-courses/pdv`.

Tudo roda em Docker. **Não é preciso instalar Java, Maven nem MySQL** na máquina.

## 1. Pré-requisitos

| Ferramenta | Por quê | Onde |
|---|---|---|
| Docker Desktop | roda app, banco, Maven e testes | https://docs.docker.com/desktop/ |
| JDK 8 local | **só** se quiser autocomplete/debug na IDE. Os testes não precisam. | Temurin 8 |

> O Docker Desktop precisa estar **aberto** antes de qualquer comando `docker`.

## 2. Clonar

```sh
git clone https://github.com/drbarros2004/pdv-QoS.git
cd pdv-QoS
```

## 3. Comandos do dia a dia

Não há script de atalho: são comandos `docker compose` diretos, para o grupo entender
o que roda por trás. Todos a partir da raiz do projeto, com o Docker Desktop aberto.

```sh
# sobe aplicação + banco (http://localhost:8080)
docker compose up -d

# acompanha o log até aparecer a linha "Started PdvApplication"
docker compose logs -f pdv-app

# roda os testes (sobe o banco sozinho se precisar)
docker compose run --rm pdv-app mvn test

# bash dentro do container, com mvn e java disponíveis
docker compose run --rm pdv-app bash

# derruba tudo
docker compose down

# apaga o banco e recria do zero (as migrações do Flyway rodam de novo)
docker compose down -v && docker compose up -d
```

Login do sistema: usuário `gerente`, senha `123`.

O `docker compose up` roda `mvn package -DskipTests` antes de iniciar a aplicação, o que
na primeira vez baixa todas as dependências Maven: medimos ~6 min do zero absoluto
(sem imagem e sem cache) até a aplicação responder. Depois fica rápido — o `~/.m2`
fica guardado no volume `m2`.

## 4. Onde escrever os testes

```
src/test/java/net/originmobi/pdv/service/VendaServiceTest.java   # espelha o pacote da classe testada
```

JUnit 4 e Mockito **já vêm** no `spring-boot-starter-test`; não precisa mexer no `pom.xml`.

Testes unitários devem ser puros (Mockito, sem `@SpringBootTest`), para não depender do banco.

## 5. Divisão das classes (1 classe de alta complexidade por integrante)

O requisito é complexidade ciclomática ≥ 10 por classe. Ranking por pontos de decisão
(`if`, `for`, `while`, `case`, `catch`, `&&`, `||`, `?`), medido em 03/09/2026:

| Classe | CC aprox. | Responsável |
|---|---|---|
| `service/VendaService.java` | 29 | |
| `service/CaixaService.java` | 28 | |
| `service/RecebimentoService.java` | 24 | |
| `service/notafiscal/NotaFiscalItemService.java` | 24 | |
| `service/notafiscal/NotaFiscalService.java` | 16 | |
| `service/cartao/CartaoLancamentoService.java` | 16 | |
| `service/CaixaLancamentoService.java` | 16 | |
| `service/PagarService.java` | 10 | |

OBS.: seria bom confirmar os números com uma ferramenta oficial (SonarQube ou uma das outras) antes
de fechar o Plano de Teste.

## 6. Responsabilidades do grupo

| Integrante | GitHub | Classe unitária | Funcionalidade (teste manual) | Outros artefatos |
|---|---|---|---|---|
| | | | | |

## 7. Convenções

- Branch por tarefa: `test/venda-service`, `docs/plano-de-teste`. Nada direto na `master`.
- PR para `master` com pelo menos 1 revisão de outro integrante.
- **Todos os artefatos entregues precisam estar na `master`** e linkados no `README.md`.
- Bugs encontrados viram **GitHub Issues** neste repositório (é o bug tracker da Entrega 1).
- Documentos de texto no **Google Docs**, cada um logado na própria conta. Só os links vão no README.

## 8. Ferramental por entrega

Disponível sem fazer nada: JUnit 4 e Mockito (vêm do `spring-boot-starter-test`) e o ambiente Docker.

| Entrega | Ferramenta | Quando adicionar |
|---|---|---|
| 1 | TestLink (casos manuais) | conta/instância antes de 11/10 |
| 1 | GitHub Issues | já disponível |
| 2 | PIT (`pitest-maven`) — escore de mutação | adicionar ao `pom.xml` na Entrega 2 |
| 2 | SonarQube (`sonar-scanner` ou SonarCloud) | Entrega 2 |
| 2 | Selenium WebDriver — testes E2E | Entrega 2 |

## 9. Prazos

| Marco | Data |
|---|---|
| Formação do grupo e escolha do sistema | 03/09/2026 |
| Repositório do grupo + fork | 10/09/2026 |
| **Entrega 1** (20%) | 11/10/2026, 23h59 |
| Apresentação da Entrega 1 | 13/10 e 15/10 |
| **Entrega 2** (30%) | 06/12/2026, 23h59 |
| Apresentação final | 08/12, 10/12 e 15/12 |
