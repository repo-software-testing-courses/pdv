# COS482 — Trabalho Prático (2026.2)

Aplicar os conceitos de qualidade e teste de software em até dois softwares livres à escolha do grupo. O trabalho atravessa o semestre inteiro: cada técnica apresentada em aula é aplicada sobre o sistema escolhido, de forma incremental.

## Cronograma do trabalho

| Marco | Data |
|---|---|
| Formação dos grupos e escolha do(s) sistema(s) | até 03/09 (aula 8) |
| Criar o repositório do grupo e fazer fork do(s) sistema(s) | até 10/09 |
| Entrega 1 (20%) | até 11/10, domingo, 23h59 |
| Apresentação da Entrega 1 | 13/10 e 15/10 |
| Aula de acompanhamento, dúvidas da Entrega 2 | 26/11 (aula 28) |
| Plantão do trabalho final | 03/12 (aula 30) |
| Entrega 2 (30%) | até 06/12, domingo, 23h59 |
| Apresentação final | 08/12, 10/12 e 15/12 |

## Grupos

- 3 a 5 pessoas. As responsabilidades de cada integrante devem ser documentadas e registradas.
- O professor deve ser consultado antes da escolha do sistema, para avaliar o grau de dificuldade e a aderência ao escopo da disciplina.
- Sugestão de repositórios: https://github.com/orgs/repo-software-testing-courses/repositories

## Requisitos do software escolhido

**Obrigatório**

- Código-fonte disponível.
- Ao menos um dos projetos não pode ser composto só de funcionalidades simples (formulários de cadastro sem algoritmo). Precisa ter classes com desvios, laços e estruturas de controle.
- Um dos projetos deve ter ao menos uma classe de alta complexidade por integrante do grupo, com complexidade ciclomática mínima de 10 por classe sob teste.

**Desejável**

- Um dos projetos ser um sistema web.

> A aula 5 (25/08) apresenta complexidade ciclomática e as demais métricas de produto, uma semana antes do prazo de escolha do sistema. Use essas métricas para avaliar os candidatos antes de fechar com o professor.

## Linguagem do projeto

**Leia antes de escolher.** No trabalho, o grupo escolhe a linguagem, com uma condição explícita.

O suporte do professor pode se limitar a Java. Se o grupo escolher outra linguagem, assume a responsabilidade de encontrar, configurar e validar o ferramental equivalente. Problemas de ferramenta em linguagem não-Java não são justificativa para atraso ou para requisito não cumprido.

## Entrega 1, peso 20% da nota final

1. Descrição do escopo do(s) sistema(s), indicando quais módulos e componentes serão testados. O escopo entra no Plano de Teste.
2. Código-fonte original (fork).
3. Casos de teste unitários de pelo menos uma classe por integrante. Não pode ser CRUD de entidade, e precisa ter complexidade razoável.
4. Plano de Teste, usando o template apresentado em aula (IEEE 829). Deve incluir os artefatos que serão gerados e as ferramentas que serão usadas.
5. Casos de teste manuais projetados e executados, ao menos uma funcionalidade por integrante. Uso do TestLink em pelo menos um cenário, podendo o grupo optar por outra ferramenta. Os demais casos podem ser documentados em texto ou planilha.
6. Issues reportadas para os problemas encontrados, em um bug tracker à escolha (GitHub Issues, Mantis, Bugzilla).

## Entrega 2, peso 30% da nota final

1. Melhorar e ampliar os testes unitários, isolando dependências (Mockito).
2. Testes de integração.
3. Medidas dos atributos de qualidade da ISO/IEC 25010, em escala, com justificativa das decisões. As medidas devem indicar como o sistema deveria ser, não como está implementado.
4. Testes de sistema para requisitos funcionais, com Selenium ou similar e ao menos um teste E2E por integrante. Opcionalmente, ao menos um requisito não funcional (desempenho, segurança).
5. Projetar e melhorar o conjunto de casos de teste (unidade, integração e sistema) com as três técnicas:
   - Funcional
   - Estrutural, com ao menos 80% de cobertura no critério todas-arestas em pelo menos uma classe de alta complexidade por integrante
   - Baseada em defeitos, com ao menos 80% de escore de mutação nas mesmas classes
6. Relatório de inspeção do código-fonte (por exemplo, SonarQube). Executar a ferramenta, enviar print, resolver os problemas de pelo menos uma classe por integrante e enviar o print depois das correções.

## Regras de entrega

- Entrega pelo Moodle e no repositório GitHub do grupo.
- Todos os artefatos na branch principal (`main` ou `master`). Artefatos em outras branches ou em outros repositórios não são considerados.
- O `README.md` do repositório deve ter links diretos para todos os artefatos (código, documentos, diagramas). Artefato não referenciado ou difícil de localizar não é avaliado.
- Documentos de texto no Google Docs, com todos os integrantes logados nas próprias contas, de forma que a colaboração individual seja visível no histórico. Os links vão no `README.md`.
- Prazo não cumprido invalida a entrega.

## Como o trabalho é avaliado

**Nota do trabalho = (0,20 · T1 + 0,30 · T2) · AP**

Critérios:

- Uso adequado dos conceitos de qualidade e teste vistos em aula.
- Artefatos, avaliados pela completude, corretude e capacidade de argumentar as decisões tomadas.
- Colaboração individual, medida pela contribuição no GitHub e pela descrição documentada das responsabilidades.
- Apresentação e corretude das respostas individuais durante as apresentações.

AP é um valor entre 0 e 1, atribuído individualmente. As consequências diretas são estas.

- Não apresentar significa nota 0 no trabalho.
- Sem contribuição verificável no GitHub, nota 0 no trabalho.
- Numa mesma equipe, um integrante pode ficar com 9,0 e outro com 5,0.
- O nível de dificuldade do projeto escolhido entra na composição da nota, mais um motivo para consultar o professor antes de fechar a escolha.

## Formato das apresentações

Estimativa para cerca de 50 alunos em 10 grupos. Os tempos exatos serão confirmados quando a lista de matriculados sair do SIGA.

| | Aulas | Formato |
|---|---|---|
| Entrega 1 | 13 e 15/10 | 15 min de apresentação e 5 min de perguntas |
| Final | 08, 10 e 15/12 | 20 min de apresentação e 10 min de perguntas |

Na apresentação final, cada integrante responde questões feitas pelo professor. É dessa arguição que sai o AP.

Slides e material de apresentação devem ser enviados pelo Moodle antes da aula da apresentação
