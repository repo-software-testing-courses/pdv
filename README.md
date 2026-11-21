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
| Testes unitários com dependências isoladas | [`CaixaServiceTest`](src/test/java/net/originmobi/pdv/CaixaServiceTest.java) — 43 casos (Mockito, `mockStatic`, `mockConstruction`) | [`VendaServiceTest`](src/test/java/net/originmobi/pdv/VendaServiceTest.java) | [`AjusteServiceTest`](src/test/java/net/originmobi/pdv/AjusteServiceTest.java) — 19 casos (Mockito, `mockStatic`; 1 defeito: [#28](https://github.com/elojas2/trabalho-pdv-2026.1/issues/28)) |
| Testes de integração | [`CaixaServiceIT`](src/test/java/net/originmobi/pdv/CaixaServiceIT.java) — 12 casos, MySQL real (2 defeitos: [#24](https://github.com/elojas2/trabalho-pdv-2026.1/issues/24), [#25](https://github.com/elojas2/trabalho-pdv-2026.1/issues/25)) | [`VendaServiceIT`](src/test/java/net/originmobi/pdv/VendaServiceIT.java) | [`AjusteServiceIT`](src/test/java/net/originmobi/pdv/AjusteServiceIT.java) — 7 casos, MySQL real (2 defeitos: [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14), [#27](https://github.com/elojas2/trabalho-pdv-2026.1/issues/27)) |
| Estrutural — todas-arestas ≥ 80% (JaCoCo) | **100%** (38/38) — [relatório](docs/relatorios/cobertura-jacoco/) | **89,5%** (34/38) — [relatório](docs/relatorios/cobertura-jacoco/) | **100%** (10/10) — [relatório](docs/relatorios/cobertura-jacoco/) |
| Baseada em defeitos — mutação ≥ 80% (PIT) | **100%** (55/55) — [relatório](docs/relatorios/mutacao-pit/) | **91%** (60/66) — [relatório](docs/relatorios/mutacao-pit/) | **100%** (17/17) — [relatório](docs/relatorios/mutacao-pit/) |
| Inspeção de código (Sonar) — antes e depois | **21 → 1** problemas — [relatório](docs/relatorios/inspecao-sonar/) | pendente | **13 → 1** problemas — [relatório](docs/relatorios/inspecao-sonar/) |
| Testes de sistema (Selenium) | pendente | pendente | [`AjusteEstoque.side`](src/test/selenium/AjusteEstoque.side) — Selenium IDE — [relatório](docs/relatorios/testes-sistema-selenium.md) |
| ISO/IEC 25010 | [Google Docs](https://docs.google.com/document/d/1bvbjeAyXI9Dq8uSdElpbEgbwPHDD9hZkLoyM-19vwG8/edit?usp=sharing) (documento do grupo) | ← | ← |
| Registro de uso de IA | [AI-012, AI-013, AI-016, AI-017](docs/ai/AI-LOG.md) | [AI-014, AI-015](docs/ai/AI-LOG.md) | [AI-011, AI-018, AI-019, AI-020](docs/ai/AI-LOG.md) |

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
| Testes de sistema (Selenium IDE) — `AjusteService` | [`docs/relatorios/testes-sistema-selenium.md`](docs/relatorios/testes-sistema-selenium.md) e [`src/test/selenium/`](src/test/selenium/) |
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
| DEF-VENDA-003 | Venda | Desconto e acréscimo aplicados trocados | [ficha](docs/defeitos/DEF-VENDA-003-desconto-acrescimo-trocados.md) | [#13](https://github.com/elojas2/trabalho-pdv-2026.1/issues/13) |
| DEF-CAIXA-001 | Caixa | Fechar caixa já fechado exibe alerta vazio | [ficha](docs/defeitos/DEF-CAIXA-001-fechamento-duplicado-mensagem-vazia.md) | [#15](https://github.com/elojas2/trabalho-pdv-2026.1/issues/15) |
| DEF-ESTOQUE-001 | Estoque | Entrada de estoque não aumenta o saldo (trigger) | [ficha](docs/defeitos/DEF-ESTOQUE-001-entrada-nao-aumenta-saldo.md) | [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14) |

Defeitos expostos pelos testes unitários (sem ficha própria; documentados nos
comentários de `VendaServiceTest`): issues
[#7](https://github.com/elojas2/trabalho-pdv-2026.1/issues/7),
[#8](https://github.com/elojas2/trabalho-pdv-2026.1/issues/8),
[#9](https://github.com/elojas2/trabalho-pdv-2026.1/issues/9) e
[#10](https://github.com/elojas2/trabalho-pdv-2026.1/issues/10). Planejamento
completo em [`docs/defeitos/planejamento-issues.md`](docs/defeitos/planejamento-issues.md).

Defeitos do `CaixaService` expostos pelos testes unitários:
[#20](https://github.com/elojas2/trabalho-pdv-2026.1/issues/20) e
[#21](https://github.com/elojas2/trabalho-pdv-2026.1/issues/21) (corrigidos na
Entrega 2) e [#22](https://github.com/elojas2/trabalho-pdv-2026.1/issues/22)
(aberto). Pelos testes de integração:
[#24](https://github.com/elojas2/trabalho-pdv-2026.1/issues/24) e
[#25](https://github.com/elojas2/trabalho-pdv-2026.1/issues/25) (abertos).

Defeitos do `AjusteService`: [#27](https://github.com/elojas2/trabalho-pdv-2026.1/issues/27) (ajuste de saída aumenta o saldo,
exposto pelo teste de integração `AjusteServiceIT` e pelo teste manual
CT-AJU-02) e [#28](https://github.com/elojas2/trabalho-pdv-2026.1/issues/28) (`processar` com código inexistente lança
`NoSuchElementException`, exposto pelo teste unitário). O `AjusteServiceIT` e
o teste manual CT-AJU-01 também confirmam, no módulo de ajuste,
[#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14).

## Recursos do sistema

- Cadastro de produtos, clientes e fornecedores
- Controle de estoque
- Gerenciamento de comandas
- Realização de vendas, inclusive com cartões
- Controle de fluxo de caixa
- Contas a pagar e a receber
- Permissões de usuários por grupo
- Cadastro de formas de pagamento
- Relatórios

## Tecnologias

| | |
| --- | --- |
| Linguagem | Java 8 |
| Framework | Spring Boot / Spring Framework 5, Spring Security |
| View | Thymeleaf 3 |
| Persistência | Hibernate (JPA), Flyway para migrações |
| Banco | MySQL 8 |
| Relatórios | JasperReports |
| Build | Maven (wrapper incluído) |
| Testes | JUnit, Spring Boot Test, JaCoCo |

## Executando com Docker (recomendado)

Requer Docker com Compose v2. Na raiz do projeto:

```sh
docker compose up -d
```

O Compose sobe dois serviços:

| Serviço | Portas | Descrição |
| --- | --- | --- |
| `pdv-app` | `8080` (app), `5005` (debug JDWP) | build Maven e execução do `.war` |
| `pdv-db` | `3306` | MySQL 8 com o banco `pdv` já criado |

O primeiro start baixa as dependências Maven e pode levar alguns minutos.
Acompanhe com:

```sh
docker compose logs -f pdv-app
```

A aplicação fica em <http://localhost:8080>. Para parar:

```sh
docker compose down          # mantém os dados
docker compose down -v       # remove também os volumes
```

A porta `5005` está aberta para debug remoto (JDWP), permitindo conectar a IDE
ao container.

## Executando localmente

1. Instale o JDK 8 e o MySQL 8.
2. Crie o banco e o usuário. O script está em [`README.txt`](README.txt):
   ```sql
   CREATE DATABASE pdv;
   CREATE USER 'pdv' IDENTIFIED BY 'kate456';
   GRANT CREATE, DELETE, INSERT, SELECT, UPDATE, REFERENCES, TRIGGER ON pdv.* TO pdv;
   FLUSH PRIVILEGES;
   ```
3. Ajuste as credenciais em `src/main/resources/application.properties`.
4. Suba a aplicação:
   ```sh
   ./mvnw spring-boot:run
   ```
   ou gere e execute o pacote:
   ```sh
   ./mvnw clean package
   java -jar target/pdv-0.0.1-SNAPSHOT.war
   ```

As migrações do Flyway são aplicadas automaticamente na subida.

## Acesso ao sistema

| Usuário | Senha |
| --- | --- |
| `gerente` | `123` |

> Credenciais padrão do projeto original, adequadas apenas para uso local.

## Testes e métricas

```sh
./mvnw test                  # executa a suíte de testes
./mvnw clean test            # testes + relatório de cobertura JaCoCo
```

O relatório de cobertura é gerado em `target/site/jacoco/index.html`.

Sem Maven instalado na máquina, os testes podem ser executados num container
Maven com JDK 8 (exemplo no PowerShell, a partir da raiz do projeto):

```sh
docker run --rm -v "${PWD}:/app" -v pdv-m2:/root/.m2 -w /app maven:3.8.8-eclipse-temurin-8 mvn clean test "-Dtest=AjusteServiceTest" "-Dmaven.test.failure.ignore=true"
```

Testes de integração quando a porta 3306 da máquina já está ocupada (por
exemplo, por um MySQL local): o banco sobe numa rede Docker própria, sem
publicar a porta, com o nome `pdv-db` esperado pela aplicação.

```sh
docker network create pdv-teste
docker run -d --name pdv-db-teste --network pdv-teste --network-alias pdv-db -e MYSQL_ROOT_PASSWORD=123456 -e "MYSQL_ROOT_HOST=%" -e MYSQL_DATABASE=pdv mysql:8.0
docker run --rm --network pdv-teste -v "${PWD}:/app" -v pdv-m2:/root/.m2 -w /app maven:3.8.8-eclipse-temurin-8 mvn test "-Dtest=AjusteServiceIT" "-Dmaven.test.failure.ignore=true"
```

Dentro do container:

```sh
docker compose exec pdv-app mvn test
```

## Estrutura do projeto

```
├── docs/                  # artefatos do trabalho
│   ├── plano-de-teste.md
│   ├── casos-de-teste-testlink.pdf
│   ├── defeitos/          # fichas de defeitos
│   ├── evidencias/        # prints dos testes manuais (venda/, caixa/, ajuste/)
│   ├── relatorios/        # testes manuais e de sistema, cobertura (JaCoCo), mutação (PIT) e Sonar
│   ├── ai/                # AI-LOG.md e transcrições
│   └── apresentacoes/     # slides
├── src/main/java/         # código-fonte da aplicação
├── src/main/resources/    # templates, estáticos e migrações Flyway
├── src/test/java/         # testes automatizados
├── src/test/selenium/     # testes de sistema (Selenium IDE, .side)
├── docker-compose.yml     # app + MySQL
├── Dockerfile             # imagem de build/execução
└── pom.xml                # dependências e plugins Maven
```

## Licença

Distribuído sob a licença Apache 2.0 — ver [`LICENSE`](LICENSE).