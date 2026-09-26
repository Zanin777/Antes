# Antes — API v1

Núcleo do TCC para decisões de hobby/curso: escolher um template, registrar expectativas, experimentar e comparar com a experiência. Java 21, Spring Boot 3.5.16, Spring Data JPA, Bean Validation e Flyway. H2 no desenvolvimento e PostgreSQL no perfil `prod`.

## Executar

Instale um **JDK 21** e configure `JAVA_HOME`. Na pasta `backend`, no PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

No Linux/macOS: `sh mvnw test` e `sh mvnw spring-boot:run`. A primeira execução baixa o Maven e as dependências. Com Maven 3.9 instalado, também é possível usar `mvn test` e `mvn spring-boot:run`.

A API atende em `http://127.0.0.1:8080`. Consulte `GET /templates` para ver os oito templates. O H2 é persistido em `backend/data/antes.mv.db`; não há console web habilitado. O Flyway cria o schema e o catálogo; o Hibernate apenas valida as tabelas (`ddl-auto=validate`).

Para empacotar: `./mvnw.cmd package`. Depois: `java -jar target/antes-api-1.0.0-SNAPSHOT.jar`.

## PostgreSQL

Crie um banco vazio e informe as credenciais por variáveis de ambiente; não grave senhas no código:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'prod'
$env:DB_URL = 'jdbc:postgresql://localhost:5432/antes'
$env:DB_USERNAME = 'antes'
$env:DB_PASSWORD = '<senha-do-banco>'
.\mvnw.cmd spring-boot:run
```

`SERVER_PORT` altera a porta e `SERVER_ADDRESS` altera o endereço de escuta. O padrão é local porque esta v1 não tem autenticação nem autorização entre participantes. `participanteCodigo` identifica registros; não é credencial de acesso.

## Fluxo e contrato

Os corpos completos estão em [exemplos/fluxo.http](exemplos/fluxo.http). Datas usam `AAAA-MM-DD`; instantes de resposta vêm do servidor em UTC. A validação de datas do diário usa `America/Sao_Paulo`. Dinheiro usa JSON numérico com até duas casas decimais e é armazenado em `BigDecimal`/`numeric(12,2)`.

| Operação | Método e caminho |
|---|---|
| Consultar catálogo | `GET /templates` |
| Criar/consultar decisão | `POST /decisoes`, `GET /decisoes/{id}` |
| Criar experimento | `POST /experimentos` |
| Consultar/ajustar plano | `GET /experimentos/{id}`, `PATCH /experimentos/{id}` |
| Listar experimentos da decisão | `GET /decisoes/{id}/experimentos` |
| Enviar/consultar pré-teste | `POST` e `GET /experimentos/{id}/pre-teste` |
| Anotar/listar diário | `POST` e `GET /experimentos/{id}/registros-sessao` |
| Enviar/consultar pós-teste | `POST` e `GET /experimentos/{id}/pos-teste` |
| Consultar comparação | `GET /experimentos/{id}/comparacao` |

Criação retorna `201`. Dados inválidos retornam `400`, recurso inexistente `404`, operação indisponível no estado atual ou duplicação `409` e método não oferecido `405`. Erros usam `application/problem+json`, com `codigo` e, em falhas de Bean Validation, `campos`. Campos JSON desconhecidos são rejeitados para evitar ignorar respostas por erro de digitação.

### Regras implementadas

- O catálogo tem H01–H04 e C01–C04, versão 1, carregados por migração a partir dos documentos-fonte. Não oferece escrita por API. Para evoluir o catálogo, adicionar uma nova migração/versão; não reescrever migrações aplicadas.
- A criação copia **todos os campos** do template para o experimento, sem FK ao catálogo. `templateVersao` é opcional: omitido, seleciona a maior versão daquele código. Categoria incompatível é rejeitada. A resposta inclui `templateSnapshot`.
- A janela contém sete dias inclusivos: `dataFimPrevista = dataInicio + 6`.
- O pré-teste é imutável desde a criação. Um segundo envio retorna `409`; não há PUT/PATCH de pré-teste. PRE08 (`intencaoAntes`) é obrigatório.
- O PATCH do experimento recebe a descrição completa do plano, o material e a data de início. Só funciona em `PLANEJADO`. Preserva as expectativas e a cópia do plano guardada no pré-teste; `materialProvedorFormato` omitido fica nulo.
- O diário exige pré-teste, aceita datas dentro da janela até hoje e muda o estado para `EM_ANDAMENTO`. Pode registrar preparação sem prática. O número de registros não é usado como número de sessões e os totais do pós não sobrescrevem os registros originais.
- O pós exige pré-teste e é enviado uma única vez. Encerra o experimento e gera a comparação na **mesma transação**. Não há endpoint independente de encerramento nem escrita manual de comparação. Depois do pós, diário e plano ficam bloqueados.
- A criação do pré/pós, os ajustes e os registros de diário bloqueiam a mesma linha do experimento durante a transação. Restrições UNIQUE garantem um pré, um pós e uma comparação por experimento.

### Situações do pós-teste

| POS00 | Validação | Estado final |
|---|---|---|
| A | Uma ou mais sessões; realizadas ≥ esperadas | `CONCLUIDO` |
| B | 0 < realizadas < esperadas | `CONCLUIDO` |
| C | Uma ou mais sessões; motivo obrigatório | `INTERROMPIDO` |
| D | Zero sessões; motivo obrigatório; ambas as avaliações `NAO_SE_APLICA` | `NAO_INICIADO` |

Em A/B/C, dificuldade e satisfação exigem notas de 1 a 5. A data de interrupção é opcional, exclusiva de C, dentro da janela e não futura. Em D, preparação pode gerar tempo e gasto maiores que zero. A/B são avaliados contra `sessoesEsperadas` do pré original. Se houve mudanças posteriores, explicá-las em `contextoMudancas`.

A API aceita `"dificuldadePercebida": 3` ou `"dificuldadePercebida": "NAO_SE_APLICA"`. Não aceita `0`, `"3"`, `3.5` nem ausência do campo. O banco guarda `NIVEL_1` a `NIVEL_5` ou `NAO_SE_APLICA` como texto; ausência nunca vira zero.

Se o obstáculo for `O9`, `descricaoOutro` é obrigatório. As intenções pré/pós usam `CONTINUAR`, `ADAPTAR`, `TESTAR_DE_NOVO`, `ADIAR` e `NAO_SEI`.

### Comparação

Sessões, minutos e gasto: **realizado − esperado**, sem converter a diferença em uma avaliação de sucesso. Segurança: depois − antes.

Na dificuldade, nota maior gera `PIOROU` e menor gera `MELHOROU`. Na satisfação, a direção é inversa. Nota igual gera `MANTEVE`; ausência explícita gera `NAO_SE_APLICA`. Os rótulos descrevem a direção da escala ordinal, sem medir ganho de aprendizagem ou qualidade da decisão.

`obstaculoMudou` compara os códigos O0–O9; dois relatos O9 diferentes continuam com o mesmo código e devem ser analisados pelo texto. `transicaoIntencao` retorna `{antes, depois, mudou}`: não existe ordenação entre as intenções.

## Organização e modelo

`api`: DTOs, controller e erros. `domain`: entidades, enums e snapshot. `repository`: consultas JPA. `service`: transações e regras. `src/main/resources/db/migration`: schema e catálogo.

Relações: `Decisao 1:N Experimento`; `Experimento 1:0..1 PreTeste/PosTeste/Comparacao`; `Experimento 1:N RegistroSessao`. O template do experimento é um objeto embutido, sem vínculo vivo com `Template`. Respostas HTTP são DTOs, nunca entidades JPA.

Os questionários e textos dos templates estão em [../docs](../docs). A versão 1.1 do instrumento acrescenta PRE08 e explicita as sete entidades.

## Verificação e limites

A suíte inclui testes unitários dos serviços e do JSON das avaliações, além de testes de integração com MockMvc, H2, Flyway e transações reais. Ela cobre snapshot, categoria, pré imutável, D/ausência explícita, escalas, concorrência, diário e rollback do pós quando a comparação falha. Consulte [VERIFICACAO.md](VERIFICACAO.md) para o resultado efetivamente obtido nesta entrega.

Esta API usa autorrelato: não comprova prática, não impõe cronômetro nem espera obrigatória de sete dias antes do pós. Guarda os instantes de resposta; o protocolo de coleta define quando solicitar o encerramento. Sem agendador, um participante que abandone o app pode continuar sem pós-teste: somente o encerramento explícito exige respondê-lo.

Sem frontend novo, integração com o protótipo anterior, IA, calendário automático ou autenticação nesta etapa.
