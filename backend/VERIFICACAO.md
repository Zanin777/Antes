# Verificação da entrega — 26/09/2026

**56 testes executados, zero falhas, zero erros e zero testes ignorados.**

| Suíte | Casos | Resultado |
|---|---:|---|
| `AntesApiTest` | 11 | Passou |
| `AvaliacaoPercebidaTest` | 16 | Passou |
| `ComparacaoServiceTest` | 9 | Passou |
| `PosTesteServiceTest` | 16 | Passou |
| `PreTesteServiceTest` | 4 | Passou |

Os 11 testes de integração inicializam Spring Boot, Hibernate, Flyway e H2, usam requisições HTTP simuladas com MockMvc e verificam registros persistidos. Incluem dois envios simultâneos de pré-teste e uma falha de constraint provocada no banco depois de inserir o pós-teste, verificando o rollback completo.

As duas migrações SQL também foram executadas diretamente no H2 2.3.232 em modo PostgreSQL, sem erro. Na integração, o Flyway aplicou as migrações, o Hibernate validou o schema e o catálogo retornou os oito templates.

O JAR executável foi empacotado com sucesso e iniciado em Java 21 com H2 em memória. Uma requisição HTTP real a `GET /templates` retornou os oito registros. O servidor usado nessa verificação foi encerrado depois do teste. Os resumos das cinco suítes estão em [verificacao/](verificacao/).

## Ambiente e método

- Runtime: Eclipse Temurin 21.0.12.1, Windows.
- Spring Boot 3.5.16; Maven 3.9.16; Surefire 3.5.6; H2 2.3.232.
- As restrições de acesso a metadados de caminhos deste ambiente impediram a compilação pelo `javac` do Maven. As mesmas fontes principais e de teste foram compiladas com Eclipse Compiler for Java 3.36.0, com `source/target 21`, nomes de parâmetros e UTF-8; depois, a suíte foi executada pelo Maven Surefire em Java 21.
- O Surefire terminou com `BUILD SUCCESS` e os resultados acima, embora tenha registrado um aviso de acesso à sua pasta temporária. Os cinco relatórios XML confirmam todos os casos executados.
- O projeto entregue mantém o compilador padrão do Maven e o wrapper oficial. O comando completo `mvnw test` com `javac` não foi validado neste ambiente restrito; deve ser executado no ambiente local de desenvolvimento.

## O que continua sem verificação

O perfil `prod`, o driver e o suporte Flyway para PostgreSQL estão configurados, mas não houve execução contra uma instância real de PostgreSQL. O modo PostgreSQL do H2 não substitui essa validação. Autenticação, frontend e integração com o protótipo visual não fazem parte desta entrega.

Para repetir a suíte no ambiente local: `./mvnw.cmd test` no Windows ou `sh mvnw test` no Linux/macOS, na pasta `backend`, com JDK 21 configurado.
