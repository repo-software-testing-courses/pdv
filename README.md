# PDV — Sistema de ERP web

Sistema de ERP/ponto de venda desenvolvido em Java com Spring Boot, usado como
objeto de estudo no trabalho prático da disciplina **Qualidade e Teste**.

Este repositório é um fork do projeto original
[originmobi/pdv](https://github.com/originmobi/pdv), acrescido dos artefatos de
teste e qualidade produzidos pelo grupo.

## Equipe

| Integrante | GitHub |
| --- | --- |
| Eloyse Fernanda | [@elojas2](https://github.com/elojas2) |
| Natalia de Abreu Lamas | [@natalialamas73](https://github.com/natalialamas73) |
| Alexandre Porto | [@AlexPortoNascimento](https://github.com/AlexPortoNascimento) |

As responsabilidades de cada integrante e as classes sob teste estão em
[`docs/README.md`](docs/README.md).

## Documentos principais

| Documento | Versão no repositório | Versão editável |
| --- | --- | --- |
| **Plano de Teste** | [`docs/plano-de-teste.md`](docs/plano-de-teste.md) | [Google Docs](https://docs.google.com/document/d/1b40d0Y7q8RmNOTqsJ-dV7mlSIBAJlqY2Q8F_PGl12M0/edit?usp=sharing) |
| **Casos de Teste** | [`docs/TestLink 1.9.20 [DEV].pdf`](<docs/TestLink 1.9.20 [DEV].pdf>) (exportação do TestLink) | [Google Docs](https://docs.google.com/document/d/1cpsUYGmLNs6N2mYzDVKepaeZyxAlKNn6bANoqa1tXVU/edit?usp=sharing) |
| **Apresentação parcial (Entrega 1)** | [`docs/apresentacoes/`](docs/apresentacoes/) | [Google Slides](https://docs.google.com/presentation/d/1Vy5m1Brl0lf9eM_LRIxNzgr0ZdlR_FlJAifwIouKT-4/edit?usp=sharing) |

## Sumário

- [Equipe](#equipe)
- [Documentos principais](#documentos-principais)
- [Artefatos do trabalho](#artefatos-do-trabalho)
- [Recursos do sistema](#recursos-do-sistema)
- [Tecnologias](#tecnologias)
- [Executando com Docker (recomendado)](#executando-com-docker-recomendado)
- [Executando localmente](#executando-localmente)
- [Acesso ao sistema](#acesso-ao-sistema)
- [Testes e métricas](#testes-e-métricas)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Licença](#licença)

## Artefatos do trabalho

Todos os artefatos estão na branch principal (`master`), sob o diretório
[`docs/`](docs/). O índice completo, com os links dos documentos editáveis no
Google Docs, está em **[`docs/README.md`](docs/README.md)**.

| Artefato | Local |
| --- | --- |
| Índice geral da documentação | [`docs/README.md`](docs/README.md) |
| Plano de Teste | [`docs/plano-de-teste.md`](docs/plano-de-teste.md) |
| Casos de Teste (TestLink) | [`docs/TestLink 1.9.20 [DEV].pdf`](<docs/TestLink 1.9.20 [DEV].pdf>) |
| Relatórios (cobertura, mutação, inspeção) | [`docs/relatorios/`](docs/relatorios/) |
| Relatório de testes manuais — VendaService | [`docs/relatorios/relatorio-testes-manuais-vendaservice.md`](docs/relatorios/relatorio-testes-manuais-vendaservice.md) |
| Fichas de defeitos | [`docs/defeitos/`](docs/defeitos/) |
| **Registro de uso de IA** | [`docs/ai/AI-LOG.md`](docs/ai/AI-LOG.md) |
| Testes gerados por IA — solução inicial (v0) | [`docs/ai/transcricoes/AI-002-v0-CaixaServiceTest.java`](docs/ai/transcricoes/AI-002-v0-CaixaServiceTest.java) |
| Testes gerados por IA — descrição das alterações da revisão | [`docs/ai/transcricoes/AI-003-alteracoes-v0-para-final.md`](docs/ai/transcricoes/AI-003-alteracoes-v0-para-final.md) |
| Slides das apresentações | [`docs/apresentacoes/`](docs/apresentacoes/) · [Apresentação parcial no Google Slides](https://docs.google.com/presentation/d/1Vy5m1Brl0lf9eM_LRIxNzgr0ZdlR_FlJAifwIouKT-4/edit?usp=sharing) |
| Código dos testes automatizados | [`src/test/java/`](src/test/java/) |
| Defeitos reportados | [Issues do repositório](https://github.com/elojas2/trabalho-pdv-2026.1/issues) |

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

Dentro do container:

```sh
docker compose exec pdv-app mvn test
```

## Estrutura do projeto

```
├── docs/                  # artefatos do trabalho (ver docs/README.md)
├── src/main/java/         # código-fonte da aplicação
├── src/main/resources/    # templates, estáticos e migrações Flyway
├── src/test/java/         # testes automatizados
├── docker-compose.yml     # app + MySQL
├── Dockerfile             # imagem de build/execução
└── pom.xml                # dependências e plugins Maven
```

## Licença

Distribuído sob a licença Apache 2.0 — ver [`LICENSE`](LICENSE).
