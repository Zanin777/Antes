/* Regras de aprendizado independentes da interface. Usadas também pelos testes. */
(function (root) {
  'use strict';
  const DAY = 24 * 60 * 60 * 1000;
  const normalize = text => String(text).normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
  const card = (id, question, answer, wrong, explanation = answer) => ({ id, question, answer, wrong, explanation });
  const packs = [
    {
      id: 'guitar', title: 'Guitarra e violão · primeiros passos', match: /\b(guitarra|violao|guitar)\b/,
      source: ['Fender · escalas para iniciantes', 'https://www.fender.com/articles/scales/5-essential-guitar-scales-for-beginners'],
      practices: ['Localize a pestana, os trastes e as casas. Toque uma corda solta e depois a primeira casa, lentamente.', 'Escolha duas notas na mesma corda e alterne entre elas, buscando um som limpo.', 'Explore as notas Lá, Dó, Ré, Mi e Sol da pentatônica menor de Lá. Priorize precisão, não velocidade.'],
      cards: [
        card('g1', 'O que são as casas da guitarra?', 'Os espaços do braço delimitados pelos trastes, onde pressionamos as cordas.', ['As peças usadas para afinar as cordas.', 'Os controles de volume do instrumento.', 'Os pontos onde o cabo é conectado.']),
        card('g2', 'Ao avançar uma casa na mesma corda, o som sobe quanto?', 'Um semitom.', ['Um tom inteiro.', 'Uma oitava.', 'Uma quinta justa.']),
        card('g3', 'Qual é a afinação padrão, da corda mais grave à mais aguda?', 'Mi, Lá, Ré, Sol, Si, Mi (E A D G B E).', ['Mi, Si, Sol, Ré, Lá, Mi (E B G D A E).', 'Dó, Ré, Mi, Fá, Sol, Lá (C D E F G A).', 'Lá, Ré, Sol, Dó, Mi, Lá (A D G C E A).']),
        card('g4', 'Quantas notas diferentes há em uma escala pentatônica?', 'Cinco notas por oitava.', ['Três notas por oitava.', 'Sete notas por oitava.', 'Doze notas por oitava.']),
        card('g5', 'Quais são as notas da pentatônica menor de Lá?', 'Lá, Dó, Ré, Mi e Sol (A C D E G).', ['Lá, Si, Dó, Ré e Mi (A B C D E).', 'Lá, Si, Dó sustenido, Mi e Fá sustenido (A B C# E F#).', 'Dó, Ré, Mi, Fá e Sol (C D E F G).'], 'A pentatônica menor usa os graus 1, terça menor, 4, 5 e sétima menor. Em Lá: A C D E G.'),
        card('g6', 'O que significa o número 0 em uma tablatura?', 'Tocar a corda solta, sem pressionar uma casa.', ['Não tocar nenhuma corda.', 'Tocar a décima casa.', 'Tocar todas as cordas juntas.']),
        card('g7', 'Qual é a função de um metrônomo?', 'Marcar uma pulsação regular para praticar o ritmo.', ['Afinar automaticamente todas as cordas.', 'Aumentar a distorção.', 'Escolher as notas de um acorde.'])
      ]
    },
    {
      id: 'javascript', title: 'JavaScript · fundamentos', match: /\b(javascript|java script|js)\b/,
      source: ['MDN · visão geral do JavaScript', 'https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Language_overview'],
      practices: ['Crie duas variáveis, some seus valores e mostre o resultado no console.', 'Escreva uma função que receba um nome e devolva uma saudação. Teste com dois nomes.', 'Crie um array de tarefas e use um laço para mostrar cada item.'],
      cards: [
        card('js1', 'Qual declaração permite reatribuir o valor de uma variável?', 'let.', ['const.', 'return.', 'import.']),
        card('js2', 'O que o operador === compara?', 'Valor e tipo, sem converter os tipos dos operandos.', ['Apenas o tamanho dos valores.', 'Valores sempre convertidos em texto.', 'Somente o endereço de uma página.']),
        card('js3', 'Para que serve um array?', 'Guardar uma coleção ordenada de valores.', ['Executar uma função automaticamente.', 'Estilizar uma página.', 'Criar exclusivamente números inteiros.']),
        card('js4', 'O que return faz dentro de uma função?', 'Encerra a execução da função e pode devolver um valor.', ['Repete a função para sempre.', 'Transforma o código em HTML.', 'Declara uma variável global.']),
        card('js5', 'Qual índice acessa o primeiro item de um array?', '0.', ['1.', '-1.', 'O tamanho do array.']),
        card('js6', 'O que addEventListener permite fazer?', 'Registrar uma função para responder a um evento.', ['Criar uma tabela SQL.', 'Importar uma folha de estilos.', 'Converter toda string em número.'])
      ]
    },
    {
      id: 'java', title: 'Java · fundamentos', match: /\bjava\b/,
      source: ['Dev.java · aprender Java', 'https://dev.java/learn/'],
      practices: ['Crie uma classe simples com um atributo e um método.', 'Escreva uma condição que compare dois números e imprima o maior.', 'Crie uma lista de nomes e percorra seus elementos.'],
      cards: [
        card('j1', 'O que uma classe descreve em Java?', 'A estrutura e os comportamentos de seus objetos.', ['Somente o layout de uma tela.', 'Um comando para encerrar o programa.', 'Uma conexão obrigatória com a internet.']),
        card('j2', 'Qual tipo primitivo representa verdadeiro ou falso?', 'boolean.', ['String.', 'double.', 'char.']),
        card('j3', 'Qual método costuma ser usado para comparar o conteúdo de duas Strings?', 'equals().', ['length().', 'toUpperCase().', 'substring().']),
        card('j4', 'O que new normalmente faz ao instanciar uma classe?', 'Cria uma nova instância e chama seu construtor.', ['Apaga todas as instâncias antigas.', 'Transforma a classe em uma interface.', 'Encerra a JVM.'])
      ]
    },
    {
      id: 'python', title: 'Python · fundamentos', match: /\bpython\b/,
      source: ['Python · tutorial oficial', 'https://docs.python.org/3/tutorial/'],
      practices: ['Crie uma lista de três itens e imprima cada um com um laço for.', 'Escreva uma função que receba dois números e retorne a soma.', 'Use uma condição para classificar um número como positivo, negativo ou zero.'],
      cards: [
        card('p1', 'Como os blocos de código são delimitados em Python?', 'Pela indentação.', ['Somente por chaves.', 'Por parênteses em todas as linhas.', 'Pela cor do texto no editor.']),
        card('p2', 'Qual palavra inicia a definição de uma função?', 'def.', ['function.', 'className.', 'make.']),
        card('p3', 'O que len([10, 20, 30]) retorna?', '3.', ['2.', '30.', '60.']),
        card('p4', 'O que um dicionário armazena?', 'Pares de chave e valor.', ['Apenas letras em ordem alfabética.', 'Somente números positivos.', 'Uma única função sem nome.'])
      ]
    },
    {
      id: 'sql', title: 'SQL · consultas básicas', match: /\b(sql|mysql|banco de dados)\b/,
      source: ['MySQL · tutorial', 'https://dev.mysql.com/doc/refman/8.4/en/tutorial.html'],
      practices: ['Desenhe uma tabela de livros com id, título e autor.', 'Escreva uma consulta SELECT com um filtro WHERE.', 'Escreva uma consulta que ordene os livros pelo título.'],
      cards: [
        card('s1', 'Qual comando consulta dados de uma tabela?', 'SELECT.', ['INSERT.', 'DROP.', 'UPDATE.']),
        card('s2', 'Para que serve WHERE em uma consulta?', 'Filtrar linhas por uma condição.', ['Renomear o banco de dados.', 'Criar todas as tabelas.', 'Definir a senha do usuário.']),
        card('s3', 'Qual é a função de uma chave primária?', 'Identificar unicamente cada linha da tabela.', ['Ordenar obrigatoriamente por nome.', 'Permitir valores duplicados sem restrição.', 'Armazenar apenas senhas.']),
        card('s4', 'Qual cláusula ordena o resultado de uma consulta?', 'ORDER BY.', ['GROUP ALL.', 'SORT TABLE.', 'FILTER BY.'])
      ]
    },
    {
      id: 'english', title: 'Inglês · primeiros passos', match: /\b(ingles|english)\b/,
      source: ['British Council · verbo to be', 'https://learnenglish.britishcouncil.org/grammar/a1-a2-grammar/present-simple-be'],
      practices: ['Apresente-se em voz alta usando três frases curtas.', 'Escreva cinco frases com I am, you are e she is.', 'Leia um diálogo curto e anote três palavras novas.'],
      cards: [
        card('e1', 'Como completar: I ___ a student?', 'am.', ['is.', 'are.', 'be.']),
        card('e2', 'Qual pronome corresponde a “nós”?', 'We.', ['They.', 'He.', 'She.']),
        card('e3', 'Como perguntar o nome de alguém?', 'What is your name?', ['Where is your name?', 'How old your name?', 'Who your name is?']),
        card('e4', 'Como completar: She ___ happy?', 'is.', ['am.', 'are.', 'be.'])
      ]
    },
    {
      id: 'web', title: 'HTML e CSS · primeiros passos', match: /\b(html|css|site|frontend|front-end)\b/,
      source: ['MDN · desenvolvimento web', 'https://developer.mozilla.org/en-US/docs/Learn_web_development'],
      practices: ['Crie uma página com um título, um parágrafo e um link.', 'Use CSS para mudar a cor, o tamanho do texto e o espaçamento.', 'Monte um formulário com rótulos associados aos campos.'],
      cards: [
        card('h1', 'Qual é a principal função do HTML?', 'Estruturar e dar significado ao conteúdo da página.', ['Guardar senhas no servidor.', 'Compilar código Java.', 'Substituir o banco de dados.']),
        card('h2', 'Para que serve o CSS?', 'Definir a apresentação visual da página.', ['Executar consultas SQL.', 'Validar uma identidade no servidor.', 'Transformar JavaScript em Java.']),
        card('h3', 'Qual elemento HTML cria um link?', 'a.', ['p.', 'strong.', 'section.']),
        card('h4', 'Qual propriedade CSS altera a cor do texto?', 'color.', ['font-size.', 'padding.', 'display.'])
      ]
    }
  ];
  function detect(name) { return packs.find(pack => pack.match.test(normalize(name))) || null; }
  function newSession() { return { done: false, elapsedMs: 0, actual: 0, reviewed: [], quiz: null, note: '', satisfaction: 3, completedAt: null }; }
  function migrate(data) {
    const copy = structuredClone(data);
    if (copy.schema !== 2) {
      copy.legacyHistory = copy.sessions.filter(s => s.done).map(s => ({ actual: s.actual }));
      copy.legacyChoice = copy.choice || '';
      copy.sessions = Array.from({ length: copy.frequency }, newSession);
      copy.choice = '';
    }
    copy.schema = 2;
    copy.customCards ||= [];
    copy.cardProgress ||= {};
    copy.attempts ||= [];
    return copy;
  }
  function validate(data) {
    return data && typeof data.name === 'string' && data.name.length <= 100 && typeof data.why === 'string'
      && typeof data.category === 'string' && Number.isInteger(data.frequency) && data.frequency >= 1 && data.frequency <= 7
      && Number.isFinite(data.minutes) && data.minutes >= 5 && data.minutes <= 240
      && Number.isFinite(data.cost) && data.cost >= 0 && Array.isArray(data.free) && data.free.length === 7
      && data.free.every(h => Number.isFinite(h) && h >= 0 && h <= 16)
      && Array.isArray(data.sessions) && data.sessions.length === data.frequency
      && data.sessions.every(s => s && typeof s.done === 'boolean' && Number.isFinite(s.actual) && s.actual >= 0
        && (data.schema !== 2 || (Number.isFinite(s.elapsedMs) && s.elapsedMs >= 0 && Array.isArray(s.reviewed) && typeof s.note === 'string')))
      && (data.schema !== 2 || (Array.isArray(data.customCards) && data.customCards.every(c => typeof c.id === 'string' && typeof c.question === 'string' && typeof c.answer === 'string' && Array.isArray(c.wrong)) && data.cardProgress && typeof data.cardProgress === 'object' && Array.isArray(data.attempts)));
  }
  function currentIndex(state) { return state.sessions.findIndex(s => !s.done); }
  function unlockAt(state, index) { return index <= 0 ? 0 : (state.sessions[index - 1].completedAt || Infinity) + DAY; }
  function canStart(state, index, now = Date.now()) { return index >= 0 && index === currentIndex(state) && now >= unlockAt(state, index); }
  function cardsFor(state) {
    const own = state.customCards || [];
    const custom = own.map(c => ({ ...c, wrong: own.filter(o => o.id !== c.id && o.answer !== c.answer).slice(0, 3).map(o => o.answer), explanation: c.answer }));
    return [...(detect(state.name)?.cards || []), ...custom];
  }
  function eligibleCards(state) { return cardsFor(state).filter(c => c.wrong.length >= 3); }
  function requirements(state, index, now = Date.now()) {
    if (!canStart(state, index, now)) return { ready: false, reason: 'Aguarde a liberação desta sessão.' };
    const s = state.sessions[index];
    const cards = cardsFor(state), count = Math.min(3, cards.length);
    const checks = [
      { key: 'time', label: `${state.minutes} minutos no cronômetro`, ok: s.elapsedMs >= state.minutes * 60000 },
      { key: 'cards', label: `${count || 3} flashcards diferentes revisados`, ok: count > 0 && new Set(s.reviewed.filter(id => cards.some(c => c.id === id))).size >= count },
      { key: 'quiz', label: 'Pelo menos 70% no quiz da sessão', ok: !!s.quiz && s.quiz.total >= 4 && s.quiz.correct / s.quiz.total >= .7 },
      { key: 'note', label: 'Relato com pelo menos 30 caracteres', ok: s.note.trim().length >= 30 }
    ];
    return { ready: checks.every(c => c.ok), checks, reason: checks.find(c => !c.ok)?.label || '' };
  }
  function complete(state, index, now = Date.now()) {
    const check = requirements(state, index, now);
    if (!check.ready) throw new Error(check.reason);
    const s = state.sessions[index];
    s.done = true; s.actual = Math.round(s.elapsedMs / 60000); s.completedAt = now;
    return s;
  }
  function addTime(state, index, elapsed) {
    if (index < 0 || !Number.isFinite(elapsed) || elapsed <= 0 || elapsed > 90000 || state.sessions[index].done) return false;
    state.sessions[index].elapsedMs += elapsed;
    state.sessions[index].actual = Math.round(state.sessions[index].elapsedMs / 60000);
    return true;
  }
  function scheduleCard(previous, remembered, now = Date.now()) {
    // Revisões antecipadas são prática livre, não aceleram a consolidação.
    if (remembered && previous && previous.dueAt > now) return { ...previous, reviewedAt: now };
    const level = remembered ? Math.min((previous?.level || 0) + 1, 3) : 0;
    const delay = remembered ? [0, 1, 3, 7][level] * DAY : 10 * 60000;
    return { level, dueAt: now + delay, reviewedAt: now };
  }
  function shuffle(items, random = Math.random) {
    const result = [...items];
    for (let i = result.length - 1; i > 0; i--) { const j = Math.floor(random() * (i + 1)); [result[i], result[j]] = [result[j], result[i]]; }
    return result;
  }
  function makeQuiz(state, random = Math.random) {
    const missed = new Set(state.attempts.at(-1)?.missed || []);
    const available = shuffle(eligibleCards(state), random).sort((a, b) => Number(missed.has(b.id)) - Number(missed.has(a.id)));
    return available.slice(0, 5).map(c => ({ ...c, options: shuffle([c.answer, ...c.wrong.slice(0, 3)], random) }));
  }
  function grade(questions, answers) {
    const missed = questions.filter((q, i) => answers[i] === undefined || answers[i] === null || q.options[Number(answers[i])] !== q.answer).map(q => q.id);
    return { correct: questions.length - missed.length, total: questions.length, missed };
  }
  function parseNotes(text, existing = []) {
    const lines = text.split('\n').map(s => s.trim()).filter(Boolean);
    if (!lines.length || lines.length > 30) throw new Error('Adicione de 1 a 30 linhas por vez.');
    const newCards = lines.map((line, i) => {
      const split = line.indexOf('|');
      if (split < 0) throw new Error(`Linha ${i + 1}: separe pergunta e resposta com |.`);
      const question = line.slice(0, split).trim(), answer = line.slice(split + 1).trim();
      if (question.length < 8 || answer.length < 3 || question.length > 300 || answer.length > 700) throw new Error(`Linha ${i + 1}: use uma pergunta de 8–300 caracteres e resposta de 3–700.`);
      return card(`custom-${Date.now()}-${i}`, question, answer, []);
    });
    const all = [...existing, ...newCards];
    if (all.length > 60) throw new Error('Este protótipo aceita até 60 cartões próprios por decisão.');
    if (new Set(all.map(c => normalize(c.question))).size !== all.length || new Set(all.map(c => normalize(c.answer))).size !== all.length) throw new Error('Use perguntas e respostas diferentes para montar alternativas distintas.');
    return newCards;
  }
  const api = { DAY, packs, detect, newSession, migrate, validate, currentIndex, unlockAt, canStart, cardsFor, eligibleCards, requirements, complete, addTime, scheduleCard, makeQuiz, grade, parseNotes };
  if (typeof module !== 'undefined' && module.exports) module.exports = api;
  else root.Learning = api;
})(typeof window === 'undefined' ? globalThis : window);
