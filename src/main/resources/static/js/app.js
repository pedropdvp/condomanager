// Indicador de carregamento global: barra no topo enquanto houver pedidos HTTP em curso.
(function () {
    let pending = 0;
    const orig = window.fetch.bind(window);
    const paint = () => { const el = document.getElementById('cmProgress'); if (el) el.classList.toggle('active', pending > 0); };
    window.fetch = function () { pending++; paint(); return orig.apply(this, arguments).finally(() => { pending = Math.max(0, pending - 1); paint(); }); };
})();
const token = () => sessionStorage.getItem('cm_token');
const user = () => JSON.parse(sessionStorage.getItem('cm_user') || '{}');
const isAdmin = () => (user().perfis || []).includes('ROLE_ADMIN_SISTEMA');
const isGestor = () => (user().perfis || []).includes('ROLE_GESTOR_EMPRESA');
const canManageUsers = () => isAdmin() || isGestor();
const isCondomino = () => (user().perfis || []).includes('ROLE_CONDOMINO');
const temPapelGestao = () => (user().perfis || []).some(p => ['ROLE_GESTOR_EMPRESA', 'ROLE_ADMIN_SISTEMA', 'ROLE_FUNCIONARIO', 'ROLE_ADMIN_CONDOMINIO'].includes(p));
const isCondominoPuro = () => isCondomino() && !temPapelGestao();
const PALETTE = ['#0d6efd','#198754','#ffc107','#dc3545','#6f42c1','#20c997','#fd7e14'];
// Permissões efetivas (granular). Módulos fora desta lista são RBAC por papel -> can()=true.
let myPerms = {};
const GRANULAR = new Set(['EMPRESAS','CONDOMINIOS','CONDOMINOS','UTILIZADORES','ATAS','PAGAMENTOS','REUNIOES','VOTACOES','DOCUMENTOS','MENSAGENS']);
function can(modulo, acao) { return !GRANULAR.has(modulo) || (myPerms[modulo] || []).includes(acao); }

const LABELS = {
    id:'ID', nome:'Nome', morada:'Morada', orcamentoAnual:'Orçamento anual (€)', numero:'Nº',
    tipologia:'Tipologia', permilagem:'Permilagem (‰)', areaM2:'Área (m²)', fracaoId:'Fração',
    mes:'Mês', ano:'Ano', valor:'Valor (€)', estado:'Estado', nif:'NIF', email:'Email',
    telefone:'Telefone', tipo:'Tipo', bloco:'Bloco', numeroPisos:'Pisos', tema:'Tema',
    tipoMaioria:'Maioria', data:'Data', hora:'Hora', local:'Local', ordemTrabalhos:'Ordem de trabalhos',
    ativo:'Ativo', perfis:'Perfis', descricao:'Descrição', categoria:'Categoria', dataDespesa:'Data'
};
const HIDE = new Set(['idEmpresa','createdAt','updatedAt','dataInicio','dataFim','condominioId','edificioId','reuniaoId','idCondomino']);
const MONEY = ['orcamentoAnual','valor'];
const charts = {};
const modal = () => bootstrap.Modal.getOrCreateInstance(document.getElementById('modal'));
let currentCond = null, onSubmitModal = null;
const cache = { cond: [], frac: [], quotas: [], users: [], edif: [], desp: [], reun: [], cond_o: [], docs: [], atas: [], ocor: [], msgs: [], votacoes: [], audit: [], empresas: [] };
let currentReuniaoId = null;
let msgBox = 'recebidas';
let msgPage = 0, msgTotalPaginas = 1, msgTotal = 0;
// Controlo de paginação server-side reutilizável (‹ Anterior · página X de Y · Próximo ›).
function paginadorHtml(page, totalPaginas, total, loaderName) {
    if (!totalPaginas || totalPaginas <= 1) return '';
    return `<nav class="d-flex align-items-center gap-2 mt-2">
        <button class="btn btn-sm btn-outline-secondary" ${page <= 0 ? 'disabled' : ''} onclick="${loaderName}(${page - 1})">‹ Anterior</button>
        <span class="small text-secondary">Página ${page + 1} de ${totalPaginas} · ${total} registos</span>
        <button class="btn btn-sm btn-outline-secondary" ${page >= totalPaginas - 1 ? 'disabled' : ''} onclick="${loaderName}(${page + 1})">Próximo ›</button></nav>`;
}

const PERFIS_ALL = [
    { value: 'CONDOMINO', label: 'Condómino' }, { value: 'ADMIN_CONDOMINIO', label: 'Administrador de Condomínio' },
    { value: 'FUNCIONARIO', label: 'Funcionário' }, { value: 'GESTOR_EMPRESA', label: 'Gestor da empresa' },
    { value: 'ADMIN_SISTEMA', label: 'Administrador do Sistema' }
];
const perfisDisponiveis = () => isAdmin() ? PERFIS_ALL : PERFIS_ALL.filter(p => p.value !== 'ADMIN_SISTEMA');
const FUNCS = [['EMPRESAS','Empresas'],['CONDOMINIOS','Condomínios'],['CONDOMINOS','Condóminos'],['UTILIZADORES','Utilizadores'],
    ['ATAS','Atas'],['PAGAMENTOS','Pagamentos'],['REUNIOES','Reuniões'],['VOTACOES','Votações'],['DOCUMENTOS','Documentos'],['MENSAGENS','Mensagens']];
const ACS = [['CRIAR','Criar'],['EDITAR','Editar'],['APAGAR','Apagar'],['CONSULTAR','Consultar']];
function matrizHTML(m, editable) {
    let h = '<table class="table table-sm text-center align-middle mb-0"><thead><tr><th class="text-start small">Funcionalidade</th>';
    ACS.forEach(a => h += `<th class="small">${a[1]}</th>`); h += '</tr></thead><tbody>';
    FUNCS.forEach(([fk, fl]) => { h += `<tr><td class="text-start small">${fl}</td>`;
        ACS.forEach(([ak]) => { const on = (m[fk] || []).includes(ak); const id = editable ? `id="pm_${fk}_${ak}"` : '';
            h += `<td><input type="checkbox" class="form-check-input" ${id} ${on ? 'checked' : ''} ${editable ? '' : 'disabled'}></td>`; });
        h += '</tr>'; });
    return h + '</tbody></table>';
}
function showAlert(msg, type='info') {
    const el = document.getElementById('alert'); el.className = `alert alert-${type}`; el.textContent = msg;
    el.classList.remove('d-none'); setTimeout(() => el.classList.add('d-none'), 5000);
}
function rows(d) { return Array.isArray(d) ? d : (d.conteudo || d.content || []); }
function fmt(k, v) { if (v === null || v === undefined) return '—'; if (MONEY.includes(k) && typeof v === 'number') return v.toFixed(2) + ' €'; if (Array.isArray(v)) return v.join(', '); if (typeof v === 'boolean') return v ? 'Sim' : 'Não'; return String(v); }
function table(items, actionsFn) {
    if (!items.length) return '<p class="text-secondary">Sem registos.</p>';
    const keys = Object.keys(items[0]).filter(k => !HIDE.has(k));
    let h = '<table class="table table-sm table-hover align-middle"><thead><tr>';
    keys.forEach(k => h += `<th>${LABELS[k] || k}</th>`); if (actionsFn) h += '<th class="text-end">Ações</th>'; h += '</tr></thead><tbody>';
    items.forEach(it => { h += '<tr>'; keys.forEach(k => h += `<td>${fmt(k, it[k])}</td>`); if (actionsFn) h += `<td class="text-end text-nowrap">${actionsFn(it)}</td>`; h += '</tr>'; });
    return h + '</tbody></table>';
}
async function apiSend(method, path, body) {
    const opt = { method, headers: { 'Authorization': 'Bearer ' + token() } };
    if (body !== undefined) { opt.headers['Content-Type'] = 'application/json'; opt.body = JSON.stringify(body); }
    const res = await fetch(path, opt);
    if (res.status === 401) { logout(); throw new Error('Sessão expirada — entre novamente.'); }
    const txt = await res.text(); let data; try { data = txt ? JSON.parse(txt) : null; } catch { data = txt; }
    if (!res.ok) {
        if (res.status === 403) throw new Error('Sem permissão (403) para esta ação.');
        throw new Error((data && (data.message || data.mensagem || data.erro || data.detail)) || ('Erro ' + res.status));
    }
    return data;
}
const apiGet = p => apiSend('GET', p), apiPost = (p, b) => apiSend('POST', p, b), apiPut = (p, b) => apiSend('PUT', p, b), apiDel = p => apiSend('DELETE', p);
async function apiUpload(path, formData, method='POST') {
    const res = await fetch(path, { method, headers: { 'Authorization': 'Bearer ' + token() }, body: formData });
    if (res.status === 401) { logout(); throw new Error('Sessão expirada — entre novamente.'); }
    const txt = await res.text(); let data; try { data = txt ? JSON.parse(txt) : null; } catch { data = txt; }
    if (!res.ok) { if (res.status === 403) throw new Error('Sem permissão (403) para esta ação.'); throw new Error((data && (data.message || data.mensagem || data.erro)) || ('Erro ' + res.status)); }
    return data;
}
// Diálogo "Guardar como" (Chrome/Edge via File System Access API); fallback para download direto.
function pickSaveHandle(suggestedName, mime) {
    const ext = suggestedName.includes('.') ? suggestedName.split('.').pop().toLowerCase() : '';
    const types = ext ? [{ description: ext.toUpperCase() + ' (.' + ext + ')', accept: { [mime || 'application/octet-stream']: ['.' + ext] } }] : [];
    return window.showSaveFilePicker({ suggestedName, types });
}
function anchorDownload(blob, name) {
    const url = URL.createObjectURL(blob); const a = document.createElement('a');
    a.href = url; a.download = name || 'ficheiro'; document.body.appendChild(a); a.click(); a.remove(); URL.revokeObjectURL(url);
}
async function downloadComPicker(path, suggestedName, mime, successMsg) {
    try {
        // Abre o seletor de pasta primeiro (dentro do gesto do clique), depois descarrega.
        let handle = null;
        if (window.showSaveFilePicker) {
            try { handle = await pickSaveHandle(suggestedName, mime); }
            catch (e) { if (e.name === 'AbortError') return; handle = null; }
        }
        const res = await fetch(path, { headers: { 'Authorization': 'Bearer ' + token() } });
        if (res.status === 401) { logout(); throw new Error('Sessão expirada — entre novamente.'); }
        if (!res.ok) throw new Error('Erro ' + res.status);
        const blob = await res.blob();
        if (handle) { const w = await handle.createWritable(); await w.write(blob); await w.close(); }
        else anchorDownload(blob, suggestedName);
        if (successMsg) showAlert(successMsg, 'success');
    } catch (e) { showAlert(e.message, 'danger'); }
}
function downloadFile(path, filename) { return downloadComPicker(path, filename || 'ficheiro', 'application/octet-stream'); }
// Exporta um array de objetos para CSV (UTF-8 com BOM, abre bem no Excel).
function exportarCSV(linhas, nome) {
    if (!linhas || !linhas.length) { showAlert('Nada para exportar.', 'info'); return; }
    const keys = Object.keys(linhas[0]).filter(k => !['idEmpresa', 'createdAt', 'updatedAt'].includes(k));
    const cel = v => '"' + String(v === null || v === undefined ? '' : (Array.isArray(v) ? v.join('; ') : v)).replace(/"/g, '""') + '"';
    const csv = [keys.join(',')].concat(linhas.map(o => keys.map(k => cel(o[k])).join(','))).join('\r\n');
    anchorDownload(new Blob(['﻿' + csv], { type: 'text/csv;charset=utf-8' }), (nome || 'dados') + '.csv');
}
function esc(s) { return String(s).replace(/'/g, "\\'").replace(/"/g, '&quot;'); }
function btn(cls, label, onclick) { return `<button class="btn btn-sm ${cls}" onclick="${onclick}">${label}</button> `; }

async function doLogin(email, password) {
    const res = await fetch('/api/v1/auth/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email, password }) });
    if (!res.ok) throw new Error('Credenciais inválidas.');
    const data = await res.json(); sessionStorage.setItem('cm_token', data.token); sessionStorage.setItem('cm_user', JSON.stringify(data.utilizador)); await showApp();
}
function logout() {
    sessionStorage.clear(); document.getElementById('appView').classList.add('d-none'); document.getElementById('loginView').classList.remove('d-none');
    document.body.dataset.bg = 'login';
    ['logoutBtn','pwdBtn','userBadge','navbarMenuContainer'].forEach(id => document.getElementById(id).classList.add('d-none'));
    clearTimeout(window._expWarn); clearTimeout(window._expLogout);
    changeLanguage('PT');
}
// Aviso + auto-logout antes de o token JWT expirar (validade ~1h).
function scheduleSessionExpiry() {
    try {
        const t = token(); if (!t) return;
        const payload = JSON.parse(atob(t.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
        if (!payload.exp) return;
        const msLeft = payload.exp * 1000 - Date.now();
        clearTimeout(window._expWarn); clearTimeout(window._expLogout);
        if (msLeft <= 0) { logout(); return; }
        if (msLeft > 120000) window._expWarn = setTimeout(() => showAlert('A sua sessão expira dentro de 2 minutos.', 'warning'), msLeft - 120000);
        window._expLogout = setTimeout(() => { showAlert('Sessão expirada — entre novamente.', 'warning'); logout(); }, msLeft);
    } catch (e) {}
}
function activateTab(name) {
    document.querySelectorAll('[data-tab]').forEach(x => x.classList.toggle('active', x.dataset.tab === name));
    ['dashboard','condominios','votacoes','mensagens','utilizadores','permissoes','auditoria','empresas','portal'].forEach(t => document.getElementById('tab-' + t).classList.toggle('d-none', t !== name));
    const bgMap = { mensagens: 'utilizadores', auditoria: 'permissoes', empresas: 'condominios', portal: 'dashboard' };
    document.body.dataset.bg = bgMap[name] || name; // fundo temático por menu
}
// Botão "Página inicial": com sessão vai para o Dashboard (admin → Utilizadores) sem
// recarregar a página; sem sessão vai para a página comercial.
function irParaInicio() {
    if (!token()) { location.href = '/landing.html'; return; }
    if (isAdmin()) { activateTab('utilizadores'); loadUtilizadores(); }
    else { activateTab('dashboard'); loadDashboard(); }
}
async function showApp() {
    const u = user();
    document.getElementById('loginView').classList.add('d-none'); document.getElementById('appView').classList.remove('d-none');
    document.getElementById('logoutBtn').classList.remove('d-none'); document.getElementById('pwdBtn').classList.remove('d-none');
    document.getElementById('navbarMenuContainer').classList.remove('d-none');
    const badge = document.getElementById('userBadge'); badge.innerHTML = `${u.nome || ''} ·<br>${(u.perfis || []).join(', ')}`; badge.classList.remove('d-none');
    scheduleSessionExpiry();
    document.getElementById('navUtilizadores').classList.toggle('d-none', !canManageUsers());
    document.getElementById('navPermissoes').classList.toggle('d-none', !canManageUsers());
    document.getElementById('navAuditoria').classList.toggle('d-none', !canManageUsers());
    document.getElementById('navEmpresas').classList.toggle('d-none', !isAdmin());
    try { myPerms = await apiGet('/api/v1/permissoes/me') || {}; } catch { myPerms = {}; }
    const verMsgs = !isAdmin() && can('MENSAGENS', 'CONSULTAR');
    document.getElementById('navMensagens').classList.toggle('d-none', !verMsgs);
    if (verMsgs) atualizarBadgeMensagens();
    const condoPuro = isCondominoPuro();
    document.getElementById('navPortal').classList.toggle('d-none', !condoPuro);
    ['navDashboard', 'navCondominios', 'navVotacoes'].forEach(id => document.getElementById(id).classList.toggle('d-none', condoPuro));
    const ab = document.getElementById('adminBanner');
    if (isAdmin()) {
        ab.classList.remove('d-none');
        ab.innerHTML = '👤 <strong>Administrador de plataforma.</strong> Gere <strong>Utilizadores</strong> e <strong>Permissões</strong> (os dados de condomínios devolvem 403 para esta conta).';
        activateTab('utilizadores'); loadUtilizadores();
    } else if (condoPuro) {
        ab.classList.add('d-none'); activateTab('portal'); loadPortal();
    } else { ab.classList.add('d-none'); activateTab('dashboard'); loadDashboard(); }
}

function kpi(label, value, color) { return `<div class="col-6 col-md-4 col-lg-2"><div class="card text-bg-${color} h-100"><div class="card-body py-2"><div class="small">${label}</div><div class="h4 mb-0">${value}</div></div></div></div>`; }
// Sombra subtil por baixo das séries -> sensação de espessura/3D.
const cmShadow = { id: 'cmShadow',
    beforeDatasetsDraw(c) { const x = c.ctx; x.save(); x.shadowColor = 'rgba(0,0,0,.30)'; x.shadowBlur = 12; x.shadowOffsetX = 0; x.shadowOffsetY = 6; },
    afterDatasetsDraw(c) { c.ctx.restore(); } };
Chart.register(cmShadow);
function chartTheme() {
    const dark = document.documentElement.dataset.bsTheme === 'dark';
    return { text: dark ? '#e9ecef' : '#343a40', grid: dark ? 'rgba(255,255,255,.10)' : 'rgba(0,0,0,.07)', sep: dark ? '#2b3035' : '#ffffff' };
}
function chart(id, type, labels, data, label) {
    if (charts[id]) charts[id].destroy();
    const t = chartTheme(), isBar = type === 'bar';
    const ds = { label, data, backgroundColor: labels.map((_, i) => PALETTE[i % PALETTE.length]),
        borderColor: t.sep, borderWidth: isBar ? 0 : 3, borderRadius: isBar ? 10 : 0,
        maxBarThickness: 56, hoverOffset: isBar ? 0 : 12 };
    charts[id] = new Chart(document.getElementById(id), {
        type, data: { labels, datasets: [ds] },
        options: { responsive: true, maintainAspectRatio: false,
            cutout: type === 'doughnut' ? '58%' : undefined,
            plugins: {
                legend: { display: !isBar, position: 'bottom',
                    labels: { color: t.text, usePointStyle: true, pointStyle: 'circle', padding: 14, font: { family: 'Inter', size: 12, weight: '500' } } },
                tooltip: { backgroundColor: 'rgba(20,22,26,.92)', padding: 10, cornerRadius: 10, titleFont: { family: 'Inter' }, bodyFont: { family: 'Inter' } }
            },
            scales: isBar ? {
                x: { ticks: { color: t.text, font: { family: 'Inter', weight: '500' } }, grid: { display: false }, border: { display: false } },
                y: { ticks: { color: t.text, font: { family: 'Inter' } }, grid: { color: t.grid }, border: { display: false } }
            } : {}
        }
    });
}
async function loadDashboard() {
    try {
        const d = await apiGet('/api/v1/dashboard'); const e = d.estrutura, f = d.financeiro;
        document.getElementById('kpis').innerHTML = kpi('Condomínios', e.condominios, 'primary') + kpi('Edifícios', e.edificios, 'info') + kpi('Frações', e.fracoes, 'secondary') + kpi('Condóminos', e.condominos, 'success') + kpi('Despesas (€)', Number(f.totalDespesas || 0).toFixed(2), 'warning') + kpi('Reuniões', d.reunioesAgendadas, 'dark');
        chart('chartEstrutura', 'bar', ['Condomínios','Edifícios','Frações','Condóminos','Utilizadores'], [e.condominios, e.edificios, e.fracoes, e.condominos, e.utilizadores], 'Total');
        const qc = f.quotasContagem || {}; chart('chartQuotas', 'doughnut', Object.keys(qc), Object.values(qc), 'Quotas');
        const qv = f.quotasValor || {}; chart('chartValor', 'bar', Object.keys(qv), Object.values(qv).map(Number), '€');
        const oc = d.ocorrenciasPorEstado || {}; chart('chartOcorrencias', 'doughnut', Object.keys(oc), Object.values(oc), 'Ocorrências');
    } catch (e) { showAlert(e.message, 'danger'); }
}

// Reconcilia a vista após uma escrita: recarrega já e volta a recarregar pouco depois,
// para o caso de a réplica do TiDB ainda não refletir a alteração (read-after-write).
// Garante que criações/edições/remoções aparecem de forma fiável mesmo em instância fria.
function recarregar(fn) { try { fn(); } catch (e) {} setTimeout(() => { try { fn(); } catch (e) {} }, 1400); }

// ---------- Condomínios + sub-entidades ----------
async function loadCondominios() {
    document.getElementById('btnNovoCond').classList.toggle('d-none', !can('CONDOMINIOS','CRIAR'));
    try {
        cache.cond = rows(await apiGet('/api/v1/condominios?size=50'));
        renderCondominios();
        document.getElementById('detalhe').innerHTML = '';
    } catch (e) { showAlert(e.message, 'danger'); }
}
function renderCondominios() {
    const q = (document.getElementById('condSearch')?.value || '').toLowerCase();
    const items = cache.cond.filter(c => !q || (c.nome || '').toLowerCase().includes(q) || (c.morada || '').toLowerCase().includes(q));
    document.getElementById('condominiosTable').innerHTML = table(items, c =>
        btn('btn-primary','Visualizar',`verCondominio(${c.id})`) +
        (can('CONDOMINIOS','EDITAR') ? btn('btn-outline-secondary','Editar',`editCondominio(${c.id})`) : '') +
        (can('CONDOMINIOS','APAGAR') ? btn('btn-outline-danger','Apagar',`apagarCondominio(${c.id})`) : ''));
}
function verCondominio(id) {
    const c = cache.cond.find(x => x.id === id); currentCond = { id, nome: c.nome };
    document.getElementById('detalhe').innerHTML = `
      <div class="card"><div class="card-body">
        <h3 class="h6">${esc(c.nome)} <span class="text-secondary small">· orçamento ${Number(c.orcamentoAnual).toFixed(2)} €</span></h3>
        <div class="btn-group btn-group-sm flex-wrap mb-3">
          <button class="btn btn-outline-primary" onclick="loadFracoes()">Frações</button>
          <button class="btn btn-outline-primary" onclick="loadEdificios()">Edifícios</button>
          <button class="btn btn-outline-primary" onclick="loadQuotas()">Quotas</button>
          <button class="btn btn-outline-primary" onclick="loadDespesas()">Despesas</button>
          <button class="btn btn-outline-primary" onclick="loadReunioes()">Reuniões</button>
          <button class="btn btn-outline-primary" onclick="loadDocumentos()">Documentos</button>
          <button class="btn btn-outline-primary" onclick="loadOcorrencias()">Ocorrências</button>
          <button class="btn btn-outline-warning" onclick="loadBalancete()">Balancete</button>
          <button class="btn btn-outline-success" onclick="baixar('pdf')">PDF</button>
          <button class="btn btn-outline-success" onclick="baixar('excel')">Excel</button>
        </div>
        <div id="subDetalhe"></div>
      </div></div>`;
    loadFracoes();
}
function subView(titulo, criarHtml, tabelaHtml, extra='') {
    document.getElementById('subDetalhe').innerHTML =
        `<div class="d-flex justify-content-between align-items-center mb-2"><h4 class="h6 mb-0">${titulo}</h4><div>${criarHtml}</div></div>${tabelaHtml}${extra}`;
}
async function loadFracoes() {
    cache.frac = rows(await apiGet(`/api/v1/fracoes?condominioId=${currentCond.id}&size=50`));
    subView('Frações', btn('btn-success','➕ Nova fração','openFracao()'),
        table(cache.frac, f => btn('btn-outline-info','Condóminos',`loadCondominosDe(${f.id},'${esc(f.numero)}')`) +
            btn('btn-outline-secondary','Editar',`editFracao(${f.id})`) + btn('btn-outline-danger','Apagar',`apagarFracao(${f.id})`)),
        '<h4 class="h6 mt-3 mb-2 text-center">Permilagens de cada Fração</h4><div style="position:relative;height:220px"><canvas id="chartPermilagem"></canvas></div>');
    chart('chartPermilagem', 'bar', cache.frac.map(f => 'Fr ' + f.numero), cache.frac.map(f => Number(f.permilagem)), 'Permilagem (‰)');
}
async function loadEdificios() {
    cache.edif = rows(await apiGet(`/api/v1/edificios?condominioId=${currentCond.id}&size=50`));
    subView('Edifícios', btn('btn-success','➕ Novo edifício','openEdificio()'),
        table(cache.edif, e => btn('btn-outline-secondary','Editar',`editEdificio(${e.id})`) + btn('btn-outline-danger','Apagar',`apagarEdificio(${e.id})`)));
}
async function loadQuotas() {
    cache.quotas = rows(await apiGet(`/api/v1/quotas?condominioId=${currentCond.id}&size=50`));
    subView('Quotas', btn('btn-warning','➕ Gerar quotas','openGerarQuotas()') + (podeGerirOcorrencias() ? btn('btn-outline-info','✉️ Lembretes de atraso','enviarLembretesQuotas()') : ''),
        table(cache.quotas, q => (['PENDENTE','ATRASADO'].includes(q.estado) && can('PAGAMENTOS','CRIAR') ? btn('btn-success','Pagar',`openPagar(${q.id})`) + btn('btn-outline-success','Pagar online',`pagarOnline(${q.id})`) : '—')));
}
async function enviarLembretesQuotas() {
    if (!confirm('Enviar email de lembrete aos condóminos com quotas em ATRASO?')) return;
    try {
        const r = await apiPost(`/api/v1/notificacoes/lembretes-quotas?condominioId=${currentCond.id}`);
        if (r.enviados) {
            const lista = (r.condominos || []).map(n => `<li>${n}</li>`).join('');
            openModal(`Lembretes enviados — ${r.enviados}`, `<p class="small mb-1">Email enviado a:</p><ul class="mb-0">${lista}</ul>`, async () => {}, '', true);
        } else {
            showAlert('Nenhuma quota em atraso — nenhum lembrete enviado.', 'info');
        }
    } catch (e) { showAlert(e.message, 'danger'); }
}
async function loadDespesas() {
    cache.desp = rows(await apiGet(`/api/v1/despesas?condominioId=${currentCond.id}&size=50`));
    subView('Despesas', btn('btn-success','➕ Nova despesa','openDespesa()') + btn('btn-outline-success','PDF',"baixarRelatorio('despesas','pdf')") + btn('btn-outline-success','Excel',"baixarRelatorio('despesas','excel')"),
        table(cache.desp, d => (d.estado === 'PENDENTE' ? btn('btn-outline-success','Aprovar',`aprovarDespesa(${d.id})`) + btn('btn-outline-warning','Rejeitar',`rejeitarDespesa(${d.id})`) : '') +
            btn('btn-outline-secondary','Editar',`editDespesa(${d.id})`) + btn('btn-outline-danger','Apagar',`apagarDespesa(${d.id})`)));
}
async function loadReunioes() {
    cache.reun = rows(await apiGet(`/api/v1/reunioes?condominioId=${currentCond.id}&size=50`));
    subView('Reuniões', (can('REUNIOES','CRIAR') ? btn('btn-success','➕ Nova reunião','openReuniao()') : ''),
        table(cache.reun, r => (can('ATAS','CONSULTAR') ? btn('btn-outline-dark','Atas',`loadAtasDe(${r.id})`) : '') +
            (can('REUNIOES','EDITAR') ? btn('btn-outline-secondary','Editar',`editReuniao(${r.id})`) + btn('btn-outline-info','Convocar',`convocarReuniao(${r.id})`)
                + (r.estado === 'AGENDADA' ? btn('btn-outline-success','Realizada',`realizarReuniao(${r.id})`) + btn('btn-outline-warning','Cancelar',`cancelarReuniao(${r.id})`) : '') : '') +
            (can('REUNIOES','APAGAR') ? btn('btn-outline-danger','Apagar',`apagarReuniao(${r.id})`) : '')));
}
async function realizarReuniao(id) { if (!confirm('Marcar a reunião como realizada?')) return; try { await apiPost(`/api/v1/reunioes/${id}/realizar`); showAlert('Reunião marcada como realizada.', 'success'); recarregar(loadReunioes); } catch (e) { showAlert(e.message, 'danger'); } }
async function cancelarReuniao(id) { if (!confirm('Cancelar a reunião? (não elimina o registo)')) return; try { await apiPost(`/api/v1/reunioes/${id}/cancelar`); showAlert('Reunião cancelada.', 'success'); recarregar(loadReunioes); } catch (e) { showAlert(e.message, 'danger'); } }
async function loadDocumentos() {
    cache.docs = rows(await apiGet(`/api/v1/documentos?condominioId=${currentCond.id}&size=50`));
    subView('Documentos', (can('DOCUMENTOS','CRIAR') ? btn('btn-success','➕ Carregar documento','openDocumento()') : ''),
        table(cache.docs, d => btn('btn-outline-primary','Descarregar',`downloadFile('/api/v1/documentos/${d.id}/download','${esc(d.nome)}')`) +
            (can('DOCUMENTOS','APAGAR') ? btn('btn-outline-danger','Apagar',`apagarDocumento(${d.id})`) : '')));
}
async function loadAtasDe(reuniaoId) {
    currentReuniaoId = reuniaoId;
    cache.atas = rows(await apiGet(`/api/v1/atas?reuniaoId=${reuniaoId}&size=50`));
    renderAtas();
}
// Render a partir de cache.atas (sem refazer fetch). Usado após criar/editar/anexar/apagar
// para refletir a mudança de imediato, imune a atrasos de replicação (read-after-write).
function renderAtas() {
    subView('Atas da reunião #' + currentReuniaoId,
        (can('ATAS','CRIAR') ? btn('btn-success','➕ Nova ata','openAta()') : '') + btn('btn-outline-secondary','‹ Voltar às reuniões','loadReunioes()'),
        table(cache.atas, a => (a.temFicheiro ? btn('btn-outline-primary','Descarregar',`downloadFile('/api/v1/atas/${a.id}/download','${esc(a.titulo || 'ata')}.pdf')`) : '') +
            (can('ATAS','EDITAR') ? btn('btn-outline-info','Anexar ficheiro',`anexarAta(${a.id})`) + btn('btn-outline-secondary','Editar',`editAta(${a.id})`) : '') +
            (can('ATAS','APAGAR') ? btn('btn-outline-danger','Apagar',`apagarAta(${a.id})`) : '')));
}
async function loadCondominosDe(fracaoId, numero) {
    cache.cond_o = rows(await apiGet(`/api/v1/condominos?fracaoId=${fracaoId}&size=50`));
    subView('Condóminos da fração ' + numero,
        (can('CONDOMINOS','CRIAR') ? btn('btn-success','➕ Novo condómino',`openCondomino(${fracaoId})`) : '<span class="small text-secondary">só leitura</span>'),
        table(cache.cond_o, o => (can('CONDOMINOS','EDITAR') ? btn('btn-outline-secondary','Editar',`editCondomino(${o.id})`) : '') +
            (can('CONDOMINOS','APAGAR') ? btn('btn-outline-danger','Apagar',`apagarCondomino(${o.id},${fracaoId},'${esc(numero)}')`) : '')));
}
function baixar(fmt) {
    const isPdf = fmt === 'pdf';
    const path = isPdf ? `/api/v1/relatorios/quotas?condominioId=${currentCond.id}` : `/api/v1/relatorios/quotas/excel?condominioId=${currentCond.id}`;
    const name = `relatorio-quotas-${currentCond.id}.${isPdf ? 'pdf' : 'xlsx'}`;
    const mime = isPdf ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    return downloadComPicker(path, name, mime, 'Relatório guardado.');
}
async function loadBalancete() {
    try {
        const b = await apiGet(`/api/v1/relatorios/balancete?condominioId=${currentCond.id}`);
        const eur = v => Number(v || 0).toFixed(2) + ' €';
        const linha = (rot, val, cls = '') => `<tr class="${cls}"><th class="fw-normal">${rot}</th><td class="text-end">${eur(val)}</td></tr>`;
        const tabela = `<div class="card"><div class="card-body">
            <table class="table table-sm w-auto mb-2">
              ${linha('Orçamento anual', b.orcamentoAnual)}
              ${linha('Receitas — quotas pagas', b.quotasPagas, 'table-success')}
              ${linha('Quotas por cobrar (pendentes/atraso)', b.quotasPorCobrar)}
              ${linha('Despesas aprovadas', b.despesasAprovadas, 'table-danger')}
              ${linha('Despesas pendentes', b.despesasPendentes)}
              ${linha('Saldo (pagas − despesas aprovadas)', b.saldo, Number(b.saldo) >= 0 ? 'table-success' : 'table-danger')}
              ${linha('Fundo de reserva (≥10% das quotas)', b.fundoReserva, 'table-info')}
            </table>
            <p class="small text-secondary mb-2">Fundo de reserva obrigatório (≥10% das quotas) — Lei 8/2022.</p>
            ${btn('btn-outline-success', '⬇ Balancete em PDF', `downloadComPicker('/api/v1/relatorios/balancete/pdf?condominioId=${currentCond.id}','balancete-${currentCond.id}.pdf','application/pdf','Balancete guardado.')`)}
          </div></div>`;
        subView('Balancete — ' + esc(currentCond.nome), '', tabela);
    } catch (e) { showAlert(e.message, 'danger'); }
}
// Relatórios por tipo (despesas, ocorrencias) em PDF/Excel.
function baixarRelatorio(tipo, fmt) {
    const isPdf = fmt === 'pdf';
    const path = `/api/v1/relatorios/${tipo}${isPdf ? '' : '/excel'}?condominioId=${currentCond.id}`;
    const mime = isPdf ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
    return downloadComPicker(path, `relatorio-${tipo}-${currentCond.id}.${isPdf ? 'pdf' : 'xlsx'}`, mime, 'Relatório guardado.');
}

const VOT_BADGE = { CRIADA: 'secondary', ABERTA: 'success', ENCERRADA: 'dark' };
async function loadVotacoes() {
    try {
        document.getElementById('btnNovaVotacao').classList.toggle('d-none', !can('VOTACOES', 'CRIAR'));
        cache.votacoes = rows(await apiGet('/api/v1/votacoes?size=50'));
        renderVotacoes();
        document.getElementById('resultadoVotacao').innerHTML = '';
    } catch (e) { showAlert(e.message, 'danger'); }
}
function renderVotacoes() {
    const q = (document.getElementById('votSearch')?.value || '').toLowerCase();
    const podeCriar = can('VOTACOES', 'CRIAR'), podeEditar = can('VOTACOES', 'EDITAR');
    const lista = cache.votacoes.filter(v => !q || (v.tema || '').toLowerCase().includes(q) || (v.estado || '').toLowerCase().includes(q));
    const linhas = lista.map(v => ({ id: v.id, Tema: v.tema, Maioria: String(v.tipoMaioria).replace(/_/g, ' '),
        Estado: `<span class="badge text-bg-${VOT_BADGE[v.estado] || 'secondary'}">${v.estado}</span>` }));
    document.getElementById('votacoesTable').innerHTML = table(linhas, l => {
        const v = cache.votacoes.find(x => x.id === l.id); let b = '';
        if (v.estado === 'CRIADA' && podeEditar) b += btn('btn-outline-success', 'Abrir', `abrirVotacao(${v.id})`);
        if (v.estado === 'ABERTA' && podeCriar) b += btn('btn-success', 'Votar', `votarVotacao(${v.id})`);
        if (v.estado === 'ABERTA' && podeEditar) b += btn('btn-outline-warning', 'Encerrar', `encerrarVotacao(${v.id})`);
        return b + btn('btn-outline-primary', 'Resultado', `loadResultado(${v.id})`) + (podeEditar ? btn('btn-outline-danger', 'Apagar', `apagarVotacao(${v.id})`) : '');
    });
}
async function openVotacao() {
    let reun = []; try { reun = rows(await apiGet('/api/v1/reunioes?size=100')); } catch (e) {}
    const reunOpts = reun.map(r => ({ value: r.id, label: `Reunião #${r.id} — ${r.data}${r.local ? ' (' + r.local + ')' : ''}` }));
    openModal('Nova votação',
        selectField('reuniaoId', 'Reunião', reunOpts.length ? reunOpts : [{ value: '', label: '(sem reuniões)' }])
        + field('tema', 'Tema', 'text', 'required')
        + selectField('tipoMaioria', 'Maioria exigida', [{ value: 'MAIORIA_SIMPLES', label: 'Maioria simples (>50% presente)' }, { value: 'SEM_OPOSICAO', label: 'Sem oposição' }, { value: 'DOIS_TERCOS', label: 'Dois terços do capital' }, { value: 'UNANIMIDADE', label: 'Unanimidade' }]),
        async () => { const d = formData(); if (!d.reuniaoId) throw new Error('Escolha uma reunião.'); await apiPost('/api/v1/votacoes', { reuniaoId: Number(d.reuniaoId), tema: d.tema, tipoMaioria: d.tipoMaioria }); showAlert('Votação criada.', 'success'); recarregar(loadVotacoes); });
}
async function abrirVotacao(id) { try { await apiPost(`/api/v1/votacoes/${id}/abrir`); showAlert('Votação aberta — pode receber votos.', 'success'); recarregar(loadVotacoes); } catch (e) { showAlert(e.message, 'danger'); } }
async function encerrarVotacao(id) { if (!confirm('Encerrar a votação? Deixa de aceitar votos.')) return; try { await apiPost(`/api/v1/votacoes/${id}/encerrar`); showAlert('Votação encerrada.', 'success'); recarregar(loadVotacoes); recarregar(() => loadResultado(id)); } catch (e) { showAlert(e.message, 'danger'); } }
async function condominosDoCondominio(condominioId) {
    const fr = rows(await apiGet(`/api/v1/fracoes?condominioId=${condominioId}&size=100`));
    const listas = await Promise.all(fr.map(f => apiGet(`/api/v1/condominos?fracaoId=${f.id}&size=100`).then(rows).then(cs => cs.map(c => ({ ...c, _fr: f.numero }))).catch(() => [])));
    return listas.flat();
}
async function votarVotacao(id) {
    const v = cache.votacoes.find(x => x.id === id);
    let condos = [];
    try { const reuniao = await apiGet(`/api/v1/reunioes/${v.reuniaoId}`); condos = await condominosDoCondominio(reuniao.condominioId); } catch (e) {}
    const condOpts = condos.map(c => ({ value: c.id, label: `${c.nome}${c._fr ? ' · Fr ' + c._fr : ''}` }));
    openModal('Registar voto — ' + (v.tema || ''),
        selectField('condominoId', 'Condómino', condOpts.length ? condOpts : [{ value: '', label: '(sem condóminos)' }])
        + selectField('resposta', 'Sentido do voto', [{ value: 'SIM', label: 'A favor (SIM)' }, { value: 'NAO', label: 'Contra (NÃO)' }, { value: 'ABSTENCAO', label: 'Abstenção' }]),
        async () => { const d = formData(); if (!d.condominoId) throw new Error('Escolha um condómino.'); await apiPost(`/api/v1/votacoes/${id}/votos`, { condominoId: Number(d.condominoId), resposta: d.resposta }); showAlert('Voto registado.', 'success'); recarregar(() => loadResultado(id)); },
        'O voto conta por <strong>permilagem</strong> da fração (Lei 8/2022). Cada condómino vota uma vez.');
}
async function loadResultado(id) {
    try {
        const r = await apiGet(`/api/v1/votacoes/${id}/resultado`);
        const aprov = r.aprovado ? '<span class="badge bg-success">APROVADO</span>' : '<span class="badge bg-danger">REPROVADO</span>';
        document.getElementById('resultadoVotacao').innerHTML = `<div class="card"><div class="card-body"><h3 class="h6">Resultado — ${esc(r.tema)} ${aprov}</h3>
            <p class="small text-secondary mb-2">Maioria: ${r.tipoMaioria} · Estado: ${r.estado}</p>
            <table class="table table-sm w-auto"><tr><th>Capital total</th><td>${r.capitalTotal} ‰</td></tr><tr><th>Presente</th><td>${r.capitalPresente} ‰</td></tr>
            <tr class="table-success"><th>SIM</th><td>${r.somaSim} ‰</td></tr><tr class="table-danger"><th>NÃO</th><td>${r.somaNao} ‰</td></tr>
            <tr><th>Abstenção</th><td>${r.somaAbstencao} ‰</td></tr><tr><th>Nº votos</th><td>${r.numeroVotos}</td></tr></table>
            <p class="small text-secondary mb-0">Contagem por <strong>permilagem</strong> — Lei 8/2022.</p></div></div>`;
    } catch (e) { showAlert(e.message, 'danger'); }
}

// ---------- Auditoria (admin/gestor, só leitura) ----------
let auditPage = 0, auditTotalPaginas = 1, auditTotal = 0;
const MET_BADGE = { LOGIN: 'secondary', GET: 'info', POST: 'success', PUT: 'warning', DELETE: 'danger' };
function parseOperacao(op) {
    if (op === 'LOGIN') return { metodo: 'LOGIN', recurso: 'Início de sessão' };
    const i = String(op).indexOf(' ');
    return i > 0 ? { metodo: op.slice(0, i), recurso: op.slice(i + 1) } : { metodo: '—', recurso: String(op) };
}
async function loadAuditoria() { auditPage = 0; cache.audit = []; await fetchAuditPage(); }
async function loadMaisAuditoria() { auditPage++; await fetchAuditPage(); }
async function fetchAuditPage() {
    try {
        const p = await apiGet(`/api/v1/auditoria?size=50&page=${auditPage}&sort=dataHora,desc`);
        auditTotalPaginas = p.totalPaginas || 1; auditTotal = p.totalElementos || 0;
        cache.audit = cache.audit.concat(p.conteudo || p.content || []);
        renderAuditoria();
        document.getElementById('auditMais').classList.toggle('d-none', auditPage >= auditTotalPaginas - 1);
    } catch (e) { showAlert(e.message, 'danger'); }
}
function renderAuditoria() {
    const q = (document.getElementById('auditSearch')?.value || '').toLowerCase();
    const met = document.getElementById('auditMetodo')?.value || '';
    const proc = cache.audit.map(h => { const o = parseOperacao(h.operacao); return { ...h, _m: o.metodo, _r: o.recurso }; })
        .filter(h => (!met || h._m === met) && (!q || (h.utilizador || '').toLowerCase().includes(q) || (h._r || '').toLowerCase().includes(q)));
    const linhas = proc.map(h => ({
        'Data/hora': h.dataHora ? String(h.dataHora).replace('T', ' ').slice(0, 19) : '—',
        Utilizador: h.utilizador || '—',
        Método: `<span class="badge text-bg-${MET_BADGE[h._m] || 'secondary'}">${h._m}</span>`,
        Recurso: h._r
    }));
    document.getElementById('auditoriaTable').innerHTML = linhas.length ? table(linhas, null) : '<p class="text-secondary">Sem registos para o filtro.</p>';
    const utilizadores = new Set(cache.audit.map(a => a.utilizador)).size;
    document.getElementById('auditResumo').textContent = `${proc.length}/${cache.audit.length} mostrados · ${auditTotal} no total · ${utilizadores} utilizadores`;
}
// ---------- Empresas (admin) ----------
async function loadEmpresas() {
    try {
        cache.empresas = rows(await apiGet('/api/v1/empresas?size=50'));
        renderEmpresas();
    } catch (e) { showAlert(e.message, 'danger'); }
}
function renderEmpresas() {
    const q = (document.getElementById('empSearch')?.value || '').toLowerCase();
    const lista = cache.empresas.filter(em => !q || (em.nome || '').toLowerCase().includes(q) || (em.nif || '').toLowerCase().includes(q) || (em.email || '').toLowerCase().includes(q));
    const linhas = lista.map(em => ({ id: em.id, Nome: em.nome, NIF: em.nif || '—', Email: em.email || '—', Telefone: em.telefone || '—', Morada: em.morada || '—' }));
    document.getElementById('empresasTable').innerHTML = table(linhas, em => btn('btn-outline-secondary', 'Editar', `editEmpresa(${em.id})`) + btn('btn-outline-danger', 'Desativar', `desativarEmpresa(${em.id})`));
}
function openEmpresa() {
    openModal('Nova empresa', field('nome', 'Nome', 'text', 'required') + field('nif', 'NIF') + field('email', 'Email', 'email') + field('telefone', 'Telefone') + field('morada', 'Morada'),
        async () => { const d = formData(); await apiPost('/api/v1/empresas', { nome: d.nome, nif: d.nif, email: d.email, telefone: d.telefone, morada: d.morada }); showAlert('Empresa criada.', 'success'); recarregar(loadEmpresas); });
}
function editEmpresa(id) {
    const em = cache.empresas.find(x => x.id === id);
    openModal('Editar empresa', field('nome', 'Nome', 'text', 'required', esc(em.nome)) + field('email', 'Email', 'email', '', esc(em.email || '')) + field('telefone', 'Telefone', 'text', '', esc(em.telefone || '')) + field('morada', 'Morada', 'text', '', esc(em.morada || '')),
        async () => { const d = formData(); await apiPut('/api/v1/empresas/' + id, { nome: d.nome, email: d.email, telefone: d.telefone, morada: d.morada }); showAlert('Empresa atualizada.', 'success'); recarregar(loadEmpresas); });
}
async function desativarEmpresa(id) { if (!confirm('Desativar esta empresa?')) return; try { await apiDel('/api/v1/empresas/' + id); showAlert('Empresa desativada.', 'success'); recarregar(loadEmpresas); } catch (e) { showAlert(e.message, 'danger'); } }

async function loadUtilizadores() {
    try { cache.users = rows(await apiGet('/api/v1/utilizadores?size=100')); renderUtilizadores(); } catch (e) { showAlert(e.message, 'danger'); }
}
function renderUtilizadores() {
    const q = (document.getElementById('userSearch').value || '').toLowerCase();
    const items = cache.users.filter(u => !q || (u.nome || '').toLowerCase().includes(q) || (u.email || '').toLowerCase().includes(q) || (u.perfis || []).join(',').toLowerCase().includes(q));
    document.getElementById('utilizadoresTable').innerHTML = table(items, u => btn('btn-outline-secondary','Editar',`editUtilizador(${u.id})`) +
        btn('btn-outline-info','Condómino',`associarUtilizadorCondomino(${u.id})`) +
        (u.ativo ? btn('btn-outline-danger','Desativar',`toggleAtivoUtilizador(${u.id},false)`) : btn('btn-outline-success','Ativar',`toggleAtivoUtilizador(${u.id},true)`)));
}
async function associarUtilizadorCondomino(id) {
    let conds = []; try { conds = rows(await apiGet('/api/v1/condominios?size=100')); } catch (e) {}
    const condOpts = conds.map(c => ({ value: c.id, label: c.nome }));
    openModal('Associar condómino ao utilizador',
        selectField('condominioId', 'Condomínio', condOpts.length ? condOpts : [{ value: '', label: '(sem condomínios)' }])
        + '<div id="condoWrap">' + selectField('condominoId', 'Condómino', [{ value: '', label: 'Escolha primeiro o condomínio' }]) + '</div>',
        async () => { const d = formData(); if (!d.condominoId) throw new Error('Escolha um condómino.'); await apiPut('/api/v1/utilizadores/' + id + '/condomino', { condominoId: Number(d.condominoId) }); showAlert('Condómino associado ao utilizador.', 'success'); });
    const popular = async (cid) => {
        const wrap = document.getElementById('condoWrap'); if (!wrap) return;
        if (!cid) { wrap.innerHTML = selectField('condominoId', 'Condómino', [{ value: '', label: '(escolha o condomínio)' }]); return; }
        const cs = await condominosDoCondominio(cid).catch(() => []);
        const o = cs.map(c => ({ value: c.id, label: `${c.nome}${c._fr ? ' · Fr ' + c._fr : ''}` }));
        wrap.innerHTML = selectField('condominoId', 'Condómino', o.length ? o : [{ value: '', label: '(sem condóminos)' }]);
    };
    const sel = document.querySelector('#modalForm [name=condominioId]');
    if (sel) { sel.addEventListener('change', e => popular(e.target.value)); if (condOpts.length) popular(condOpts[0].value); }
}
async function fillMatrizUser(perfil) {
    const box = document.getElementById('matrizPerm'); if (!box) return;
    try { box.innerHTML = matrizHTML(await apiGet('/api/v1/permissoes/' + perfil), false); } catch { box.innerHTML = '<p class="small text-secondary">—</p>'; }
}
function wirePerfilMatriz() { const s = document.querySelector('#modalForm [name=perfil]'); if (s) s.addEventListener('change', e => fillMatrizUser(e.target.value)); }
function blocoMatriz() { return '<label class="form-label small mt-2">Permissões do perfil (geridas no separador Permissões)</label><div id="matrizPerm" class="border rounded p-2" style="max-height:230px;overflow:auto"><p class="small text-secondary mb-0">a carregar…</p></div>'; }
function openNovoAcesso() {
    const perfis = perfisDisponiveis(); const def = perfis[0].value;
    openModal('Criar novo acesso', field('nome','Nome','text','required') + field('email','Email','email','required') + field('password','Password (mín. 6)','password','minlength="6" required') + selectField('perfil','Perfil',perfis.map(p=>({value:p.value,label:p.label})),def) + blocoMatriz(),
        async () => { const d = formData(); await apiPost('/api/v1/utilizadores', { nome: d.nome, email: d.email, password: d.password, perfis: [d.perfil] }); showAlert('Acesso criado.', 'success'); recarregar(loadUtilizadores); }, '', true);
    wirePerfilMatriz(); fillMatrizUser(def);
}
function editUtilizador(id) {
    const u = cache.users.find(x => x.id === id); const perfis = perfisDisponiveis(); const cur = (u.perfis && u.perfis[0]) || 'CONDOMINO';
    openModal('Editar utilizador', field('nome','Nome','text','required',esc(u.nome)) + `<div class="mb-2"><label class="form-label small">Email (imutável)</label><input class="form-control form-control-sm" value="${esc(u.email)}" disabled></div>` + selectField('ativo','Estado',[{value:'true',label:'Ativo'},{value:'false',label:'Inativo'}],String(u.ativo)) + selectField('perfil','Perfil',perfis.map(p=>({value:p.value,label:p.label})),cur) + blocoMatriz(),
        async () => { const d = formData(); await apiPut(`/api/v1/utilizadores/${id}`, { nome: d.nome, ativo: d.ativo === 'true', perfis: [d.perfil] }); showAlert('Utilizador atualizado.', 'success'); recarregar(loadUtilizadores); }, '', true);
    wirePerfilMatriz(); fillMatrizUser(cur);
}
async function toggleAtivoUtilizador(id, ativar) {
    const u = cache.users.find(x => x.id === id);
    if (!confirm(ativar ? 'Ativar este utilizador?' : 'Desativar este utilizador? (fica inativo, não é removido — pode reativá-lo depois)')) return;
    try {
        if (ativar) await apiPut(`/api/v1/utilizadores/${id}`, { nome: u.nome, ativo: true, perfis: u.perfis });
        else await apiDel(`/api/v1/utilizadores/${id}`);
        showAlert(ativar ? 'Utilizador ativado.' : 'Utilizador desativado.', 'success'); recarregar(loadUtilizadores);
    } catch (e) { showAlert(e.message, 'danger'); }
}

let permInit = false;
async function loadPermPerfis() {
    const sel = document.getElementById('permPerfil');
    if (!permInit) { const perfis = await apiGet('/api/v1/permissoes/perfis'); sel.innerHTML = perfis.map(p => `<option value="${p}">${p}</option>`).join(''); document.getElementById('permReadonlyNote').classList.toggle('d-none', isAdmin()); document.getElementById('permSave').classList.toggle('d-none', !isAdmin()); permInit = true; }
    loadPermissoes();
}
async function loadPermissoes() { const p = document.getElementById('permPerfil').value; if (!p) return; try { document.getElementById('permMatriz').innerHTML = matrizHTML(await apiGet('/api/v1/permissoes/' + p), isAdmin()); } catch (e) { showAlert(e.message, 'danger'); } }
async function savePermissoes() {
    const perfil = document.getElementById('permPerfil').value; const body = {};
    FUNCS.forEach(([fk]) => { const acoes = ACS.filter(([ak]) => { const el = document.getElementById(`pm_${fk}_${ak}`); return el && el.checked; }).map(([ak]) => ak); if (acoes.length) body[fk] = acoes; });
    try { await apiPut('/api/v1/permissoes/' + perfil, body); showAlert('Permissões guardadas e aplicadas.', 'success'); if (perfil === (user().perfis||[]).map(r=>r.replace('ROLE_','')).find(x=>x)) { myPerms = await apiGet('/api/v1/permissoes/me'); } } catch (e) { showAlert(e.message, 'danger'); }
}

// ---------- Modais ----------
function field(name, label, type='text', extra='', value='') { return `<div class="mb-2"><label class="form-label small">${label}</label><input name="${name}" type="${type}" class="form-control form-control-sm" ${extra} value="${value}"></div>`; }
function selectField(name, label, options, value='') { let o = options.map(x => `<option value="${x.value}" ${String(x.value) === String(value) ? 'selected' : ''}>${x.label}</option>`).join(''); return `<div class="mb-2"><label class="form-label small">${label}</label><select name="${name}" class="form-select form-select-sm">${o}</select></div>`; }
function opts(arr) { return arr.map(v => ({ value: v, label: v })); }
function openModal(title, fieldsHtml, submitFn, info='', large=false) {
    document.getElementById('modalDialog').className = 'modal-dialog' + (large ? ' modal-lg' : '');
    document.getElementById('modalTitle').textContent = title; document.getElementById('modalFields').innerHTML = fieldsHtml; document.getElementById('modalError').classList.add('d-none');
    const inf = document.getElementById('modalInfo'); if (info) { inf.innerHTML = info; inf.classList.remove('d-none'); } else inf.classList.add('d-none');
    onSubmitModal = submitFn; modal().show();
}
function formData() { const o = {}; new FormData(document.getElementById('modalForm')).forEach((v, k) => o[k] = v); return o; }

function openCondominio() { openModal('Novo condomínio', field('nome','Nome','text','required') + field('morada','Morada') + field('orcamentoAnual','Orçamento anual (€)','number','step="0.01" min="0" required'), async () => { const d = formData(); await apiPost('/api/v1/condominios', { nome: d.nome, morada: d.morada, orcamentoAnual: Number(d.orcamentoAnual) }); showAlert('Condomínio criado.', 'success'); recarregar(loadCondominios); }); }
function editCondominio(id) { const c = cache.cond.find(x => x.id === id); openModal('Editar condomínio', field('nome','Nome','text','required',esc(c.nome)) + field('morada','Morada','text','',esc(c.morada || '')) + field('orcamentoAnual','Orçamento anual (€)','number','step="0.01" min="0" required',c.orcamentoAnual), async () => { const d = formData(); await apiPut(`/api/v1/condominios/${id}`, { nome: d.nome, morada: d.morada, orcamentoAnual: Number(d.orcamentoAnual) }); showAlert('Condomínio atualizado.', 'success'); recarregar(loadCondominios); }); }
async function apagarCondominio(id) { if (!confirm('Apagar este condomínio?')) return; try { await apiDel(`/api/v1/condominios/${id}`); showAlert('Condomínio apagado.', 'success'); recarregar(loadCondominios); } catch (e) { showAlert(e.message, 'danger'); } }

function openEdificio() { openModal(`Novo edifício — ${currentCond.nome}`, field('nome','Nome','text','required') + field('bloco','Bloco') + field('numeroPisos','Nº de pisos','number','min="0"'), async () => { const d = formData(); await apiPost('/api/v1/edificios', { condominioId: currentCond.id, nome: d.nome, bloco: d.bloco, numeroPisos: d.numeroPisos ? Number(d.numeroPisos) : null }); showAlert('Edifício criado.', 'success'); recarregar(loadEdificios); }); }
function editEdificio(id) { const e = cache.edif.find(x => x.id === id); openModal('Editar edifício', field('nome','Nome','text','required',esc(e.nome)) + field('bloco','Bloco','text','',esc(e.bloco || '')) + field('numeroPisos','Nº de pisos','number','min="0"',e.numeroPisos ?? ''), async () => { const d = formData(); await apiPut(`/api/v1/edificios/${id}`, { nome: d.nome, bloco: d.bloco, numeroPisos: d.numeroPisos ? Number(d.numeroPisos) : null }); showAlert('Edifício atualizado.', 'success'); recarregar(loadEdificios); }); }
async function apagarEdificio(id) { if (!confirm('Apagar este edifício?')) return; try { await apiDel(`/api/v1/edificios/${id}`); showAlert('Edifício apagado.', 'success'); recarregar(loadEdificios); } catch (e) { showAlert(e.message, 'danger'); } }

async function openFracao() {
    const eds = rows(await apiGet(`/api/v1/edificios?condominioId=${currentCond.id}&size=50`));
    if (!eds.length) { showAlert('Crie primeiro um edifício.', 'warning'); return; }
    openModal(`Nova fração — ${currentCond.nome}`, selectField('edificioId','Edifício',eds.map(e=>({value:e.id,label:e.nome}))) + field('numero','Nº da fração','text','required') + field('tipologia','Tipologia (ex.: T2)') + field('permilagem','Permilagem (‰, 0–1000)','number','step="0.0001" min="0" max="1000" required') + field('areaM2','Área (m²)','number','step="0.01" min="0"'),
        async () => { const d = formData(); await apiPost('/api/v1/fracoes', { condominioId: currentCond.id, edificioId: Number(d.edificioId), numero: d.numero, tipologia: d.tipologia, permilagem: Number(d.permilagem), areaM2: d.areaM2 ? Number(d.areaM2) : null }); showAlert('Fração criada.', 'success'); recarregar(loadFracoes); });
}
function editFracao(id) { const f = cache.frac.find(x => x.id === id); openModal('Editar fração', field('numero','Nº','text','required',esc(f.numero)) + field('tipologia','Tipologia','text','',esc(f.tipologia || '')) + field('permilagem','Permilagem (‰)','number','step="0.0001" min="0" max="1000" required',f.permilagem) + field('areaM2','Área (m²)','number','step="0.01" min="0"',f.areaM2 || ''), async () => { const d = formData(); await apiPut(`/api/v1/fracoes/${id}`, { numero: d.numero, tipologia: d.tipologia, permilagem: Number(d.permilagem), areaM2: d.areaM2 ? Number(d.areaM2) : null }); showAlert('Fração atualizada.', 'success'); recarregar(loadFracoes); }); }
async function apagarFracao(id) { if (!confirm('Apagar esta fração?')) return; try { await apiDel(`/api/v1/fracoes/${id}`); showAlert('Fração apagada.', 'success'); recarregar(loadFracoes); } catch (e) { showAlert(e.message, 'danger'); } }

function openCondomino(fracaoId) { openModal('Novo condómino', field('nome','Nome','text','required') + field('nif','NIF') + field('email','Email','email') + field('telefone','Telefone') + selectField('tipo','Tipo',opts(['PROPRIETARIO','INQUILINO'])), async () => { const d = formData(); await apiPost('/api/v1/condominos', { fracaoId, nome: d.nome, nif: d.nif, email: d.email, telefone: d.telefone, tipo: d.tipo }); showAlert('Condómino criado.', 'success'); recarregar(() => loadCondominosDe(fracaoId, '')); }); }
function editCondomino(id) { const o = cache.cond_o.find(x => x.id === id); openModal('Editar condómino', field('nome','Nome','text','required',esc(o.nome)) + field('nif','NIF','text','',esc(o.nif || '')) + field('email','Email','email','',esc(o.email || '')) + field('telefone','Telefone','text','',esc(o.telefone || '')) + selectField('tipo','Tipo',opts(['PROPRIETARIO','INQUILINO']),o.tipo), async () => { const d = formData(); await apiPut(`/api/v1/condominos/${id}`, { nome: d.nome, nif: d.nif, email: d.email, telefone: d.telefone, tipo: d.tipo }); showAlert('Condómino atualizado.', 'success'); recarregar(() => loadCondominosDe(o.fracaoId, '')); }); }
async function apagarCondomino(id, fracaoId, numero) { if (!confirm('Apagar este condómino?')) return; try { await apiDel(`/api/v1/condominos/${id}`); showAlert('Condómino apagado.', 'success'); recarregar(() => loadCondominosDe(fracaoId, numero)); } catch (e) { showAlert(e.message, 'danger'); } }

function openDespesa() { openModal(`Nova despesa — ${currentCond.nome}`, field('descricao','Descrição','text','required') + selectField('categoria','Categoria',opts(['MANUTENCAO','LIMPEZA','SEGURO','AGUA','ELETRICIDADE','OUTROS'])) + field('valor','Valor (€)','number','step="0.01" min="0.01" required') + field('dataDespesa','Data','date','required'), async () => { const d = formData(); await apiPost('/api/v1/despesas', { condominioId: currentCond.id, descricao: d.descricao, categoria: d.categoria, valor: Number(d.valor), dataDespesa: d.dataDespesa }); showAlert('Despesa criada.', 'success'); recarregar(loadDespesas); }); }
function editDespesa(id) { const x = cache.desp.find(d => d.id === id); openModal('Editar despesa', field('descricao','Descrição','text','required',esc(x.descricao)) + selectField('categoria','Categoria',opts(['MANUTENCAO','LIMPEZA','SEGURO','AGUA','ELETRICIDADE','OUTROS']),x.categoria) + field('valor','Valor (€)','number','step="0.01" min="0.01" required',x.valor) + field('dataDespesa','Data','date','required',x.dataDespesa), async () => { const d = formData(); await apiPut(`/api/v1/despesas/${id}`, { descricao: d.descricao, categoria: d.categoria, valor: Number(d.valor), dataDespesa: d.dataDespesa }); showAlert('Despesa atualizada.', 'success'); recarregar(loadDespesas); }); }
async function aprovarDespesa(id) { try { await apiPost(`/api/v1/despesas/${id}/aprovar`); showAlert('Despesa aprovada.', 'success'); recarregar(loadDespesas); } catch (e) { showAlert(e.message, 'danger'); } }
async function rejeitarDespesa(id) { try { await apiPost(`/api/v1/despesas/${id}/rejeitar`); showAlert('Despesa rejeitada.', 'success'); recarregar(loadDespesas); } catch (e) { showAlert(e.message, 'danger'); } }
async function apagarDespesa(id) { if (!confirm('Apagar esta despesa?')) return; try { await apiDel(`/api/v1/despesas/${id}`); showAlert('Despesa apagada.', 'success'); recarregar(loadDespesas); } catch (e) { showAlert(e.message, 'danger'); } }

function openReuniao() { openModal(`Nova reunião — ${currentCond.nome}`, field('data','Data','date','required') + field('hora','Hora','time') + field('local','Local') + field('ordemTrabalhos','Ordem de trabalhos'), async () => { const d = formData(); await apiPost('/api/v1/reunioes', { condominioId: currentCond.id, data: d.data, hora: d.hora || null, local: d.local, ordemTrabalhos: d.ordemTrabalhos }); showAlert('Reunião agendada.', 'success'); recarregar(loadReunioes); }); }
function editReuniao(id) { const r = cache.reun.find(x => x.id === id); openModal('Editar reunião', field('data','Data','date','required',r.data) + field('hora','Hora','time','',r.hora || '') + field('local','Local','text','',esc(r.local || '')) + field('ordemTrabalhos','Ordem de trabalhos','text','',esc(r.ordemTrabalhos || '')), async () => { const d = formData(); await apiPut(`/api/v1/reunioes/${id}`, { data: d.data, hora: d.hora || null, local: d.local, ordemTrabalhos: d.ordemTrabalhos }); showAlert('Reunião atualizada.', 'success'); recarregar(loadReunioes); }); }
async function convocarReuniao(id) {
    if (!confirm('Enviar convocatória por email aos condóminos do condomínio?')) return;
    try {
        const r = await apiPost(`/api/v1/reunioes/${id}/convocar`);
        const linhas = (r.destinatarios || []).map(d => `<tr><td>${d.nome || '—'}</td><td>${d.email}</td></tr>`).join('');
        const tabela = (r.destinatarios && r.destinatarios.length)
            ? `<p class="small mb-1">Email enviado a estes condóminos:</p><table class="table table-sm table-striped"><thead><tr><th>Condómino</th><th>Email</th></tr></thead><tbody>${linhas}</tbody></table>`
            : '<p class="text-secondary">Nenhum condómino com email definido — não foi enviado nenhum email.</p>';
        const aviso = r.semEmail ? `<p class="small text-warning mb-2">⚠️ ${r.semEmail} condómino(s) sem email não foram contactados.</p>` : '';
        openModal(`Convocatória enviada — ${r.emailsEnviados} email${r.emailsEnviados === 1 ? '' : 's'}`, aviso + tabela, async () => {}, '', true);
    } catch (e) { showAlert(e.message, 'danger'); }
}
async function apagarReuniao(id) { if (!confirm('Apagar esta reunião?')) return; try { await apiDel(`/api/v1/reunioes/${id}`); showAlert('Reunião apagada.', 'success'); recarregar(loadReunioes); } catch (e) { showAlert(e.message, 'danger'); } }

function openGerarQuotas() { const now = new Date(); openModal(`Gerar quotas — ${currentCond.nome}`, field('mes','Mês (1–12)','number','min="1" max="12" required',now.getMonth() + 1) + field('ano','Ano','number','min="2000" max="2100" required',now.getFullYear()), async () => { const d = formData(); const r = await apiPost('/api/v1/quotas/gerar', { condominioId: currentCond.id, mes: Number(d.mes), ano: Number(d.ano) }); showAlert(`Quotas geradas: ${r.quotasGeradas} (total ${Number(r.valorTotal).toFixed(2)} €).`, 'success'); recarregar(loadQuotas); }); }
function openPagar(quotaId) { const q = cache.quotas.find(x => x.id === quotaId); openModal('Registar pagamento', field('valor','Valor (€)','number','step="0.01" min="0.01" required',q.valor) + selectField('metodo','Método',opts(['TRANSFERENCIA','MBWAY','MULTIBANCO','PAYPAL','DINHEIRO'])), async () => { const d = formData(); await apiPost('/api/v1/pagamentos', { quotaId, valor: Number(d.valor), metodo: d.metodo }); showAlert('Pagamento registado.', 'success'); recarregar(loadQuotas); }, `Quota #${quotaId} — ${q.mes}/${q.ano}`); }
async function pagarOnline(quotaId) {
    try {
        const r = await apiPost(`/api/v1/pagamentos/online/iniciar?quotaId=${quotaId}`);
        openModal('Pagar online — Multibanco',
            `<table class="table table-sm w-auto mb-2">
               <tr><th>Entidade</th><td><code>${r.entidade}</code></td></tr>
               <tr><th>Referência</th><td><code>${r.referencia}</code></td></tr>
               <tr><th>Valor</th><td>${Number(r.valor).toFixed(2)} €</td></tr></table>
             <p class="small text-secondary mb-2">${r.instrucoes}</p>
             <div class="alert alert-warning small mb-0">${r.nota}</div>`,
            async () => { await apiPost(`/api/v1/pagamentos/online/confirmar?quotaId=${quotaId}`); showAlert('Pagamento confirmado.', 'success'); recarregarVistaPagamentos(); },
            'Carregue em «Guardar» para simular a confirmação do pagamento (callback do gateway).');
    } catch (e) { showAlert(e.message, 'danger'); }
}
// Recarrega a vista de quotas certa (gestor: loadQuotas; portal do condómino: loadPortal).
function recarregarVistaPagamentos() {
    const ativo = document.querySelector('.nav-link.active')?.dataset.tab;
    if (ativo === 'portal') { loadPortal(); } else { recarregar(loadQuotas); }
}
// ---------- Portal do Condómino (self-service) ----------
async function loadPortal() {
    const cont = document.getElementById('portalConteudo');
    cont.innerHTML = '<p class="text-secondary">A carregar a sua área…</p>';
    try {
        const ctx = await apiGet('/api/v1/me/contexto');
        const quotas = await apiGet('/api/v1/me/quotas');
        let votacoes = [];
        try { votacoes = rows(await apiGet('/api/v1/votacoes?size=50')).filter(v => v.estado === 'ABERTA'); } catch (e) {}
        const eur = v => Number(v || 0).toFixed(2) + ' €';
        const qBadge = { PENDENTE: 'warning', PAGO: 'success', ATRASADO: 'danger', ANULADO: 'secondary' };
        const quotasHtml = quotas.length
            ? '<table class="table table-sm align-middle"><thead><tr><th>Mês/Ano</th><th>Valor</th><th>Estado</th><th class="text-end">Ações</th></tr></thead><tbody>'
              + quotas.map(q => `<tr><td>${q.mes}/${q.ano}</td><td>${eur(q.valor)}</td><td><span class="badge text-bg-${qBadge[q.estado] || 'secondary'}">${q.estado}</span></td><td class="text-end">${['PENDENTE', 'ATRASADO'].includes(q.estado) ? btn('btn-outline-success', 'Pagar online', `pagarOnline(${q.id})`) : '—'}</td></tr>`).join('')
              + '</tbody></table>'
            : '<p class="text-secondary mb-0">Sem quotas registadas.</p>';
        const votHtml = votacoes.length
            ? votacoes.map(v => `<div class="d-flex justify-content-between align-items-center border rounded p-2 mb-2"><div><strong>${v.tema}</strong> <span class="small text-secondary">· ${String(v.tipoMaioria).replace(/_/g, ' ')}</span></div>${btn('btn-success', 'Votar', `votarPortal(${v.id})`)}</div>`).join('')
            : '<p class="text-secondary mb-0">Sem votações abertas de momento.</p>';
        cont.innerHTML = `
            <div class="card mb-3"><div class="card-body">
                <h2 class="h5 mb-1">A minha área — ${ctx.condominoNome}</h2>
                <p class="text-secondary mb-0">${ctx.condominioNome} · Fração ${ctx.fracaoNumero}</p>
            </div></div>
            <div class="card mb-3"><div class="card-body"><h3 class="h6 mb-3">As minhas quotas</h3>${quotasHtml}</div></div>
            <div class="card"><div class="card-body"><h3 class="h6 mb-3">Votações abertas</h3>${votHtml}</div></div>`;
    } catch (e) {
        cont.innerHTML = `<div class="alert alert-warning">Não foi possível carregar a sua área: ${e.message}<br><span class="small">A conta tem de estar associada a um condómino.</span></div>`;
    }
}
function votarPortal(votacaoId) {
    openModal('Votar',
        selectField('resposta', 'Sentido do voto', [{ value: 'SIM', label: 'A favor (SIM)' }, { value: 'NAO', label: 'Contra (NÃO)' }, { value: 'ABSTENCAO', label: 'Abstenção' }]),
        async () => { const d = formData(); await apiPost(`/api/v1/votacoes/${votacaoId}/votar`, { resposta: d.resposta }); showAlert('Voto registado.', 'success'); loadPortal(); },
        'O seu voto conta pela permilagem da sua fração (Lei 8/2022). Cada condómino vota uma vez.');
}
async function apagarVotacao(id) { if (!confirm('Apagar esta votação (e os seus votos)?')) return; try { await apiDel('/api/v1/votacoes/' + id); showAlert('Votação apagada.', 'success'); recarregar(loadVotacoes); } catch (e) { showAlert(e.message, 'danger'); } }
async function apagarOcorrencia(id) { if (!confirm('Apagar esta ocorrência?')) return; try { await apiDel('/api/v1/ocorrencias/' + id); showAlert('Ocorrência apagada.', 'success'); cache.ocor = (cache.ocor || []).filter(o => o.id !== id); renderOcorrencias(); } catch (e) { showAlert(e.message, 'danger'); } }
async function apagarMensagem(id) { if (!confirm('Apagar esta mensagem enviada?')) return; try { await apiDel('/api/v1/mensagens/' + id); showAlert('Mensagem apagada.', 'success'); loadMensagens(msgPage); } catch (e) { showAlert(e.message, 'danger'); } }
function openAlterarPassword() {
    openModal('A minha conta',
        field('novaPassword', 'Nova password (6–72 caracteres)', 'password', 'minlength="6" required')
        + '<hr><p class="small mb-2"><strong>RGPD</strong> — os seus dados pessoais:</p>'
        + '<div class="d-flex gap-2 flex-wrap"><button type="button" class="btn btn-sm btn-outline-secondary" onclick="exportarMeusDados()">⬇ Exportar os meus dados</button>'
        + '<button type="button" class="btn btn-sm btn-outline-danger" onclick="apagarMinhaConta()">Apagar a minha conta</button></div>',
        async () => { const d = formData(); await apiPut(`/api/v1/utilizadores/${user().id}/password`, { novaPassword: d.novaPassword }); showAlert('Password alterada.', 'success'); });
}
async function exportarMeusDados() {
    try {
        const d = await apiGet('/api/v1/rgpd/meus-dados');
        anchorDownload(new Blob([JSON.stringify(d, null, 2)], { type: 'application/json' }), 'os-meus-dados.json');
        showAlert('Dados pessoais exportados.', 'success');
    } catch (e) { showAlert(e.message, 'danger'); }
}
async function apagarMinhaConta() {
    if (!confirm('Apagar/anonimizar a sua conta? Os seus dados pessoais serão removidos e a sessão terminada. Ação irreversível.')) return;
    try { await apiDel('/api/v1/rgpd/minha-conta'); try { modal().hide(); } catch (e) {} showAlert('Conta apagada. Sessão terminada.', 'success'); logout(); }
    catch (e) { showAlert(e.message, 'danger'); }
}
function openRecuperar() {
    openModal('Recuperar password', field('email','Email da conta','email','required') + '<hr><p class="small text-secondary mb-2">Já tens um token? Redefine aqui:</p>' + field('token','Token de recuperação') + field('novaPassword','Nova password','password','minlength="6"'),
        async () => { const d = formData();
            if (d.token && d.novaPassword) { const res = await fetch('/api/v1/auth/redefinir-password', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ token: d.token, novaPassword: d.novaPassword }) }); if (!res.ok) throw new Error('Token inválido ou expirado.'); showAlert('Password redefinida. Já podes entrar.', 'success'); }
            else if (d.email) { await fetch('/api/v1/auth/recuperar-password', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email: d.email }) }); showAlert('Se a conta existir, foi enviado um email com o token (ou registado no log, se o SMTP estiver desligado).', 'info'); }
            else throw new Error('Indica o email (para pedir) ou token + nova password (para redefinir).'); },
        'O envio do token por <strong>email</strong> exige SMTP ativo (<code>MAIL_ENABLED=true</code>).');
}
function openDocumento() {
    openModal('Carregar documento — ' + currentCond.nome,
        field('nome','Nome','text','required') + field('tipo','Tipo (ex.: PDF, Regulamento)') + field('ficheiro','Ficheiro','file','required'),
        async () => {
            const f = document.getElementById('modalForm');
            if (!f.ficheiro.files.length) throw new Error('Escolha um ficheiro.');
            const fd = new FormData();
            fd.append('condominioId', currentCond.id); fd.append('nome', f.nome.value); fd.append('tipo', f.tipo.value || ''); fd.append('ficheiro', f.ficheiro.files[0]);
            await apiUpload('/api/v1/documentos', fd); showAlert('Documento carregado.', 'success'); recarregar(loadDocumentos);
        });
}
async function apagarDocumento(id) { if (!confirm('Apagar este documento?')) return; try { await apiDel('/api/v1/documentos/' + id); showAlert('Documento apagado.', 'success'); recarregar(loadDocumentos); } catch (e) { showAlert(e.message, 'danger'); } }

// ---------- Ocorrências (sub-vista do condomínio) ----------
const PRIO_BADGE = { BAIXA: 'secondary', MEDIA: 'info', ALTA: 'warning', URGENTE: 'danger' };
const OCOR_BADGE = { ABERTA: 'primary', EM_ANALISE: 'info', EM_EXECUCAO: 'warning', CONCLUIDA: 'success', CANCELADA: 'secondary' };
const podeGerirOcorrencias = () => (user().perfis || []).some(p => ['ROLE_GESTOR_EMPRESA', 'ROLE_FUNCIONARIO', 'ROLE_ADMIN_CONDOMINIO'].includes(p));
function renderOcorrencias() {
    const linhas = cache.ocor.map(o => ({ id: o.id, 'Título': o.titulo,
        Prioridade: `<span class="badge text-bg-${PRIO_BADGE[o.prioridade] || 'secondary'}">${o.prioridade}</span>`,
        Estado: `<span class="badge text-bg-${OCOR_BADGE[o.estado] || 'secondary'}">${String(o.estado).replace('_', ' ')}</span>` }));
    subView('Ocorrências', btn('btn-success', '➕ Nova ocorrência', 'openOcorrencia()') + btn('btn-outline-success','PDF',"baixarRelatorio('ocorrencias','pdf')") + btn('btn-outline-success','Excel',"baixarRelatorio('ocorrencias','excel')"),
        table(linhas, o => podeGerirOcorrencias()
            ? btn('btn-outline-secondary', 'Estado', `estadoOcorrencia(${o.id})`) + btn('btn-outline-info', 'Atribuir', `atribuirOcorrencia(${o.id})`) + btn('btn-outline-secondary', 'Editar', `editOcorrencia(${o.id})`) + btn('btn-outline-danger', 'Apagar', `apagarOcorrencia(${o.id})`)
            : '<span class="small text-secondary">—</span>'));
}
async function loadOcorrencias() {
    cache.ocor = rows(await apiGet(`/api/v1/ocorrencias?condominioId=${currentCond.id}&size=50`));
    renderOcorrencias();
}
function openOcorrencia() {
    openModal('Nova ocorrência', field('titulo', 'Título', 'text', 'required') + field('descricao', 'Descrição') + selectField('prioridade', 'Prioridade', opts(['BAIXA', 'MEDIA', 'ALTA', 'URGENTE']), 'MEDIA'),
        async () => { const d = formData(); const nova = await apiPost('/api/v1/ocorrencias', { condominioId: currentCond.id, titulo: d.titulo, descricao: d.descricao, prioridade: d.prioridade }); showAlert('Ocorrência registada.', 'success'); cache.ocor = [nova, ...(cache.ocor || [])]; renderOcorrencias(); });
}
function editOcorrencia(id) {
    const o = cache.ocor.find(x => x.id === id);
    openModal('Editar ocorrência', field('titulo', 'Título', 'text', 'required', esc(o.titulo)) + field('descricao', 'Descrição', 'text', '', esc(o.descricao || '')) + selectField('prioridade', 'Prioridade', opts(['BAIXA', 'MEDIA', 'ALTA', 'URGENTE']), o.prioridade),
        async () => { const d = formData(); const upd = await apiPut('/api/v1/ocorrencias/' + id, { titulo: d.titulo, descricao: d.descricao, prioridade: d.prioridade }); showAlert('Ocorrência atualizada.', 'success'); cache.ocor = cache.ocor.map(x => x.id === id ? upd : x); renderOcorrencias(); });
}
function estadoOcorrencia(id) {
    const o = cache.ocor.find(x => x.id === id);
    openModal('Alterar estado', selectField('estado', 'Estado', opts(['ABERTA', 'EM_ANALISE', 'EM_EXECUCAO', 'CONCLUIDA', 'CANCELADA']), o.estado),
        async () => { const d = formData(); const upd = await apiPut('/api/v1/ocorrencias/' + id + '/estado', { estado: d.estado }); showAlert('Estado atualizado.', 'success'); cache.ocor = cache.ocor.map(x => x.id === id ? upd : x); renderOcorrencias(); });
}
async function atribuirOcorrencia(id) {
    let us = []; try { us = rows(await apiGet('/api/v1/utilizadores?size=100')); } catch (e) {}
    const options = us.map(u => ({ value: u.id, label: `${u.nome} (${u.email})` }));
    openModal('Atribuir ocorrência', selectField('utilizadorId', 'Responsável', options.length ? options : [{ value: '', label: '(sem utilizadores)' }]),
        async () => { const d = formData(); if (!d.utilizadorId) throw new Error('Escolha um responsável.'); const upd = await apiPost('/api/v1/ocorrencias/' + id + '/atribuir', { utilizadorId: Number(d.utilizadorId) }); showAlert('Ocorrência atribuída.', 'success'); cache.ocor = cache.ocor.map(x => x.id === id ? upd : x); renderOcorrencias(); });
}

// ---------- Mensagens (tab de topo) ----------
const TIPO_MSG_BADGE = { INDIVIDUAL: 'primary', GRUPO: 'info', BROADCAST: 'warning' };
function setMsgBox(box) {
    msgBox = box;
    document.getElementById('msgboxRecebidas').classList.toggle('active', box === 'recebidas');
    document.getElementById('msgboxEnviadas').classList.toggle('active', box === 'enviadas');
    loadMensagens(0);
}
function renderMensagens() {
    const recebidas = msgBox !== 'enviadas';
    const linhas = cache.msgs.map(m => ({ id: m.id,
        Assunto: (recebidas && !m.lida ? '🔵 ' : '') + (m.assunto || ''),
        Tipo: `<span class="badge text-bg-${TIPO_MSG_BADGE[m.tipo] || 'secondary'}">${m.tipo}</span>`,
        De: m.origemNome || '—',
        Data: m.dataEnvio ? String(m.dataEnvio).replace('T', ' ').slice(0, 16) : '—' }));
    document.getElementById('mensagensTable').innerHTML =
        table(linhas, l => btn('btn-outline-primary', 'Abrir', `abrirMensagem(${l.id})`) + (msgBox === 'enviadas' ? btn('btn-outline-danger', 'Apagar', `apagarMensagem(${l.id})`) : ''))
        + paginadorHtml(msgPage, msgTotalPaginas, msgTotal, 'loadMensagens');
}
async function loadMensagens(page = 0) {
    const recebidas = msgBox !== 'enviadas';
    const p = await apiGet((recebidas ? '/api/v1/mensagens/recebidas' : '/api/v1/mensagens/enviadas') + `?page=${page}&size=15`);
    cache.msgs = p.conteudo || p.content || [];
    msgPage = p.pagina ?? page; msgTotalPaginas = p.totalPaginas ?? 1; msgTotal = p.totalElementos ?? cache.msgs.length;
    renderMensagens();
}
async function abrirMensagem(id) {
    const m = cache.msgs.find(x => x.id === id);
    const corpo = `<div class="small text-secondary mb-2">De: ${m.origemNome || '—'} · ${m.dataEnvio ? String(m.dataEnvio).replace('T', ' ').slice(0, 16) : ''}</div><div style="white-space:pre-wrap">${m.conteudo || ''}</div>`;
    openModal(m.assunto || 'Mensagem', corpo, async () => {}, '', true);
    if (msgBox !== 'enviadas' && !m.lida) {
        try { await apiPut('/api/v1/mensagens/' + id + '/lida'); m.lida = true; atualizarBadgeMensagens(); loadMensagens(msgPage); } catch (e) {}
    }
}
async function openMensagem() {
    let us = []; try { us = rows(await apiGet('/api/v1/utilizadores?size=100')); } catch (e) {}
    const destOpts = us.map(u => ({ value: u.id, label: `${u.nome} (${u.email})` }));
    openModal('Nova mensagem',
        selectField('tipo', 'Tipo', [{ value: 'INDIVIDUAL', label: 'Individual' }, { value: 'BROADCAST', label: 'Difusão (todos)' }], 'INDIVIDUAL')
        + '<div id="destWrap">' + selectField('destinoId', 'Destinatário', destOpts.length ? destOpts : [{ value: '', label: '(sem utilizadores)' }]) + '</div>'
        + field('assunto', 'Assunto', 'text', 'required')
        + '<div class="mb-2"><label class="form-label small">Conteúdo</label><textarea name="conteudo" class="form-control form-control-sm" rows="4" required></textarea></div>',
        async () => { const d = formData(); const body = { tipo: d.tipo, assunto: d.assunto, conteudo: d.conteudo };
            if (d.tipo === 'INDIVIDUAL') { if (!d.destinoId) throw new Error('Escolha um destinatário.'); body.destinoId = Number(d.destinoId); }
            const nova = await apiPost('/api/v1/mensagens', body); showAlert('Mensagem enviada.', 'success');
            // Mostrar a 1.ª página de "Enviadas" garantindo que a nova mensagem aparece (imune a read-after-write).
            msgBox = 'enviadas';
            document.getElementById('msgboxRecebidas').classList.remove('active');
            document.getElementById('msgboxEnviadas').classList.add('active');
            await loadMensagens(0);
            if (!cache.msgs.some(x => x.id === nova.id)) { cache.msgs = [nova, ...cache.msgs]; renderMensagens(); } });
    const sel = document.querySelector('#modalForm [name=tipo]');
    if (sel) sel.addEventListener('change', e => { document.getElementById('destWrap').style.display = e.target.value === 'INDIVIDUAL' ? '' : 'none'; });
}
async function atualizarBadgeMensagens() {
    try { const r = await apiGet('/api/v1/mensagens/nao-lidas/count'); const n = r.naoLidas || 0;
        const b = document.getElementById('msgBadge'); b.textContent = n; b.classList.toggle('d-none', n === 0);
    } catch (e) {}
}

function openAta() {
    const r = (cache.reun || []).find(x => x.id === currentReuniaoId);
    const dataDefault = r && r.data ? r.data : '';
    openModal('Nova ata', field('titulo','Título','text','required') + field('descricao','Descrição') + field('dataReuniao','Data da reunião','date','required',dataDefault),
        async () => { const d = formData(); if (!d.dataReuniao) throw new Error('A data da reunião é obrigatória.'); const nova = await apiPost('/api/v1/atas', { reuniaoId: currentReuniaoId, titulo: d.titulo, descricao: d.descricao, dataReuniao: d.dataReuniao }); showAlert('Ata criada. Pode anexar o ficheiro.', 'success'); cache.atas = [nova, ...(cache.atas || [])]; renderAtas(); });
}
function editAta(id) {
    const a = cache.atas.find(x => x.id === id);
    openModal('Editar ata', field('titulo','Título','text','required',esc(a.titulo || '')) + field('descricao','Descrição','text','',esc(a.descricao || '')) + field('dataReuniao','Data da reunião','date','',a.dataReuniao || ''),
        async () => { const d = formData(); const upd = await apiPut('/api/v1/atas/' + id, { titulo: d.titulo, descricao: d.descricao, dataReuniao: d.dataReuniao || null }); showAlert('Ata atualizada.', 'success'); cache.atas = (cache.atas || []).map(a => a.id === id ? upd : a); renderAtas(); });
}
function anexarAta(id) {
    openModal('Anexar ficheiro à ata', field('ficheiro','Ficheiro (PDF, etc.)','file','required'),
        async () => { const f = document.getElementById('modalForm'); if (!f.ficheiro.files.length) throw new Error('Escolha um ficheiro.'); const fd = new FormData(); fd.append('ficheiro', f.ficheiro.files[0]); const upd = await apiUpload('/api/v1/atas/' + id + '/ficheiro', fd); showAlert('Ficheiro anexado.', 'success'); cache.atas = (cache.atas || []).map(a => a.id === id ? upd : a); renderAtas(); });
}
async function apagarAta(id) { if (!confirm('Apagar esta ata?')) return; try { await apiDel('/api/v1/atas/' + id); showAlert('Ata apagada.', 'success'); cache.atas = (cache.atas || []).filter(a => a.id !== id); renderAtas(); } catch (e) { showAlert(e.message, 'danger'); } }

document.getElementById('modalForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const err = document.getElementById('modalError'); err.classList.add('d-none');
    const sb = document.querySelector('#modalForm button[type=submit]'); const txt = sb ? sb.textContent : '';
    if (sb) { sb.disabled = true; sb.textContent = 'A processar…'; }
    try { await onSubmitModal(); modal().hide(); }
    catch (ex) { err.textContent = ex.message; err.classList.remove('d-none'); }
    finally { if (sb) { sb.disabled = false; sb.textContent = txt; } }
});

async function loadHealth() { const b = document.getElementById('healthBadge'); try { const d = await (await fetch('/api/v1/health')).json(); b.className = 'badge bg-success'; b.textContent = 'serviço: ' + (d.status || 'UP'); } catch { b.className = 'badge bg-danger'; b.textContent = 'serviço: indisponível'; } }

document.getElementById('loginForm').addEventListener('submit', async (e) => { e.preventDefault(); const err = document.getElementById('loginError'); err.classList.add('d-none'); const btn = document.getElementById('loginBtn'); btn.disabled = true; btn.textContent = 'A entrar…'; try { await doLogin(document.getElementById('email').value, document.getElementById('password').value); } catch (ex) { err.textContent = ex.message; err.classList.remove('d-none'); } finally { btn.disabled = false; btn.textContent = 'Entrar'; } });
document.getElementById('logoutBtn').addEventListener('click', logout);
document.querySelectorAll('[data-tab]').forEach(b => b.addEventListener('click', () => { const tab = b.dataset.tab; activateTab(tab); if (tab === 'utilizadores') { loadUtilizadores(); return; } if (tab === 'permissoes') { loadPermPerfis(); return; } if (tab === 'auditoria') { loadAuditoria(); return; } if (tab === 'empresas') { loadEmpresas(); return; } if (tab === 'portal') { loadPortal(); return; } if (isAdmin()) { showAlert('Conta de admin: gere Utilizadores, Permissões, Auditoria e Empresas.', 'info'); return; } if (tab === 'dashboard') loadDashboard(); if (tab === 'condominios') loadCondominios(); if (tab === 'votacoes') loadVotacoes(); if (tab === 'mensagens') loadMensagens(); }));
const themeSaved = localStorage.getItem('theme'); if (themeSaved) document.documentElement.dataset.bsTheme = themeSaved;
document.getElementById('themeToggle').addEventListener('click', () => {
    const h = document.documentElement; h.dataset.bsTheme = h.dataset.bsTheme === 'light' ? 'dark' : 'light'; localStorage.setItem('theme', h.dataset.bsTheme);
    // Re-renderizar gráficos da vista ativa para as cores acompanharem o tema.
    const active = document.querySelector('.nav-link.active')?.dataset.tab;
    if (active === 'dashboard') loadDashboard();
    if (currentCond && document.getElementById('chartPermilagem')) loadFracoes();
});

function changeLanguage(lang) {
    const btn = document.getElementById('langBtn');
    if (btn) {
        btn.innerHTML = `🌐 ${lang}`;
    }
    ['PT', 'EN', 'FR'].forEach(l => {
        const item = document.getElementById(`lang${l}`);
        if (item) {
            item.classList.toggle('active', l === lang);
        }
    });

    const underConst = document.getElementById('underConstructionView');
    const appView = document.getElementById('appView');
    const loginView = document.getElementById('loginView');

    if (lang === 'EN' || lang === 'FR') {
        if (appView) appView.classList.add('d-none');
        if (loginView) loginView.classList.add('d-none');
        if (underConst) underConst.classList.remove('d-none');
    } else {
        if (underConst) underConst.classList.add('d-none');
        if (token()) {
            if (appView) appView.classList.remove('d-none');
        } else {
            if (loginView) loginView.classList.remove('d-none');
        }
    }
}
window.changeLanguage = changeLanguage;

document.body.dataset.bg = 'login';
loadHealth();
if (token()) showApp();
