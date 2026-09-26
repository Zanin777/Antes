const test = require('node:test');
const assert = require('node:assert/strict');
const L = require('../dist/learning.js');
function decision() {
  return L.migrate({name:'Tocar guitarra',why:'Aprender uma música',category:'Hobby',frequency:3,minutes:5,cost:0,free:[1,1,1,1,1,1,1],sessions:[{done:false,actual:0},{done:false,actual:0},{done:false,actual:0}],reflection:'',choice:''});
}
function prepared(s) {
  Object.assign(s.sessions[0],{elapsedMs:300000,reviewed:['g1','g2','g3'],quiz:{correct:3,total:4},note:'Pratiquei a escala devagar e preciso melhorar a volta.'});
  return s;
}
test('detecta temas com acentos, distingue Java de JavaScript e admite assunto desconhecido',()=>{
  assert.equal(L.detect('Aprender violão').id,'guitar');
  assert.equal(L.detect('JavaScript para iniciantes').id,'javascript');
  assert.equal(L.detect('Java para iniciantes').id,'java');
  assert.equal(L.detect('Inglês básico').id,'english');
  assert.equal(L.detect('Cerâmica'),null);
});
test('migração preserva histórico manual sem dar crédito nas novas sessões',()=>{
  const old={...decision(),schema:1,sessions:[{done:true,actual:25},{done:true,actual:35},{done:false,actual:0}]};
  const migrated=L.migrate(old);
  assert.equal(migrated.legacyHistory.reduce((n,s)=>n+s.actual,0),60);
  assert.equal(migrated.sessions.filter(s=>s.done).length,0);
  assert.deepEqual(L.migrate(migrated),migrated);
});
test('não permite pular a sessão corrente nem concluir só com tempo',()=>{
  const s=decision(); s.sessions[0].elapsedMs=300000;
  assert.equal(L.canStart(s,1),false);
  assert.throws(()=>L.complete(s,0),/flashcards/);
});
test('cada requisito é obrigatório, incluindo tempo real mínimo e cartões distintos',()=>{
  for (const key of ['elapsedMs','reviewed','quiz','note']) {
    const s=prepared(decision());
    s.sessions[0][key]={elapsedMs:299999,reviewed:['g1','g1','g1'],quiz:{correct:2,total:4},note:'curto'}[key];
    assert.equal(L.requirements(s,0).ready,false,key);
  }
});
test('libera a próxima sessão somente após 24 horas da conclusão',()=>{
  const now=1800000000000,s=prepared(decision());
  L.complete(s,0,now);
  assert.equal(s.sessions[0].actual,5);
  assert.equal(L.canStart(s,1,now+3600000),false);
  assert.equal(L.canStart(s,1,now+L.DAY-1),false);
  assert.equal(L.canStart(s,1,now+L.DAY),true);
  assert.throws(()=>L.complete(s,0,now),/Aguarde/);
});
test('cronômetro recusa saltos de suspensão, valores negativos e sessão concluída',()=>{
  const s=decision();
  assert.equal(L.addTime(s,0,1000),true);
  assert.equal(L.addTime(s,0,100000),false);
  assert.equal(L.addTime(s,0,-5),false);
  assert.equal(s.sessions[0].elapsedMs,1000);
  s.sessions[0].done=true;
  assert.equal(L.addTime(s,0,1000),false);
});
test('repetição espaçada exige o vencimento para subir de nível',()=>{
  const now=1800000000000;
  const first=L.scheduleCard(null,true,now);
  const early=L.scheduleCard(first,true,now+1000);
  assert.equal(early.level,1);assert.equal(early.dueAt,now+L.DAY);
  const next=L.scheduleCard(first,true,now+L.DAY);
  assert.equal(next.level,2); assert.equal(next.dueAt,now+4*L.DAY);
  const missed=L.scheduleCard(next,false,now+L.DAY);
  assert.equal(missed.level,0);assert.equal(missed.dueAt,now+L.DAY+600000);
});
test('quiz tem alternativas distintas e corrige todas as respostas',()=>{
  for(const pack of L.packs){
    const s=decision();s.name=pack.match.source.includes('guitarra')?'guitarra':{javascript:'javascript',java:'java',python:'python',sql:'sql',english:'inglês',web:'html'}[pack.id];
    const questions=L.makeQuiz(s,()=>.3);
    assert.ok(questions.length>=4);
    questions.forEach(q=>assert.equal(new Set(q.options).size,4));
    const answers=Object.fromEntries(questions.map((q,i)=>[i,String(q.options.indexOf(q.answer))]));
    assert.equal(L.grade(questions,answers).correct,questions.length);
    assert.equal(L.grade(questions,{}).correct,0);
  }
});
test('novo quiz prioriza as perguntas erradas na última tentativa',()=>{
  const s=decision();s.attempts=[{missed:['g7']}];
  assert.equal(L.makeQuiz(s,()=>.5)[0].id,'g7');
});
test('importa assunto livre e cria questões sem fingir conhecer seu conteúdo',()=>{
  const s=decision();s.name='Cerâmica';
  s.customCards=L.parseNotes('O que é argila? | Material plástico mineral\nO que é um forno? | Equipamento de queima\nO que é esmalte? | Revestimento vitrificável\nO que é modelagem? | Dar forma ao material');
  assert.equal(L.cardsFor(s).length,4);
  assert.equal(L.makeQuiz(s).length,4);
});
test('importação inválida é atômica e duplicatas não geram alternativas ambíguas',()=>{
  const s=decision();
  assert.throws(()=>L.parseNotes('O que é uma casa? | Espaço entre trastes\nSem separador'),/Linha 2/);
  assert.equal(s.customCards.length,0);
  assert.throws(()=>L.parseNotes('O que é uma casa? | Resposta\nO que é um traste? | Resposta'),/diferentes/);
});
test('estado carregado precisa de dados válidos',()=>{
  const s=decision();assert.equal(L.validate(s),true);
  assert.equal(L.validate({...s,minutes:-1}),false);
  assert.equal(L.validate({...s,frequency:100}),false);
  assert.equal(L.validate({...s,customCards:[{id:'x'}]}),false);
});
