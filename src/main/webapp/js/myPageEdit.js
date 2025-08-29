// myPageEdit.js
(function() {
  const $  = (sel, p = document) => p.querySelector(sel);
  const $$ = (sel, p = document) => Array.from(p.querySelectorAll(sel));

  // ===== 공통 유틸 =====
  function buildHeaders(ct = 'application/x-www-form-urlencoded') {
    const h = { 'Content-Type': ct };
    const t = document.querySelector('meta[name="_csrf"]')?.content;
    const n = document.querySelector('meta[name="_csrf_header"]')?.content;
    if (t && n) h[n] = t;
    return h;
  }
  function getMeta(name, fallback = '') {
    return document.querySelector(`meta[name="${name}"]`)?.content || fallback;
  }
  function ready(fn) {
    (document.readyState === 'loading') ? document.addEventListener('DOMContentLoaded', fn) : fn();
  }

  // ===== 이메일 입력 접근자(단일/분리형 모두 지원) =====
  function emailLocalEl()  { return $('#emailInput')  || $('input[name="email"]'); }
  function emailDomainEl() { return $('#domainInput') || $('input[name="domain"]'); }
  function domainSelectEl(){ return $('#domainSelect')|| $('select[name="domain_select"]'); }

  // 이메일 full 조립 / 원본 조립
  function getFullEmail() {
    const local  = (emailLocalEl()?.value  || '').trim();
    const domain = (emailDomainEl()?.value || '').trim();
    if (!local) return '';
    return domain ? `${local}@${domain}` : local;
  }
  function getOriginalEmail() {
    const localOrig  = (emailLocalEl()?.dataset?.original  || '').trim();
    const domainOrig = (emailDomainEl()?.dataset?.original || '').trim();
    return domainOrig ? `${localOrig}@${domainOrig}` : localOrig;
  }

  // ===== 이메일 인증 상태/UI =====
  const emailState = { verified: false, lastSentEmail: '', pollTimer: null };

  function setEmailVerifyUI({ ok, msg, cls }) {
    const msgEl = $('#emailVerifyMsg');
    const flag  = $('#emailVerifiedFlag');
    const btn   = $('#btnSendEmailVerify');

    if (msgEl) {
      msgEl.textContent = msg || '';
      // 요구사항: 성공 외에는 빨간색
      msgEl.className = 'validation-message' + (cls ? ' ' + cls : (ok ? ' success' : ' error'));
    }
    if (flag) flag.value = ok ? 'Y' : 'N';
    emailState.verified = !!ok;
    if (btn) btn.disabled = !!ok; // 인증 완료 시 버튼 비활성화
  }

  function stopEmailPoll() {
    if (emailState.pollTimer) {
      clearInterval(emailState.pollTimer);
      emailState.pollTimer = null;
    }
  }
  async function checkEmailStatus(email) {
    try {
      const url = getMeta('email-verify-status-url', '/api/auth/email/status') + '?email=' + encodeURIComponent(email);
      const res = await fetch(url, { method: 'GET', credentials: 'same-origin' });
      if (!res.ok) return;
      const data = await res.json().catch(() => ({}));
      if (data.verified === true) {
        setEmailVerifyUI({ ok: true, msg: '이메일 인증이 완료되었습니다.', cls: 'success' });
        stopEmailPoll();
      }
    } catch (_) { /* 폴링 실패는 무시 */ }
  }
  function startEmailPoll(email) {
    stopEmailPoll();
    emailState.pollTimer = setInterval(() => checkEmailStatus(email), 5000);
    checkEmailStatus(email); // 즉시 1회
  }

  function resetEmailVerifyIfChanged() {
    const current  = getFullEmail();
    const original = getOriginalEmail();
    const btn = $('#btnSendEmailVerify');
    const flag= $('#emailVerifiedFlag');
    const msg = $('#emailVerifyMsg');

    stopEmailPoll();

    if (!current) {
      btn && (btn.disabled = true);
      msg && (msg.textContent = '');
      msg && (msg.className   = 'validation-message');
      flag && (flag.value     = 'N');
      emailState.verified = false;
      return;
    }
    if (current === original) {
      // 변경 안됨 → 인증 불필요
      btn && (btn.disabled = true);
      if (!emailState.verified) {
        msg && (msg.textContent = '');
        msg && (msg.className   = 'validation-message');
        flag && (flag.value     = 'N');
      } else {
        flag && (flag.value = 'Y');
      }
      return;
    }
    // 변경됨 → 인증 필요
    btn && (btn.disabled = false);
    msg && (msg.textContent = '');
    msg && (msg.className   = 'validation-message');
    flag && (flag.value     = 'N');
    emailState.verified = false;
  }

  // JSP onchange="handleDomainSelect(this.value)" 용
  window.handleDomainSelect = function(v) {
    const d = emailDomainEl();
    if (!d) return;
    if (v) { d.value = v; d.readOnly = true; }
    else   { d.readOnly = false; d.value = ''; }
    resetEmailVerifyIfChanged();
  };

  // ===== 닉네임: 중복확인 =====
  (function wireNickname() {
    const input = document.getElementById('nickname');
    const btn   = document.getElementById('btnCheckNickname');
    const msg   = document.getElementById('nicknameMessage');
    if (!input || !btn || !msg) return;

    const state = { lastChecked: '', available: false };

    function setMsg(text, cls) {
      msg.textContent = text || '';
      msg.className = 'validation-message' + (cls ? ' ' + cls : '');
    }

    function refreshBtn() {
      const v    = (input.value || '').trim();
      const orig = (input.dataset.original || '').trim();

      if (!v) {
        btn.disabled = true;
        btn.title = '닉네임을 입력해 주세요.';
        setMsg('', '');
        state.lastChecked = '';
        state.available   = false;
        return;
      }
      if (v === orig) {
        btn.disabled = true;
        btn.title = '현재 사용 중인 닉네임입니다.';
        setMsg('현재 사용 중인 닉네임입니다.', 'neutral');
        state.lastChecked = v;
        state.available   = true;  // 동일 닉네임은 저장 허용
        return;
      }
      btn.disabled = false;
      btn.title = '';
      if (state.lastChecked !== v) {
        state.available = false;
        setMsg('', '');
      }
    }

    input.addEventListener('input',  refreshBtn);
    input.addEventListener('change', refreshBtn);
    refreshBtn();

    btn.addEventListener('click', async () => {
      if (btn.disabled) return;

      const nickname = (input.value || '').trim();
      const url = getMeta('nickname-check-url', '/mypage/nickname/check');

      const oldText = btn.textContent;
      btn.textContent = '확인중…';
      btn.disabled    = true;
      setMsg('중복 확인중…', 'neutral');

      try {
        const res = await fetch(url, {
          method: 'POST',
          headers: buildHeaders('application/x-www-form-urlencoded'),
          credentials: 'same-origin',
          body: 'nickname=' + encodeURIComponent(nickname)
        });

        // JSON 파싱 시도(안전)
        let data;
        try { data = await res.clone().json(); }
        catch {
          const txt = await res.text();
          try { data = JSON.parse(txt); } catch { data = {}; }
        }

        if (data.same) {
          setMsg(data.message || '현재 사용 중인 닉네임입니다.', 'neutral');
          state.available = true;
        } else if (data.available) {
          setMsg(data.message || '사용 가능한 닉네임입니다.', 'success');
          state.available = true;
        } else {
          setMsg(data.message || '이미 사용 중인 닉네임입니다.', 'error');
          state.available = false;
        }
        state.lastChecked = nickname;
      } catch {
        setMsg('중복확인 중 오류가 발생했습니다.', 'error');
        state.available   = false;
        state.lastChecked = nickname;
      } finally {
        btn.textContent = oldText;
        refreshBtn();
      }
    });

    // 폼 제출에서 참조할 수 있게 노출
    window.__nickCheckState = state;
  })();

  // ===== 전송 전 공백 정리 =====
  window.sanitizeBeforeSubmit = function(form) {
    try {
      if (!form) return true;
      if (form.email)    form.email.value    = (form.email.value || '').trim();
      if (form.domain)   form.domain.value   = (form.domain.value || '').trim();
      if (form.phone)    form.phone.value    = (form.phone.value || '').trim();
      if (form.nickname) form.nickname.value = (form.nickname.value || '').trim();
    } catch (_) {}
    return true;
  };

  // ===== 폼 유효성(닉네임 중복확인 강제 포함) =====
  window.validateForm = function(form) {
    if (!form) return true;

    const emailLocal = form.email?.value?.trim()    ?? '';
    const domain     = form.domain?.value?.trim()   ?? '';
    const phone      = form.phone?.value?.trim()    ?? '';
    const nick       = form.nickname?.value?.trim() ?? '';
    const origNick   = form.nickname?.dataset?.original?.trim() ?? '';

    if (!emailLocal) { alert('이메일을 입력해 주세요.');          form.email?.focus();    return false; }
    if (!domain)     { alert('이메일 도메인을 입력해 주세요.');   form.domain?.focus();   return false; }
    if (!phone)      { alert('전화번호를 입력해 주세요.');        form.phone?.focus();    return false; }
    if (!nick)       { alert('닉네임을 입력해 주세요.');          form.nickname?.focus(); return false; }

    // 이메일 변경 시 인증 필수
    const fullEmail = getFullEmail();
    const origEmail = getOriginalEmail();
    if (fullEmail !== origEmail) {
      const verified = ($('#emailVerifiedFlag')?.value || 'N') === 'Y';
      if (!verified) {
        alert('이메일이 변경되었습니다. 이메일 인증을 완료해 주세요.');
        $('#btnSendEmailVerify')?.focus();
        return false;
      }
    }

    // 닉네임 중복확인 강제
    const ns = window.__nickCheckState || { available: false, lastChecked: '' };
    if (nick !== origNick) {
      if (!(ns.available && ns.lastChecked === nick)) {
        alert('닉네임 중복확인을 해주세요.');
        $('#btnCheckNickname')?.focus();
        return false;
      }
    }
    return true;
  };

  // ===== 초기화 =====
  ready(function() {
    // 원본 이메일 기준 세팅
    resetEmailVerifyIfChanged();

    // 입력 변화 감지(분리/단일 공통)
    document.addEventListener('input', (e) => {
      const nameOrId = e.target?.name || e.target?.id || '';
      if (['email', 'emailInput', 'domain', 'domainInput'].includes(nameOrId)) {
        resetEmailVerifyIfChanged();
      }
    });
    document.addEventListener('change', (e) => {
      const nameOrId = e.target?.name || e.target?.id || '';
      if (['domain', 'domainInput', 'domain_select', 'domainSelect'].includes(nameOrId)) {
        resetEmailVerifyIfChanged();
      }
    });

    // 도메인 셀렉터 초기 반영(있을 때만)
    const sel = domainSelectEl();
    if (sel && sel.value) { window.handleDomainSelect(sel.value); }

    // 이메일 인증 버튼
    const btn = $('#btnSendEmailVerify');
    const msg = $('#emailVerifyMsg');
    btn && btn.addEventListener('click', async () => {
      if (btn.disabled) return;
      const full = getFullEmail();
      if (!full || !full.includes('@')) {
        setEmailVerifyUI({ ok: false, msg: '올바른 이메일 형식이 아닙니다.', cls: 'error' });
        return;
      }

      // UX: 즉시 안내(빨간색) → 서버 요청은 비동기
      setEmailVerifyUI({ ok: false, msg: '인증 메일을 전송했습니다. 메일함을 확인해 주세요.', cls: 'error' });
      btn.disabled = true;

      try {
        await fetch(getMeta('email-verify-send-url', '/api/auth/email/send'), {
          method: 'POST',
          headers: buildHeaders(),
          credentials: 'same-origin',
          // 목적 접두사(purpose)는 컨트롤러 단에서 선택적으로 사용
          body: new URLSearchParams({ email: full, purpose: 'UE' }).toString()
        });
        emailState.lastSentEmail = full;
        startEmailPoll(full);
      } catch (e) {
        msg && (msg.textContent = '메일 전송 중 오류가 발생했습니다.');
        msg && (msg.className   = 'validation-message error');
        // 재시도는 입력을 다시 바꾸면 버튼이 활성화됨
      }
    });

    // 인증 완료 페이지에서 postMessage 수신
    window.addEventListener('message', (e) => {
      try {
        if (e?.data?.type === 'EMAIL_VERIFIED' && emailState.lastSentEmail) {
          checkEmailStatus(emailState.lastSentEmail);
        }
      } catch (_) {}
    });

    // IIFE로 이미 wireNickname 실행됨 — 추가 호출 불필요!
  });
})();
