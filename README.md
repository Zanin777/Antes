# Antes — backend v1 e protótipo visual 0.2

O núcleo do TCC está em [backend](backend/README.md): Java 21, Spring Boot, JPA, Flyway, questionários pré/pós e comparação automática. Veja [os exemplos da API](backend/exemplos/fluxo.http) e os [questionários atualizados](docs/questionarios-pre-pos-v1.txt). O backend ainda não está conectado às telas.

As seções abaixo descrevem exclusivamente o protótipo visual anterior, preservado em `dist/`.

## Protótipo visual 0.2

Aplicativo de apoio a decisões com cinco telas. O experimento agora reúne prática, flashcards, quiz e material próprio.

## Abrir e editar

Abra dist/index.html no navegador ou sirva a pasta dist com Live Server no VS Code. Não é necessário instalar bibliotecas nem compilar o aplicativo. Os dados ficam neste navegador e endereço. Abrir por outro endereço ou como arquivo local cria um espaço separado. Ainda não há login, sincronização, backend, MySQL ou pagamentos.

## Novidades

- Tema claro/escuro com preferência salva. Na primeira abertura, segue o sistema.
- Cronômetro com pausa e retomada; o tempo deixa de ser editável.
- Sessões sequenciais: a próxima abre 24 horas depois de concluir a anterior.
- Conclusão exige tempo planejado, três flashcards distintos, quiz com pelo menos 70% e relato com no mínimo 30 caracteres.
- Flashcards com resposta revelável e autoavaliação. Erros voltam em 10 minutos; acertos têm intervalos de 1, 3 e 7 dias. Revisões antecipadas não aumentam o nível.
- Quizzes com até cinco perguntas, alternativas embaralhadas, correção e explicações. Novas tentativas priorizam erros da anterior.
- Sugestões de prática, avaliação da experiência e histórico de relatos.
- Importação de perguntas e respostas próprias para criar cartões e questões.

## Conteúdos

O nome da decisão seleciona um banco introdutório: guitarra/violão, JavaScript, Java, Python, SQL/MySQL, inglês ou HTML/CSS. Os bancos são locais, editáveis em dist/learning.js, e incluem referências. Não há geração por IA nem cobertura automática de todo assunto.

Exemplo: **Aprender a tocar guitarra** inclui casas, semitons, afinação, pentatônica menor de Lá, tablatura e metrônomo.

Para outros assuntos, use **Experimento → Meu material**. Escreva uma pergunta e resposta por linha, separadas por |. Adicione pelo menos quatro cartões com perguntas e respostas distintas para montar um quiz. As outras respostas viram alternativas; o aplicativo não verifica a correção do conteúdo fornecido.

## Fluxo da sessão

1. Inicie o cronômetro e pratique. Pode estudar cartões enquanto ele conta.
2. Revele e avalie pelo menos três cartões diferentes após iniciar a prática.
3. Faça o quiz. Quizzes iniciados antes da prática são livres e não contam retroativamente.
4. Registre o que fez, as dificuldades e como se sentiu.
5. Conclua a sessão quando as quatro etapas estiverem cumpridas. Cartões e quizzes livres continuam disponíveis no intervalo de 24 horas.

O cronômetro usa tempo monotônico enquanto a aba está aberta. Fechar/recarregar pausa sem creditar o período fora do aplicativo. Interrupções de execução acima de 90 segundos são descartadas e pausam a contagem. Navegadores modernos impedem duas abas de rodarem o cronômetro simultaneamente via Web Locks; onde a API não existe, há um aviso para usar uma aba.

As regras evitam a conclusão por cliques na interface. Este protótipo local não comprova prática real e não resiste à edição deliberada dos dados ou relógio. Validação contra adulteração exigiria backend e horários confiáveis do servidor.

## Dados anteriores

O aplicativo lê antes-v1 e preserva as sessões antigas em histórico separado, sem convertê-las em sessões aprovadas pelo novo fluxo. Novos registros usam antes-v2; os dados anteriores não são apagados. A reflexão e a escolha anterior também são mantidas nos dados migrados.

## Estrutura do código

| Arquivo | Responsabilidade |
|---|---|
| dist/index.html | Estrutura e navegação |
| dist/styles.css | Layout original |
| dist/study.css | Temas e área de aprendizado |
| dist/theme.js | Preferência de tema |
| dist/app.js | Estado, telas principais e formulários |
| dist/learning.js | Conteúdo e regras de aprendizado |
| dist/study.js | Cronômetro, cartões, quizzes e interações |
| tests/learning.test.cjs | Testes das regras |

Com Node.js instalado, execute na pasta do projeto: **node --test tests/learning.test.cjs**.

As 12 verificações cobrem migração, reconhecimento de assunto, bloqueios, intervalo de 24 horas, cronômetro, revisão espaçada, correção e importação. Também foram conferidos no navegador: tema, cartões, quiz, material próprio e pausa após recarregar.

Próximas evoluções possíveis: múltiplas decisões, horários exatos, backend Java/MySQL e IA com credenciais no servidor. Ainda não implementadas.

