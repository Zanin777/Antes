// Aplica a preferência antes de pintar a tela, evitando um clarão ao recarregar.
(function () {
  let theme;
  try { theme = localStorage.getItem('antes-theme'); } catch { /* Usa o tema do sistema. */ }
  document.documentElement.dataset.theme = theme === 'dark' || theme === 'light' ? theme : (matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
})();
function setupTheme() {
  const button = document.querySelector('#theme-toggle');
  const update = () => { const dark=document.documentElement.dataset.theme==='dark'; button.textContent=dark?'☀ Modo claro':'☾ Modo escuro'; button.setAttribute('aria-label',dark?'Ativar modo claro':'Ativar modo escuro'); button.setAttribute('aria-pressed',String(dark)); };
  update();
  button.addEventListener('click',()=>{ const next=document.documentElement.dataset.theme==='dark'?'light':'dark'; document.documentElement.dataset.theme=next; try{localStorage.setItem('antes-theme',next);}catch{} update(); });
}
