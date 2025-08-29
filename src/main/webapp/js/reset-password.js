// /js/reset-password.js
(function () {
  const $ = (s, p = document) => p.querySelector(s);

  // ===== 공통 유틸 =====
  function headers(ct = 'application/x-www-form-urlencoded') {
    const h = { 'Content-Type': ct };
    const t = document.querySelector('meta[name="_csrf"]')?.content;
    const n = document.querySelector('meta[name="_csrf_header"]')?.content;
    if (t && n) h[n] = t;
    return h;
  }
  function meta(name, fallback = '') {
    return document.querySelector(`meta[name="${name}"]`)?.content || fallback;
  }
  function loginUrl() {
    // 우선순위: <meta name="login-url"> > 하단 링크(.signup-link) > 기본값
    return (
      meta('login-url') ||
      $('.signup-link')?.getAttribute('href') ||
      '/myLogin.do'
    );
  }
  function fullEmail() {
    const l = ($('#rpEmailLocal')?.value || '').trim();
    const d = ($('#rpEmailDomain')?.value || '').trim();
    return l && d ? `${l}@${d}` : '';
  }
  function setNote(el, text, type /* 'success' | 'error' | '' */) {
    if (!el) return;
    el.textContent = text || '';
    el.className = 'note' + (type ? ` ${type}` : '');
  }

  // STEP1 입력 잠그기/풀기
  function lockStep1(lock) {
    [
      '#rpUserId',
      '#rpEmailLocal',
      '#rpEmailDomain',
      '#rpDomainSelect',
      '#btnSendRP',
    ].forEach(sel => {
      const el = $(sel);
      if (el) el.disabled = !!lock;
    });
  }

  // 도메인 select → 입력 싱크
  $('#rpDomainSelect')?.addEventListener('change', (e) => {
    const v = e.target.value;
    const d = $('#rpEmailDomain');
    if (!d) return;
    if (v) {
      d.value = v;
      d.readOnly = true;
    } else {
      d.readOnly = false;
      d.value = '';
    }
  });

  // ===== 이메일 인증 폴링 =====
  let poll = null;
  function stopPoll() {
    if (poll) {
      clearInterval(poll);
      poll = null;
    }
  }
  async function pollStatus(email, onVerified) {
    stopPoll();
    const url = meta('email-verify-status-url', '/api/auth/email/status');
    const tick = async () => {
      try {
        const res = await fetch(`${url}?email=${encodeURIComponent(email)}`, {
          credentials: 'same-origin',
        });
        if (!res.ok) return;
        const data = await res.json().catch(() => ({}));
        if (data.verified === true) {
          stopPoll();
          onVerified && onVerified();
        }
      } catch (_) {}
    };
    poll = setInterval(tick, 5000);
    tick();
  }

  // ===== STEP 1: 인증 메일 요청 =====
  $('#btnSendRP')?.addEventListener('click', async () => {
    const userId = ($('#rpUserId')?.value || '').trim();
    const email = fullEmail();
    const msg = $('#rpMsg');

    if (!userId) return setNote(msg, '아이디를 입력해 주세요.', 'error');
    if (!email || !email.includes('@')) return setNote(msg, '올바른 이메일을 입력해 주세요.', 'error');

    // 즉시 피드백
    setNote(msg, '인증 메일을 전송했습니다. 메일함을 확인해 주세요.', '');

    // 서버에 "재설정 요청" (폼 URL 인코딩 방식 — 컨트롤러 consumes와 동일)
    const reqUrl = meta('recover-reset-request-url', '/api/recover/reset-password/request');
    try {
      const res = await fetch(reqUrl, {
        method: 'POST',
        headers: headers(),
        credentials: 'same-origin',
        body: new URLSearchParams({ userId, email }).toString(),
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        return setNote(msg, data?.message || '전송에 실패했습니다. 아이디/이메일을 확인해 주세요.', 'error');
      }

      // 인증 완료될 때까지 상태 폴링 → 완료 시 Step2 표시 및 STEP1 잠금
      pollStatus(email, () => {
        setNote(msg, '이메일 인증이 완료되었습니다. 새 비밀번호를 입력해 주세요.', 'success');
        $('#rpStep2').style.display = 'block';
        lockStep1(true);
      });
    } catch (e) {
      setNote(msg, '전송 중 오류가 발생했습니다.', 'error');
    }
  });

  // ===== STEP 2: 비밀번호 규칙 & 재설정 =====
  // 가입 때와 동일 규칙: 8~12자, 영문/숫자/특수기호 각각 1개 이상
  const PW_RULE = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_\-+\=[\]{}|\\;:'",.<>\/?`~]).{8,12}$/;

  function validatePw() {
    const p1 = ($('#rpNewPwd')?.value || '').trim();
    const p2 = ($('#rpNewPwd2')?.value || '').trim();
    const msg = $('#rpPwMsg');
    const btn = $('#btnDoReset');

    if (!p1 && !p2) {
      setNote(msg, '', '');
      btn.disabled = true;
      return false;
    }
    if (p1 !== p2) {
      setNote(msg, '비밀번호가 일치하지 않습니다.', 'error');
      btn.disabled = true;
      return false;
    }
    if (!PW_RULE.test(p1)) {
      setNote(msg, '8~12자이며 영문/숫자/특수기호를 모두 포함해야 합니다.', 'error');
      btn.disabled = true;
      return false;
    }
    setNote(msg, '사용 가능한 비밀번호입니다.', 'success');
    btn.disabled = false;
    return true;
  }

  $('#rpNewPwd')?.addEventListener('input', validatePw);
  $('#rpNewPwd2')?.addEventListener('input', validatePw);

  // 재설정 호출
  $('#btnDoReset')?.addEventListener('click', async () => {
    if (!validatePw()) return;

    const userId = ($('#rpUserId')?.value || '').trim();
    const email = fullEmail();
    const newPassword = ($('#rpNewPwd')?.value || '').trim();
    const done = $('#rpDoneMsg');

    const url = meta('recover-reset-confirm-url', '/api/recover/reset-password/confirm');
    try {
      const res = await resFetch(url, { userId, email, newPassword });

      const data = await res.json().catch(() => ({}));
      if (res.ok) {
        setNote(done, data?.message || '비밀번호가 변경되었습니다.', 'success');
        $('#btnDoReset').disabled = true;

        // ✅ 성공: alert → 로그인 페이지로 이동
        alert('비밀번호가 재설정되었습니다. 로그인 페이지로 이동합니다.');
        window.location.assign(loginUrl());
      } else {
        setNote(done, data?.message || '재설정에 실패했습니다. 다시 시도해 주세요.', 'error');
      }
    } catch (e) {
      setNote(done, '재설정 중 오류가 발생했습니다.', 'error');
    }
  });

  // fetch 래퍼 (중복 파라미터 구성 정리)
  async function resFetch(url, params) {
    return fetch(url, {
      method: 'POST',
      headers: headers(),
      credentials: 'same-origin',
      body: new URLSearchParams(params).toString(),
    });
  }
})();
