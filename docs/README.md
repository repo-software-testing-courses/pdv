# Documentação — Trabalho de Qualidade e Teste

Índice central dos artefatos do trabalho. Todo artefato entregue deve estar
referenciado aqui (ou no [README da raiz](../README.md)); artefato não
referenciado não é avaliado.

## Estrutura

| Diretório / arquivo | Conteúdo |
| --- | --- |
| [`plano-de-teste.md`](plano-de-teste.md) | Plano de Teste: escopo, estratégia, ferramentas, critérios de entrada/saída |
| [`relatorios/`](relatorios/) | Relatórios de cobertura (JaCoCo), mutação (PIT), inspeção estática (SonarQube) e relatórios de testes manuais |
| [`relatorios/mutacao-pit/`](relatorios/mutacao-pit/) | Teste de mutação (PIT): escore, print e análise dos sobreviventes |
| [`relatorios/relatorio-testes-manuais-vendaservice.md`](relatorios/relatorio-testes-manuais-vendaservice.md) | Relatório de testes manuais de `VendaService` (fechamento de venda), com evidências |
| [`defeitos/`](defeitos/) | Fichas de defeitos encontrados durante os testes |
| [`ai/AI-LOG.md`](ai/AI-LOG.md) | Registro obrigatório do uso de Inteligência Artificial |
| [`ai/transcricoes/`](ai/transcricoes/) | Conversas, prompts e configurações completas de ferramentas de IA |
| [`apresentacoes/`](apresentacoes/) | Slides das apresentações parcial e final |

## Defeitos registrados

Fichas dos defeitos encontrados nos testes (o rastreador oficial é o GitHub
Issues; estas fichas documentam a análise e as evidências).

| Defeito | Módulo | Título | Ficha | Issue |
| --- | --- | --- | --- | --- |
| DEF-VENDA-001 | Venda | Fechamento sem título lança "Zero length string" | [ficha](defeitos/DEF-VENDA-001-zero-length-string.md) | [#11](https://github.com/elojas2/trabalho-pdv-2026.1/issues/11) |
| DEF-VENDA-002 | Venda | Cartão débito/crédito não fecha a venda (NPE) | [ficha](defeitos/DEF-VENDA-002-cartao-nao-fecha.md) | [#12](https://github.com/elojas2/trabalho-pdv-2026.1/issues/12) |
| DEF-VENDA-003 | Venda | Desconto e acréscimo aplicados trocados | [ficha](defeitos/DEF-VENDA-003-desconto-acrescimo-trocados.md) | [#13](https://github.com/elojas2/trabalho-pdv-2026.1/issues/13) |
| DEF-CAIXA-001 | Caixa | Fechar caixa já fechado exibe alerta vazio | [ficha](defeitos/DEF-CAIXA-001-fechamento-duplicado-mensagem-vazia.md) | [#15](https://github.com/elojas2/trabalho-pdv-2026.1/issues/15) |
| DEF-ESTOQUE-001 | Estoque | Entrada de estoque não aumenta o saldo (trigger) | [ficha](defeitos/DEF-ESTOQUE-001-entrada-nao-aumenta-saldo.md) | [#14](https://github.com/elojas2/trabalho-pdv-2026.1/issues/14) |

Defeitos expostos pelos testes unitários (sem ficha própria; documentados nos
comentários de `VendaServiceTest`): issues
[#7](https://github.com/elojas2/trabalho-pdv-2026.1/issues/7),
[#8](https://github.com/elojas2/trabalho-pdv-2026.1/issues/8),
[#9](https://github.com/elojas2/trabalho-pdv-2026.1/issues/9) e
[#10](https://github.com/elojas2/trabalho-pdv-2026.1/issues/10). Planejamento
completo em [`defeitos/planejamento-issues.md`](defeitos/planejamento-issues.md).

## Documentos editáveis (Google Docs)

Os documentos de texto são elaborados no Google Docs, com todos os integrantes
logados, para que a colaboração individual fique registrada no histórico de
versões.

| Documento | Link |
| --- | --- |
| Plano de Teste | [Link do google docs](https://docs.google.com/document/d/1b40d0Y7q8RmNOTqsJ-dV7mlSIBAJlqY2Q8F_PGl12M0/edit?usp=sharing) |
| Casos de teste | [Link do google docs](https://docs.google.com/document/d/1cpsUYGmLNs6N2mYzDVKepaeZyxAlKNn6bANoqa1tXVU/edit?usp=sharing) |
| Relatório de inspeção de código | _adicionar link_ |
| Medidas ISO/IEC 25010 | _adicionar link_ |

## Entregas

| Entrega | Peso | Prazo | Status |
| --- | --- | --- | --- |
| Entrega 1 | 3 | 23/09/2026 | em andamento |
| Entrega 2 | 5 | _a definir_ | não iniciada |

## Equipe e responsabilidades

| Integrante | GitHub | Responsabilidades | Classes sob teste |
| --- | --- | --- | --- |
| Eloyse Fernanda | [@elojas2](https://github.com/elojas2) | Testes unitários e manuais | CaixaService |
| Natalia de Abreu Lamas | [@natalialamas73](https://github.com/natalialamas73) | Testes unitários e manuais | AjusteService |
| Alexandre Porto | [@AlexPortoNascimento](https://github.com/AlexPortoNascimento) | Testes unitários e manuais | VendaService |
