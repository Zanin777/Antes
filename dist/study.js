/* Interface do experimento. As regras estão em learning.js. */
let studyTab = 'session';
let cardIndex = 0;
let cardRevealed = false;
let onlyDue = false;
let quizQuestions = [];
let quizAnswers = {};
let quizFeedback = null;
let quizStartedFor = null;
let timerId = null;
let timerLastTick = 0;
let timerSaveTick = 0;
let releaseTimerLock = null;
let timerStarting = false;
const clockText = ms => {
  const sec = Math.floor(Math.max(0, ms) / 1000);
  return `${String(Math.floor(sec / 60)).padStart(2, '0')}:${String(sec % 60).padStart(2, '0')}`;
};
const dateText = timestamp => new Date(timestamp).toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' });
function resetStudy() {
  stopTimer(); studyTab = 'session'; cardIndex = 0; cardRevealed = false;
  quizQuestions = []; quizAnswers = {}; quizFeedback = null; quizStartedFor = null;
}
function currentSession() { return state.sessions[Learning.currentIndex(state)]; }
function refreshStudy(selector) {
  render();
  const element = document.querySelector(selector);
  if (!element) return;
  if (!element.matches('button,input,select,textarea,a')) element.setAttribute('tabindex','-1');
  element.focus({preventScroll:true});
}
function studyStats() {
  const cards = Learning.cardsFor(state);
  return { cards, due: cards.filter(c => !state.cardProgress[c.id] || state.cardProgress[c.id].dueAt <= Date.now()).length,
    learned: cards.filter(c => state.cardProgress[c.id]?.level >= 2).length };
}
function experiment() {
  const index = Learning.currentIndex(state), pack = Learning.detect(state.name);
  const { cards, due } = studyStats();
  return heading('Da intenção à prática.', esc(state.name) + ' · um pouco a cada dia.', '<a class="btn" href="#resultados">Ver resultados ↗</a>') + `
    <div class="study-banner"><div><span class="eyebrow">SUA JORNADA DE APRENDIZADO</span><h2>Tempo para praticar. Espaço para aprender.</h2><p>Cada sessão reúne prática, revisão e um relato. A próxima abre 24 horas depois da conclusão.</p></div><div class="study-count"><strong>${total()}<span> / ${state.frequency}</span></strong><span>sessões concluídas</span></div></div>
    <ol class="journey" aria-label="Progresso das sessões">${state.sessions.map((s, i) => `<li class="${s.done ? 'complete' : i === index ? 'current' : ''}"><span>${s.done ? '✓' : i + 1}</span><div><strong>Sessão ${i + 1}</strong><small>${s.done ? dateText(s.completedAt) : i === index && Learning.canStart(state, i) ? 'Seu próximo passo' : i === index ? 'Em intervalo' : 'Depois da anterior'}</small></div></li>`).join('')}</ol>
    <div class="study-tabs" role="group" aria-label="Área de aprendizado">
      ${[['session','◷','Prática'],['cards','▤','Flashcards'],['quiz','✎','Quiz'],['material','＋','Meu material']].map(([id, icon, label]) => `<button class="study-tab ${studyTab === id ? 'selected' : ''}" data-study-tab="${id}" aria-pressed="${studyTab === id}"><span aria-hidden="true">${icon}</span> ${label}${id === 'cards' ? `<b>${due}</b>` : ''}</button>`).join('')}
    </div>
    <div class="study-layout"><section id="study-content">${({session: sessionPanel, cards: cardsPanel, quiz: quizPanel, material: materialPanel})[studyTab]()}</section>
    <aside class="study-aside"><div class="card"><span class="eyebrow muted">SEU CONTEÚDO</span><h3>${esc(pack?.title || 'Trilha com seu material')}</h3><p>${pack ? 'Conteúdo introdutório selecionado pelo tema da sua decisão.' : 'Adicione perguntas e respostas do seu estudo para criar sua trilha.'}</p><div class="mini-metrics"><div><strong>${cards.length}</strong><span>flashcards</span></div><div><strong>${state.attempts.length}</strong><span>quizzes feitos</span></div></div>${pack ? `<a class="text-link" href="${pack.source[1]}" target="_blank" rel="noopener noreferrer">${esc(pack.source[0])} ↗</a>` : '<button class="btn" data-study-tab="material">Adicionar conteúdo</button>'}</div>
    <div class="card guidance"><span class="small-icon">↗</span><h3>Constância vale mais que pressa.</h3><p>Nos intervalos entre sessões, você pode continuar revisando cartões e fazendo quizzes livres.</p><p class="fine-print">O tempo é registrado, mas não comprova que a prática aconteceu. Use seu relato para observar o que aprendeu.</p></div></aside></div>`;
}
function sessionPanel() {
  const index = Learning.currentIndex(state);
  if (index < 0) return `<div class="card finish-panel"><span class="finish-icon">✓</span><h2>Você percorreu sua jornada.</h2><p>Compare a experiência com suas expectativas e escolha o próximo passo.</p><a class="btn primary" href="#resultados">Refletir sobre os resultados →</a><p>Seus flashcards e quizzes continuam disponíveis.</p></div>`;
  const s = state.sessions[index], unlocked = Learning.canStart(state, index), pack = Learning.detect(state.name);
  if (!unlocked) return `<div class="card finish-panel"><span class="finish-icon">◷</span><div class="eyebrow muted">UM INTERVALO PARA ASSIMILAR</div><h2>Sessão ${index + 1}: ${dateText(Learning.unlockAt(state, index))}</h2><p>Seu próximo encontro abre 24 horas após a sessão anterior. Aproveite para revisar o que ainda está difícil.</p><button class="btn primary" data-study-tab="cards">Revisar flashcards →</button></div>`;
  return `<div class="card focus-card"><div class="row"><span class="tag blue">SESSÃO ${index + 1} DE ${state.frequency}</span><span id="timer-status" class="muted">${timerId ? 'Cronômetro em andamento' : 'Pronto quando você estiver'}</span></div>
    <h2>Reserve este momento para você.</h2><p>${esc(pack?.practices[index % pack.practices.length] || 'Escolha um conceito do seu material e pratique com um exemplo. Depois, explique o que conseguiu fazer.')}</p>
    <div class="timer-display" id="timer-value" role="timer" aria-label="Tempo registrado">${clockText(s.elapsedMs)}</div><div class="timer-target">de ${state.minutes} minutos planejados</div>
    <div class="progress"><span id="timer-progress" style="width:${Math.min(100, s.elapsedMs / (state.minutes * 60000) * 100)}%"></span></div>
    <div class="timer-controls"><button class="btn primary" id="timer-toggle">${timerId ? 'Ⅱ Pausar' : s.elapsedMs ? '▶ Continuar prática' : '▶ Iniciar prática'}</button><button class="btn" data-study-tab="cards">Estudar com flashcards</button></div><p class="fine-print">Mantenha esta aba aberta durante a prática. Ao fechar ou recarregar, o cronômetro pausa e preserva o tempo registrado.</p>
    <div class="session-checklist" id="session-checklist">${checklistHTML(index)}</div>
    <label for="session-note">O que você praticou e o que ainda está difícil?</label><textarea id="session-note" maxlength="2000" placeholder="Ex.: pratiquei as notas da pentatônica, mas ainda confundo a ordem ao voltar…">${esc(s.note)}</textarea><div class="note-counter muted"><span id="note-length">${s.note.trim().length}</span>/30 caracteres mínimos · salvamento automático</div>
    <label for="satisfaction">Como foi a experiência?</label><select id="satisfaction">${['Muito difícil','Difícil','Razoável','Boa','Muito boa'].map((text,i) => `<option value="${i+1}" ${s.satisfaction === i+1 ? 'selected' : ''}>${i+1} — ${text}</option>`).join('')}</select>
    <div class="form-actions"><button class="btn" data-study-tab="quiz">Fazer quiz da sessão</button><button class="btn primary" id="complete-session" ${Learning.requirements(state,index).ready ? '' : 'disabled'}>Concluir sessão ✓</button></div></div>`;
}
function checklistHTML(index) {
  const checks = Learning.requirements(state, index).checks || [];
  return `<h3>Para concluir esta sessão</h3>${checks.map(c => `<div class="check-row ${c.ok ? 'checked' : ''}"><span aria-hidden="true">${c.ok ? '✓' : '○'}</span><span>${c.label}</span>${c.ok ? '<small>Concluído</small>' : ''}</div>`).join('')}`;
}
function availableFlashcards() {
  const all = Learning.cardsFor(state);
  return onlyDue ? all.filter(c => !state.cardProgress[c.id] || state.cardProgress[c.id].dueAt <= Date.now()) : all;
}
function cardsPanel() {
  const cards = availableFlashcards();
  if (!cards.length) return `<div class="card empty"><h2>${onlyDue ? 'Revisão em dia!' : 'Sua trilha começa com conteúdo.'}</h2><p>${onlyDue ? 'Nenhum cartão está pendente agora. Você pode praticar livremente com todos os cartões.' : 'Adicione pelo menos quatro pares de pergunta e resposta para montar flashcards e um quiz.'}</p><button class="btn primary" ${onlyDue ? 'id="show-all-cards"' : 'data-study-tab="material"'}>${onlyDue ? 'Ver todos os cartões' : 'Adicionar meu material'}</button></div>`;
  cardIndex = ((cardIndex % cards.length) + cards.length) % cards.length;
  const c = cards[cardIndex], progress = state.cardProgress[c.id];
  return `<div class="card flash-panel"><div class="row"><h2>Recorde antes de revelar.</h2><button class="btn compact" id="filter-cards" aria-pressed="${onlyDue}">${onlyDue ? 'Pendentes' : 'Todos os cartões'}</button></div><p>Tente responder em voz alta. Depois, compare e diga como foi.</p><div class="flashcard ${cardRevealed ? 'revealed' : ''}"><span class="eyebrow">${cardRevealed ? 'RESPOSTA' : 'PERGUNTA'} · ${cardIndex + 1}/${cards.length}</span><h3>${esc(c.question)}</h3>${cardRevealed ? `<div class="flash-answer">${esc(c.answer)}</div>` : '<div class="flash-prompt">Pense por alguns segundos antes de virar.</div>'}<button class="btn ${cardRevealed ? '' : 'primary'}" id="flip-card" aria-expanded="${cardRevealed}">${cardRevealed ? 'Ocultar resposta ↶' : 'Revelar resposta ↗'}</button></div>
    ${cardRevealed ? '<div class="recall-actions"><button class="btn" data-recall="again">Ainda estou aprendendo</button><button class="btn primary" data-recall="remembered">Lembrei da resposta ✓</button></div>' : ''}
    <div class="row flash-footer"><button class="btn compact" id="previous-card" aria-label="Cartão anterior">←</button><span class="muted">${progress ? `Próxima revisão: ${dateText(progress.dueAt)}` : 'Primeira revisão'}</span><button class="btn compact" id="next-card" aria-label="Próximo cartão">→</button></div><p class="fine-print">Errou: reveja em 10 minutos. Lembrou: o intervalo cresce para 1, 3 e 7 dias. Sua autoavaliação não é uma nota do quiz.</p></div>`;
}
function quizPanel() {
  if (Learning.eligibleCards(state).length < 4) return `<div class="card empty"><h2>Vamos preparar seu quiz.</h2><p>Este assunto ainda não tem um banco pronto. Adicione quatro perguntas com respostas diferentes no seu material. As alternativas serão montadas a partir delas.</p><button class="btn primary" data-study-tab="material">Preparar conteúdo →</button></div>`;
  if (!quizQuestions.length) return `<div class="card quiz-intro"><span class="eyebrow" style="color:var(--blue)">REVISÃO ATIVA</span><h2>O que ficou do seu estudo?</h2><p>Responda até cinco perguntas sobre ${esc(state.name.toLowerCase())}. No final, veja a explicação de cada resposta.</p><div class="quiz-facts"><span>✎ 4–5 questões</span><span>◎ Meta: 70%</span><span>↻ Novas tentativas</span></div><div class="hint">${(timerId || currentSession()?.elapsedMs > 0) && Learning.canStart(state, Learning.currentIndex(state)) ? 'Este quiz contará para sua sessão atual.' : 'Este é um quiz livre. Para contar para uma sessão, inicie primeiro o cronômetro da prática.'}</div><button class="btn primary" id="start-quiz">Começar quiz →</button></div>`;
  return `<div class="card"><div class="row"><h2>${quizFeedback ? 'Sua revisão, questão por questão.' : 'Hora de testar a memória.'}</h2><span class="tag blue">${quizQuestions.length} QUESTÕES</span></div>${quizFeedback ? `<div class="quiz-score ${quizFeedback.correct/quizFeedback.total >= .7 ? 'passed' : ''}" role="status"><strong>${quizFeedback.correct}/${quizFeedback.total}</strong><span>${quizFeedback.correct/quizFeedback.total >= .7 ? 'Meta atingida. Continue praticando!' : 'Mais uma oportunidade de aprender.'}</span></div>` : '<p>Escolha uma alternativa em cada questão. Você pode estudar e tentar novamente.</p>'}
    <form id="quiz-form">${quizQuestions.map((q,i) => `<fieldset class="quiz-question"><legend><span>${String(i+1).padStart(2,'0')}</span> ${esc(q.question)}</legend>${q.options.map((option,j) => `<label class="quiz-option ${quizFeedback && option === q.answer ? 'correct' : quizFeedback && Number(quizAnswers[i]) === j ? 'incorrect' : ''}"><input type="radio" name="question${i}" value="${j}" ${quizAnswers[i] === String(j) ? 'checked' : ''} ${quizFeedback ? 'disabled' : 'required'}><span>${esc(option)}</span>${quizFeedback && option === q.answer ? '<b>✓</b>' : ''}</label>`).join('')}${quizFeedback ? `<p class="answer-explanation"><strong>${q.options[Number(quizAnswers[i])] === q.answer ? 'Você acertou.' : 'Vamos revisar.'}</strong> ${esc(q.explanation)}</p>` : ''}</fieldset>`).join('')}${quizFeedback ? '<div class="form-actions"><button type="button" class="btn" data-study-tab="cards">Revisar flashcards</button><button type="button" class="btn primary" id="retry-quiz">Tentar novo quiz ↻</button></div>' : '<button class="btn primary">Conferir respostas →</button>'}</form></div>`;
}
function materialPanel() {
  return `<div class="card"><span class="eyebrow muted">SEU REPERTÓRIO</span><h2>Transforme anotações em revisão.</h2><p>Use uma pergunta e sua resposta por linha, separadas por <strong>|</strong>. Confira o conteúdo no seu material de estudo antes de adicioná-lo.</p><form id="material-form"><label for="notes-import">Perguntas e respostas</label><textarea id="notes-import" class="notes-import" required maxlength="30000" placeholder="O que é uma casa da guitarra? | Espaço entre trastes onde pressionamos uma corda.&#10;Quantas notas tem uma pentatônica? | Cinco notas por oitava."></textarea><p class="fine-print">Adicione quatro ou mais pares com respostas distintas para habilitar questões de múltipla escolha. Isso organiza seu conteúdo; não verifica se as respostas estão corretas.</p><button class="btn primary">Criar flashcards e questões ＋</button></form>${state.customCards.length ? `<h3 style="margin-top:28px">Seus ${state.customCards.length} cartões</h3><div class="custom-cards">${state.customCards.map(c => `<details><summary>${esc(c.question)}</summary><p>${esc(c.answer)}</p></details>`).join('')}</div>` : ''}<div class="hint" style="margin-top:24px">Bancos disponíveis: guitarra/violão, JavaScript, Java, Python, SQL, inglês e HTML/CSS. A seleção usa o nome da decisão; não há geração por IA nesta versão.</div></div>`;
}
function updateStudyStatus() {
  const index = Learning.currentIndex(state), s = state.sessions[index];
  if (!s) return;
  const clock = document.querySelector('#timer-value');
  if (clock) clock.textContent = clockText(s.elapsedMs);
  const progress = document.querySelector('#timer-progress');
  if (progress) progress.style.width = Math.min(100, s.elapsedMs / (state.minutes * 60000) * 100) + '%';
  const checklist = document.querySelector('#session-checklist');
  if (checklist) checklist.innerHTML = checklistHTML(index);
  const complete = document.querySelector('#complete-session');
  if (complete) complete.disabled = !Learning.requirements(state,index).ready;
}
function tickTimer() {
  const now = performance.now(), delta = now - timerLastTick;
  timerLastTick = now;
  if (!Learning.addTime(state, Learning.currentIndex(state), delta)) {
    stopTimer(false); save(); render(); toast('O cronômetro pausou após uma interrupção. Retome quando estiver pronto.'); return;
  }
  updateStudyStatus();
  if (now - timerSaveTick >= 5000) { save(); timerSaveTick = now; }
}
function stopTimer(flush = true) {
  if (!timerId) return;
  if (flush) Learning.addTime(state, Learning.currentIndex(state), performance.now() - timerLastTick);
  clearInterval(timerId); timerId = null;
  releaseTimerLock?.(); releaseTimerLock = null;
}
async function startTimer() {
  const index = Learning.currentIndex(state);
  if (!Learning.canStart(state,index) || timerId || timerStarting) return;
  const begin = () => { timerLastTick = performance.now(); timerSaveTick = timerLastTick; timerId = setInterval(tickTimer,1000); render(); };
  if (!navigator.locks) { toast('Seu navegador não permite proteger o cronômetro entre abas. Use uma única aba.'); begin(); return; }
  timerStarting = true;
  try { await navigator.locks.request('antes-focus-timer',{ifAvailable:true}, async lock => {
    timerStarting = false;
    if (!lock) { toast('Já existe uma prática em andamento em outra aba. Pause lá primeiro.'); return; }
    await new Promise(resolve => { releaseTimerLock = resolve; begin(); });
  }); } catch { toast('Não foi possível iniciar o cronômetro. Reabra esta aba.'); }
  finally { timerStarting = false; }
}
function beginQuiz() {
  quizQuestions = Learning.makeQuiz(state); quizAnswers = {}; quizFeedback = null;
  const index = Learning.currentIndex(state);
  quizStartedFor = Learning.canStart(state,index) && (timerId || state.sessions[index].elapsedMs > 0) ? index : null;
  refreshStudy('#quiz-form input');
  document.querySelector('#study-content')?.scrollIntoView({block:'start'});
}
function bindStudy() {
  document.querySelectorAll('[data-study-tab]').forEach(button => button.addEventListener('click', () => { studyTab = button.dataset.studyTab; refreshStudy(`[data-study-tab="${studyTab}"]`); }));
  document.querySelector('#timer-toggle')?.addEventListener('click', () => { if (timerId) { stopTimer(); save(); render(); } else void startTimer(); });
  document.querySelector('#session-note')?.addEventListener('input', e => { currentSession().note = e.target.value; document.querySelector('#note-length').textContent = e.target.value.trim().length; save(); updateStudyStatus(); });
  document.querySelector('#satisfaction')?.addEventListener('change', e => { currentSession().satisfaction = Number(e.target.value); save(); });
  document.querySelector('#complete-session')?.addEventListener('click', () => {
    const index = Learning.currentIndex(state);
    if (!Learning.requirements(state,index).ready) { toast('Conclua os itens da sessão primeiro.'); return; }
    stopTimer();
    try { Learning.complete(state,index); state.reflection = state.sessions[index].note; save(); quizQuestions=[]; quizFeedback=null; quizStartedFor=null; render(); toast('Sessão concluída! Sua próxima prática abre em 24 horas.'); } catch(error) { toast(error.message); }
  });
  document.querySelector('#flip-card')?.addEventListener('click', () => { cardRevealed=!cardRevealed; refreshStudy('#flip-card'); });
  document.querySelector('#next-card')?.addEventListener('click', () => { cardIndex++; cardRevealed=false; refreshStudy('#next-card'); });
  document.querySelector('#previous-card')?.addEventListener('click', () => { cardIndex--; cardRevealed=false; refreshStudy('#previous-card'); });
  document.querySelector('#filter-cards')?.addEventListener('click', () => { onlyDue=!onlyDue; cardIndex=0; cardRevealed=false; render(); });
  document.querySelector('#show-all-cards')?.addEventListener('click', () => { onlyDue=false; cardIndex=0; render(); });
  document.querySelectorAll('[data-recall]').forEach(button => button.addEventListener('click', () => {
    if (!cardRevealed) return;
    const c=availableFlashcards()[cardIndex]; if (!c) return;
    state.cardProgress[c.id]=Learning.scheduleCard(state.cardProgress[c.id],button.dataset.recall==='remembered');
    const index=Learning.currentIndex(state), s=state.sessions[index];
    if (Learning.canStart(state,index) && (timerId || s.elapsedMs>0) && !s.reviewed.includes(c.id)) s.reviewed.push(c.id);
    if (!onlyDue) cardIndex++;
    cardRevealed=false; save(); refreshStudy('#flip-card');
  }));
  document.querySelector('#start-quiz')?.addEventListener('click', beginQuiz);
  document.querySelector('#retry-quiz')?.addEventListener('click', beginQuiz);
  document.querySelector('#quiz-form')?.addEventListener('change', e => { if(e.target.name.startsWith('question')) quizAnswers[e.target.name.slice(8)]=e.target.value; });
  document.querySelector('#quiz-form')?.addEventListener('submit', e => {
    e.preventDefault(); if (quizFeedback || quizQuestions.some((q,i)=>quizAnswers[i]===undefined)) return;
    quizFeedback=Learning.grade(quizQuestions,quizAnswers);
    const index=Learning.currentIndex(state), counts=quizStartedFor===index && Learning.canStart(state,index);
    state.attempts.push({...quizFeedback,at:Date.now(),session:counts?index:null}); state.attempts=state.attempts.slice(-100);
    if(counts) state.sessions[index].quiz={...quizFeedback};
    quizFeedback.missed.forEach(id => { state.cardProgress[id]={level:0,dueAt:Date.now(),reviewedAt:Date.now()}; });
    save(); refreshStudy('#study-content h2');
    document.querySelector('#study-content')?.scrollIntoView({block:'start'});
  });
  document.querySelector('#material-form')?.addEventListener('submit', e => {
    e.preventDefault();
    try { const added=Learning.parseNotes(document.querySelector('#notes-import').value,state.customCards); state.customCards.push(...added); save(); render(); toast(`${added.length} cartões adicionados ao seu estudo.`); } catch(error) { toast(error.message); }
  });
}
function studyResults() {
  const completed=state.sessions.filter(s=>s.done), attempts=state.attempts, {learned,cards}=studyStats();
  return `<div class="learning-results"><div class="stat"><span class="stat-label">Último quiz</span><div class="stat-number">${attempts.length ? Math.round(attempts.at(-1).correct/attempts.at(-1).total*100)+'%' : '—'}</div><small>${attempts.length} tentativas registradas</small></div><div class="stat"><span class="stat-label">Cartões consolidados</span><div class="stat-number">${learned}<span class="muted"> / ${cards.length}</span></div><small>Autoavaliação em duas revisões</small></div><div class="stat"><span class="stat-label">Experiência média</span><div class="stat-number">${completed.length ? (completed.reduce((n,s)=>n+s.satisfaction,0)/completed.length).toFixed(1) : '—'}<span class="muted"> / 5</span></div><small>Como você se sentiu ao praticar</small></div></div>${state.legacyHistory?.length ? `<div class="hint legacy-note">${state.legacyHistory.length} sessões da versão anterior foram preservadas no histórico (${state.legacyHistory.reduce((n,s)=>n+s.actual,0)} min). Elas não contam como sessões verificadas pelas novas etapas.</div>` : ''}${completed.length ? `<details class="card history"><summary>Relatos das sessões concluídas</summary>${completed.map((s,i)=>`<article><h3>Sessão ${i+1} · ${dateText(s.completedAt)}</h3><p>${esc(s.note)}</p><span class="muted">${s.actual} minutos · quiz ${s.quiz.correct}/${s.quiz.total} · experiência ${s.satisfaction}/5</span></article>`).join('')}</details>` : ''}`;
}
window.addEventListener('pagehide',()=>{ if(timerId){stopTimer();save();} });
