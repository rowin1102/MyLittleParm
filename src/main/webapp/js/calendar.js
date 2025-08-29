document.addEventListener('DOMContentLoaded', () => {
  var Calendar = window.tui.Calendar;

  const COLOR_MAP = {
    pink:   '#ffd1e8',
    blue:   '#cfe9ff',
    green:  '#d8f5d0',
    yellow: '#fff6bf'
  };

  const cal = new Calendar('#calendar', {
    defaultView: 'month',
    useFormPopup: false,
    useDetailPopup: false,
    isReadOnly: false,
    month: { showEventTime: false },
    calendars: [{ id: 'plant', name: 'Plant', backgroundColor: '#e9f2ff' }],
  });

  // ───────────────── 공통 유틸 ─────────────────
  function toLocalYMD(date) {
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }
  function addDaysLocal(ymd, days) {
    const [y, m, d] = ymd.split('-').map(Number);
    const dt = new Date(y, m - 1, d);
    dt.setDate(dt.getDate() + days);
    return dt;
  }
  function parseYmd(s) {
    const [y, m, d] = s.split('-').map(Number);
    return new Date(y, m - 1, d);
  }
  function addDaysYmd(ymd, days) {
    const dt = parseYmd(ymd);
    dt.setDate(dt.getDate() + days);
    const y = dt.getFullYear();
    const m = String(dt.getMonth() + 1).padStart(2, '0');
    const d = String(dt.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  }
  function getMonthRange() {
    const d = cal.getDate();
    const y = d.getFullYear(), m = d.getMonth();
    const first = new Date(y, m, 1);
    const nextFirst = new Date(y, m + 1, 1); // exclusive
    return { start: toLocalYMD(first), end: toLocalYMD(nextFirst) };
  }
  function positionModalUnderCell(modal, nativeEvent) {
    let left = 100, top = 100;
    if (nativeEvent && nativeEvent.target) {
      const cell = nativeEvent.target.closest('.toastui-calendar-daygrid-cell, .toastui-calendar-weekday-grid-cell');
      if (cell) {
        const rect = cell.getBoundingClientRect();
        left = rect.left + window.scrollX;
        top = rect.bottom + window.scrollY + 8;
      }
    }
    modal.style.left = left + 'px';
    modal.style.top = top + 'px';
  }
  function getCsrf() {
    const token = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    return token && header ? { header, token } : null;
  }

  // ───── 모달/백드롭 공통 제어 ─────
  let backdrop = document.getElementById('modalBackdrop');
  if (!backdrop) {
    backdrop = document.createElement('div');
    backdrop.id = 'modalBackdrop';
    backdrop.style.cssText = [
      'display:none','position:fixed','inset:0',
      'background:rgba(0,0,0,0.15)','z-index:900'
    ].join(';');
    document.body.appendChild(backdrop);
  }
  const modalSearch = document.getElementById('plantSearchModal');
  const modalDetail = document.getElementById('detailModal');
  const modalView   = document.getElementById('eventDetailModal');
  const MODALS = [modalSearch, modalDetail, modalView];

  MODALS.forEach(m => { if (m) { m.style.zIndex = '950'; } });

  function isOpen(el){ return el && el.style.display !== 'none'; }
  function anyOpen(){ return MODALS.some(isOpen); }
  function showModal(el){
    if (!el) return;
    el.style.display = 'block';
    backdrop.style.display = 'block';
  }
  function hideModal(el){
    if (!el) return;
    el.style.display = 'none';
    if (!anyOpen()) backdrop.style.display = 'none';
  }
  function closeAllModals(){ MODALS.forEach(hideModal); }

  backdrop.addEventListener('click', closeAllModals);
  document.addEventListener('mousedown', (e) => {
    if (!anyOpen()) return;
    for (const m of MODALS) {
      if (isOpen(m) && m.contains(e.target)) return;
    }
    closeAllModals();
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && anyOpen()) closeAllModals();
  });

  // ───── 제목을 첫 주만 보이게 보정 ─────
  function keepOnlyFirstRowTitles() {
    const titles = Array.from(document.querySelectorAll('#calendar .toastui-calendar-event-title'));
    if (!titles.length) return;

    const grouped = new Map(); // text -> [{el, top}]
    for (const el of titles) {
      const text = (el.textContent || '').trim();
      if (!text) continue;
      const top = el.getBoundingClientRect().top;
      const arr = grouped.get(text) || [];
      arr.push({ el, top });
      grouped.set(text, arr);
    }

    grouped.forEach(arr => {
      arr.sort((a, b) => a.top - b.top); // 첫 주가 가장 위
      for (let i = 1; i < arr.length; i++) arr[i].el.textContent = '';
    });
  }

  // ───────────── 상단 툴바/월 표시/이동 ─────────────
  function renderCurrentMonth() {
    const d = cal.getDate();
    const ym = `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}`;
    document.getElementById('currentMonth').textContent = ym;
  }
  document.getElementById('btnToday').onclick = () => { cal.today(); renderCurrentMonth(); loadEvents(); };
  document.getElementById('btnPrev').onclick  = () => { cal.prev();  renderCurrentMonth(); loadEvents(); };
  document.getElementById('btnNext').onclick  = () => { cal.next();  renderCurrentMonth(); loadEvents(); };
  renderCurrentMonth();

  // ───────────── 일정 드래그 이동(시작일 수정) ─────────────
  cal.on('beforeUpdateSchedule', async ({ schedule, changes }) => {
    if (!changes.start) return;

    if (typeof schedule.id === 'string' && /^\d{13}$/.test(schedule.id)) {
      loadEvents();
      alert('방금 만든 일정입니다. 잠시 후 다시 이동하세요.');
      return;
    }

    cal.updateSchedule(schedule.id, schedule.calendarId, changes);

    const newStart = changes.start instanceof Date
      ? toLocalYMD(changes.start)
      : (typeof changes.start === 'string' ? changes.start : toLocalYMD(new Date(changes.start)));

    const csrf = getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf) headers[csrf.header] = csrf.token;

    try {
      const res = await fetch(`${window.APP_CTX || ''}/calendar/updateDate.do`, {
        method: 'POST',
        headers,
        credentials: 'include',
        body: JSON.stringify({ id: schedule.id, start_date: newStart })
      });
      const data = await res.json();
      if (!res.ok || !data.ok) throw new Error('DB update failed');
      setTimeout(keepOnlyFirstRowTitles, 0);
    } catch (e) {
      alert('일정 날짜 업데이트 중 오류가 발생했습니다.');
      console.error(e);
      loadEvents();
    }
  });

  // ───────────── 서버에서 이벤트 로드 ─────────────
  async function loadEvents() {
    const { start, end } = getMonthRange();
    const url = `${window.APP_CTX||''}/calendar/list.do?start=${start}&end=${end}`;
    try {
      const res = await fetch(url, { headers: { 'Accept': 'application/json' }, credentials: 'include' });
      if (!res.ok) { cal.clear(); return; }
      const list = await res.json();

      cal.clear();
      const events = (Array.isArray(list) ? list : []).map(e => {
        const hex = COLOR_MAP[e.color] || '#eaeaea';
        const startYmd = e.start;
        return {
          id: String(e.id),
          calendarId: 'plant',
          title: `${e.title} (${startYmd})`,
          start: e.start,
          end:   e.end, // END_EXCL
          isAllday: true,
          category: 'allday',
          backgroundColor: hex,
          borderColor: hex,
          raw: {
            dbTitle: e.title,
            memo: e.memo || '',
            start: e.start,
            plant: e.plant || '',
            endExcl: e.end
          }
        };
      });
      cal.createEvents(events);
      setTimeout(keepOnlyFirstRowTitles, 0);
    } catch (err) {
      console.error('loadEvents error', err);
    }
  }
  loadEvents();

  // ───────────── 날짜 선택 → 식물 검색 모달 ─────────────
  let lastNativeEvent = null;
  cal.on('selectDateTime', ({ start, nativeEvent }) => {
    lastNativeEvent = nativeEvent;
    const iso = toLocalYMD(start);
    showSearchModal(iso, nativeEvent);
    cal.clearGridSelections();
  });

  function showSearchModal(date, nativeEvent) {
    const modal = modalSearch;
    document.getElementById('selectedDateText').textContent = '선택한 날짜: ' + date;
    resetSearch();
    positionModalUnderCell(modal, nativeEvent);
    modal.dataset.date = date;
    showModal(modal);
    document.getElementById('plantKeyword').focus();
  }
  function closeModal() {
    hideModal(modalSearch);
    resetSearch();
  }
  window.closeModal = closeModal;

  function resetSearch() {
    const kw = document.getElementById('plantKeyword');
    const sr = document.getElementById('searchResult');
    if (kw) kw.value = '';
    if (sr) sr.innerHTML = '';
  }

  async function searchPlants() {
    const keywordEl = document.getElementById('plantKeyword');
    const container = document.getElementById('searchResult');
    const keyword = (keywordEl?.value || '').trim();

    container.innerHTML = '';
    if (!keyword) { container.innerHTML = '<div class="empty">검색어를 입력하세요.</div>'; return; }

    const url = `${window.APP_CTX || ''}/search.do?keyword=${encodeURIComponent(keyword)}`;
    try {
      const res = await fetch(url, { method: 'GET', headers: { 'Accept': 'application/json' }, credentials: 'include' });
      if (!res.ok) throw new Error('검색 실패');

      const ct = (res.headers.get('content-type') || '').toLowerCase();
      if (!ct.includes('application/json')) throw new Error('JSON 아님');

      const list = await res.json();
      if (!Array.isArray(list) || list.length === 0) {
        container.innerHTML = '<div class="empty">검색한 결과가 없습니다.</div>';
        return;
      }

      list.forEach(p => {
        if (!p) return;
        const n = p.name ?? '';
        const g = Number(p.max_grow_days ?? 0);
        if (!n) return;

        const div = document.createElement('div');
        div.className = 'result-item';
        div.textContent = g > 0 ? `${n} (${g}일)` : `${n}`;
        div.onclick = () => selectPlant({ name: n, growthPeriod: g });
        container.appendChild(div);
      });
    } catch (e) {
      container.innerHTML = '<div class="empty">오류가 발생했습니다.</div>';
      console.error(e);
    }
  }
  document.getElementById('plantSearchForm')?.addEventListener('submit', (e) => {
    e.preventDefault();
    searchPlants();
  });

  // ───────────── 검색 결과 선택 → 등록 모달 ─────────────
  function selectPlant(plant) {
    const startYmd = modalSearch.dataset.date;
    closeModal();

    const dm = modalDetail;
    document.getElementById('dDate').value   = startYmd;
    document.getElementById('dPlant').value  = plant.name;
    document.getElementById('dGrow').value   = plant.growthPeriod || 0;

    document.getElementById('dTitle').value  = '';
    document.getElementById('dDateText').textContent = startYmd;
    document.getElementById('dMemo').value   = '';
    document.getElementById('dColor').value  = 'pink';

    positionModalUnderCell(dm, lastNativeEvent);
    showModal(dm);
  }
  window.closeDetail = function(){ hideModal(modalDetail); };

  // ───────────── 저장(신규 등록) ─────────────
  document.getElementById('detailForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const start    = document.getElementById('dDate').value;
    const plant    = document.getElementById('dPlant').value;
    const grow     = Number(document.getElementById('dGrow').value || 0);
    const titleEl  = document.getElementById('dTitle');
    const memoEl   = document.getElementById('dMemo');
    const titleRaw = (titleEl.value || '').trim();
    const memo     = (memoEl.value || '').trim();
    const colorKey = document.getElementById('dColor').value;

    // ▶ 제목/메모 검증 + 포커스
    if (!titleRaw) { alert('제목을 입력해 주세요.'); titleEl.focus(); return; }
    if (!memo)     { alert('메모를 입력해 주세요.');  memoEl.focus();  return; }

    const csrf = getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf) headers[csrf.header] = csrf.token;

    try {
      const res = await fetch(`${window.APP_CTX || ''}/calendar/save.do`, {
        method: 'POST',
        headers,
        credentials: 'include',
        body: JSON.stringify({
          plants_name: plant,
          start_date : start,
          title: titleRaw,
          memo,
          color: colorKey
        })
      });
      const data = await res.json().catch(() => ({ ok: false }));
      if (!res.ok || !data.ok) throw new Error('저장 실패');

      const days = grow > 0 ? grow + 1 : 1; // END_EXCL = start + days
      const endYmdExclusive = toLocalYMD(addDaysLocal(start, days));
      const hex = COLOR_MAP[colorKey] || '#eaeaea';

      const tempId = String(Date.now());
      cal.createEvents([{
        id: tempId,
        calendarId: 'plant',
        title: `${titleRaw} (${start})`,
        start: start,
        end: endYmdExclusive,
        isAllday: true,
        category: 'allday',
        backgroundColor: hex,
        borderColor: hex,
        raw: { dbTitle: titleRaw, memo, start, plant, endExcl: endYmdExclusive }
      }]);

      if (data.id != null) {
        cal.updateEvent(tempId, 'plant', { id: String(data.id) });
      }

      setTimeout(keepOnlyFirstRowTitles, 0);
      hideModal(modalDetail);
    } catch (err) {
      alert('저장 중 오류가 발생했습니다.');
      console.error(err);
    }
  });

  // ───────────── 상세 모달(보기/수정/삭제) ─────────────
  let currentEvent = null;

  function setViewMode(readonly) {
    const titleEl = document.getElementById('vTitle');
    const memoEl  = document.getElementById('vMemo');
    if (titleEl) titleEl.readOnly = readonly;
    if (memoEl)  memoEl.readOnly  = readonly;

    document.getElementById('btnEdit').style.display   = readonly ? ''  : 'none';
    document.getElementById('btnDelete').style.display = readonly ? ''  : 'none';
    document.getElementById('btnSave').style.display   = readonly ? 'none' : '';
    document.getElementById('btnCancel').style.display = readonly ? 'none' : '';
  }

  cal.on('clickEvent', ({ event, nativeEvent }) => {
    const modal = modalView;

    const plant   = event?.raw?.plant ?? '';
    const memo    = event?.raw?.memo ?? '';
    const title   = event?.raw?.dbTitle ?? '';
    const endExcl = (typeof event?.raw?.endExcl === 'string')
      ? event.raw.endExcl
      : toLocalYMD(new Date(event.end));
    const harvest = addDaysYmd(endExcl, 1);

    document.getElementById('vId').value = event?.id || '';
    const titleEl = document.getElementById('vTitle');
    if (titleEl) titleEl.value = title;

    const plantEl = document.getElementById('vPlant');
    if (plantEl) plantEl.value = plant;

    const hvEl = document.getElementById('vHarvest');
    if (hvEl) hvEl.value = harvest;

    const memoEl = document.getElementById('vMemo');
    if (memoEl) memoEl.value = memo;

    currentEvent = event;
    setViewMode(true);
    positionModalUnderCell(modal, nativeEvent);
    showModal(modal);
  });

  document.getElementById('btnEdit').addEventListener('click', () => {
    setViewMode(false);
    const titleEl = document.getElementById('vTitle');
    if (titleEl) titleEl.focus();
  });

  document.getElementById('btnCancel').addEventListener('click', () => {
    if (!currentEvent) return;
    const titleEl = document.getElementById('vTitle');
    const memoEl  = document.getElementById('vMemo');
    if (titleEl) titleEl.value = currentEvent?.raw?.dbTitle ?? '';
    if (memoEl)  memoEl.value  = currentEvent?.raw?.memo ?? '';
    setViewMode(true);
  });

  document.getElementById('btnSave').addEventListener('click', async () => {
    if (!currentEvent) return;

    const id      = document.getElementById('vId').value;
    const titleEl = document.getElementById('vTitle');
    const memoEl  = document.getElementById('vMemo');
    const title   = (titleEl?.value || '').trim();
    const memo    = (memoEl?.value  || '').trim();

    // ▶ 제목/메모 검증 + 포커스
    if (!title) { alert('제목을 입력해 주세요.'); titleEl.focus(); return; }
    if (!memo)  { alert('메모를 입력해 주세요.');  memoEl.focus();  return; }

    const csrf = getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf) headers[csrf.header] = csrf.token;

    try {
      const res = await fetch(`${window.APP_CTX||''}/calendar/update.do`, {
        method: 'POST', headers, credentials:'include',
        body: JSON.stringify({ id, title, memo })
      });
      const data = await res.json().catch(()=>({ok:false}));
      if (!res.ok || !data.ok) throw new Error('update failed');

      cal.updateEvent(currentEvent.id, currentEvent.calendarId, {
        title: (() => {
          const startYmd = typeof currentEvent.raw?.start === 'string'
            ? currentEvent.raw.start
            : toLocalYMD(currentEvent.start?.toDate ? currentEvent.start.toDate() : new Date(currentEvent.start));
          return `${title} (${startYmd})`;
        })(),
        raw: { ...(currentEvent.raw||{}), dbTitle: title, memo }
      });

      setTimeout(keepOnlyFirstRowTitles, 0);
      setViewMode(true);
    } catch (e) {
      alert('수정 중 오류가 발생했습니다.');
      console.error(e);
    }
  });

  document.getElementById('btnDelete').addEventListener('click', async () => {
    if (!currentEvent) return;
    if (!confirm('이 일정을 삭제할까요?')) return;

    const csrf = getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf) headers[csrf.header] = csrf.token;

    try {
      const res = await fetch(`${window.APP_CTX||''}/calendar/delete.do`, {
        method: 'POST', headers, credentials:'include',
        body: JSON.stringify({ id: currentEvent.id })
      });
      const data = await res.json().catch(()=>({ok:false}));
      if (!res.ok || !data.ok) throw new Error('delete failed');

      cal.deleteEvent(currentEvent.id, currentEvent.calendarId);
      currentEvent = null;
      hideModal(modalView);

      setTimeout(keepOnlyFirstRowTitles, 0);
    } catch (e) {
      alert('삭제 중 오류가 발생했습니다.');
      console.error(e);
    }
  });

  window.closeEventDetail = function () { hideModal(modalView); };

  // ───────────── 렌더 감시: 새 노드 추가 시 보정 ─────────────
  const containerEl = document.querySelector('#calendar');
  const mo = new MutationObserver(() => {
    setTimeout(keepOnlyFirstRowTitles, 0);
  });
  mo.observe(containerEl, { childList: true, subtree: true });
});
