// Smoke E2E do CondoManager (Playwright), corre contra a app publicada.
//
//   BASE_URL=https://condomanager.onrender.com node e2e/smoke.mjs
//
// Sai com código != 0 se algum teste falhar (pode ser usado em CI).
import { chromium } from 'playwright';

const BASE = process.env.BASE_URL || 'https://condomanager.onrender.com';
const GESTOR = { email: 'gestor.alfa@demo.local', password: 'gestor123' };

let fails = 0;
const check = (nome, cond) => { console.log(`${cond ? 'PASS' : 'FAIL'} — ${nome}`); if (!cond) fails++; };
const poll = async (fn, ms = 15000, step = 250) => { const end = Date.now() + ms; while (Date.now() < end) { if (await fn()) return true; await new Promise(r => setTimeout(r, step)); } return false; };

const browser = await chromium.launch();
const page = await browser.newPage();
const errors = [];
page.on('pageerror', e => errors.push(String(e)));

await page.goto(BASE, { waitUntil: 'networkidle' });
await page.evaluate(({ email, password }) => window.doLogin(email, password), GESTOR);
await poll(() => page.evaluate(() => document.getElementById('kpis').innerHTML.length > 50));
check('login + dashboard carrega KPIs', await page.evaluate(() => document.getElementById('kpis').innerHTML.length > 50));

// Ata: criar e confirmar que aparece na lista (read-after-write)
const ataOk = await page.evaluate(async () => {
  await loadCondominios(); verCondominio((cache.cond || [])[0].id);
  await new Promise(r => setTimeout(r, 400)); await loadReunioes();
  window.__rid = cache.reun[0].id; await loadAtasDe(window.__rid);
  const t = 'SMOKE-ATA-' + Date.now();
  const nova = await apiPost('/api/v1/atas', { reuniaoId: window.__rid, titulo: t, descricao: 'x', dataReuniao: cache.reun[0].data });
  cache.atas = [nova, ...cache.atas]; renderAtas();
  const ok = document.getElementById('subDetalhe').innerHTML.includes(t);
  try { await apiDel('/api/v1/atas/' + nova.id); } catch {}
  return ok;
});
check('ata criada aparece logo na lista', ataOk);

// Ocorrência: criar
const ocorOk = await page.evaluate(async () => {
  const r = await apiPost('/api/v1/ocorrencias', { condominioId: currentCond.id, titulo: 'SMOKE-OC', descricao: 'x', prioridade: 'ALTA' });
  const ok = !!r.id && r.estado === 'ABERTA';
  try { await apiDel('/api/v1/ocorrencias/' + r.id); } catch {}
  return ok;
});
check('criar ocorrência (estado ABERTA)', ocorOk);

// Votação: criar -> abrir -> votar -> resultado
const votOk = await page.evaluate(async () => {
  const rid = (await apiGet('/api/v1/reunioes?size=5')).conteudo[0].id;
  const v = await apiPost('/api/v1/votacoes', { reuniaoId: rid, tema: 'SMOKE-VOT', tipoMaioria: 'MAIORIA_SIMPLES' });
  await apiPost('/api/v1/votacoes/' + v.id + '/abrir');
  const reuniao = await apiGet('/api/v1/reunioes/' + rid);
  const fr = (await apiGet('/api/v1/fracoes?condominioId=' + reuniao.condominioId + '&size=5')).conteudo;
  const cs = (await apiGet('/api/v1/condominos?fracaoId=' + fr[0].id + '&size=5')).conteudo;
  await apiPost('/api/v1/votacoes/' + v.id + '/votos', { condominoId: cs[0].id, resposta: 'SIM' });
  const res = await apiGet('/api/v1/votacoes/' + v.id + '/resultado');
  const ok = Number(res.somaSim) > 0 && res.numeroVotos === 1;
  try { await apiDel('/api/v1/votacoes/' + v.id); } catch {}
  return ok;
});
check('votação criar→abrir→votar→resultado', votOk);

// Mensagem broadcast
const msgOk = await page.evaluate(async () => { const m = await apiPost('/api/v1/mensagens', { tipo: 'BROADCAST', assunto: 'SMOKE', conteudo: 'x' }); const ok = !!m.id; try { await apiDel('/api/v1/mensagens/' + m.id); } catch {} return ok; });
check('enviar mensagem broadcast', msgOk);

// Relatórios (PDF + Excel) válidos
const relOk = await page.evaluate(async () => {
  const pdf = await (await fetch('/api/v1/relatorios/quotas?condominioId=' + currentCond.id, { headers: { Authorization: 'Bearer ' + token() } })).blob();
  const xls = await (await fetch('/api/v1/relatorios/quotas/excel?condominioId=' + currentCond.id, { headers: { Authorization: 'Bearer ' + token() } })).blob();
  return pdf.size > 100 && xls.size > 100;
});
check('relatórios PDF + Excel geram conteúdo', relOk);

// Swagger
const apiDocs = await page.evaluate(async (base) => (await fetch(base + '/v3/api-docs')).status, BASE);
check('swagger /v3/api-docs responde 200', apiDocs === 200);

check('sem erros de consola (pageerror)', errors.length === 0);

await browser.close();
console.log(fails === 0 ? '\n✅ TODOS OS TESTES PASSARAM' : `\n❌ ${fails} TESTE(S) FALHARAM`);
process.exit(fails === 0 ? 0 : 1);
