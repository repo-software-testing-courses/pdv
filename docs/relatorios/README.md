# Relatórios de qualidade

Coloque aqui os relatórios exportados e os prints das ferramentas.

| Arquivo esperado | Ferramenta | Observação |
| --- | --- | --- |
| `cobertura-jacoco/` ou `cobertura.pdf` | JaCoCo | critério todas-arestas (branch), meta ≥ 80% |
| [`mutacao-pit/`](mutacao-pit/) | PIT | escore de mutação ≥ 80% nas mesmas classes |
| `sonar-antes.png` | SonarQube | print da análise inicial |
| `sonar-depois.png` | SonarQube | print após as correções |
| `inspecao-codigo.md` | — | problemas encontrados, classes corrigidas e responsável |

Como gerar a cobertura localmente:

```sh
./mvnw clean test          # relatório em target/site/jacoco/index.html
```
