// 이메일 도메인 선택 핸들러
function handleDomainSelect(value) {
  const domainInput = document.getElementById('domainInput');
  if (value) { domainInput.value = value; domainInput.readOnly = true; }
  else { domainInput.value = ''; domainInput.readOnly = false; }
}

// 중복확인 상태
let isIdChecked = false;
let isNicknameChecked = false;

/* =============== 공통 유틸 =============== */
function isEmpty(value) {
  return value == null || String(value).trim() === '';
}

/* =============== CSRF 헤더 빌더(켜져있든 꺼져있든 무해) =============== */
function buildHeaders(contentType) {
  const token = document.querySelector('meta[name="_csrf"]')?.content;
  const header = document.querySelector('meta[name="_csrf_header"]')?.content;
  return {
    ...(contentType ? { 'Content-Type': contentType } : {}),
    ...(token && header ? { [header]: token } : {})
  };
}

/* =============== 중복확인 =============== */
function checkIdDuplicate(btnEl) {
  const userIdInput = document.querySelector('input[name="user_id"]');
  const messageDiv = document.getElementById('idMessage');
  const userId = userIdInput.value.trim();

  if (!userId) {
    messageDiv.textContent = '아이디를 입력해주세요.';
    messageDiv.className = 'validation-message error';
    userIdInput.focus();
    return;
  }

  btnEl.disabled = true;
  const original = btnEl.textContent;
  btnEl.textContent = '확인중...';

  fetch('/checkDuplicate.do', {
    method: 'POST',
    headers: buildHeaders('application/x-www-form-urlencoded'),
    body: 'user_id=' + encodeURIComponent(userId)
  })
  .then(r => r.json())
  .then(data => {
    if (data.available) {
      messageDiv.textContent = data.message || '사용가능합니다.';
      messageDiv.className = 'validation-message success';
      isIdChecked = true;
    } else {
      messageDiv.textContent = data.message || '아이디를 다시 입력해주세요.';
      messageDiv.className = 'validation-message error';
      isIdChecked = false;
    }
  })
  .catch(() => {
    messageDiv.textContent = '중복확인 중 오류가 발생했습니다.';
    messageDiv.className = 'validation-message error';
    isIdChecked = false;
  })
  .finally(() => {
    btnEl.disabled = false;
    btnEl.textContent = original;
  });
}

function checkNicknameDuplicate(btnEl) {
  const nicknameInput = document.querySelector('input[name="nickname"]');
  const messageDiv = document.getElementById('nicknameMessage');
  const nickname = nicknameInput.value.trim();

  if (!nickname) {
    messageDiv.textContent = '닉네임을 입력해주세요.';
    messageDiv.className = 'validation-message error';
    nicknameInput.focus();
    return;
  }

  btnEl.disabled = true;
  const original = btnEl.textContent;
  btnEl.textContent = '확인중...';

  fetch('/checkNicknameDuplicate.do', {
    method: 'POST',
    headers: buildHeaders('application/x-www-form-urlencoded'),
    body: 'nickname=' + encodeURIComponent(nickname)
  })
  .then(r => r.json())
  .then(data => {
    if (data.available) {
      messageDiv.textContent = data.message || '사용가능합니다.';
      messageDiv.className = 'validation-message success';
      isNicknameChecked = true;
    } else {
      messageDiv.textContent = data.message || '닉네임을 다시 입력해주세요.';
      messageDiv.className = 'validation-message error';
      isNicknameChecked = false;
    }
  })
  .catch(() => {
    messageDiv.textContent = '중복확인 중 오류가 발생했습니다.';
    messageDiv.className = 'validation-message error';
    isNicknameChecked = false;
  })
  .finally(() => {
    btnEl.disabled = false;
    btnEl.textContent = original;
  });
}

/* =============== 비밀번호 검증 =============== */
// 8~12자, 영문/숫자/특수기호 각각 1개 이상 포함
const PW_RULE = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_\-+\=[\]{}|\\;:'",.<>\/?`~]).{8,12}$/;

function checkPasswordMatch() {
  const password = document.querySelector('input[name="password"]').value;
  const passwordConfirm = document.querySelector('input[name="password_confirm"]').value;
  const messageDiv = document.getElementById('passwordMessage');

  if (isEmpty(passwordConfirm)) {
    messageDiv.textContent = '';
    messageDiv.className = 'validation-message';
    return;
  }

  if (password !== passwordConfirm) {
    messageDiv.textContent = '비밀번호가 일치하지 않습니다.';
    messageDiv.className = 'validation-message error';
  } else if (!PW_RULE.test(password)) {
    messageDiv.textContent = '8~12자 영문/숫자/특수기호를 모두 포함해야 합니다.';
    messageDiv.className = 'validation-message error';
  } else {
    messageDiv.textContent = '비밀번호가 일치합니다.';
    messageDiv.className = 'validation-message success';
  }
}

/* =============== 전화번호 자동 하이픈 & 안내 =============== */
function autoHyphenPhone(value) {
  const digits = value.replace(/\D/g, '').slice(0, 11);
  if (digits.length < 4) return digits;
  if (digits.length < 8) return digits.slice(0, 3) + '-' + digits.slice(3);
  return digits.slice(0, 3) + '-' + digits.slice(3, 7) + '-' + digits.slice(7);
}

function validatePhoneFormat() {
  const phoneInput = document.querySelector('input[name="phone"]');
  const phoneMessage = document.getElementById('phoneMessage');
  const phone = phoneInput.value.trim();
  if (isEmpty(phone)) {
    phoneMessage.textContent = '전화번호를 입력해 주세요.';
    phoneMessage.className = 'validation-message error';
    return;
  }
  const phonePattern = /^\d{3}-\d{4}-\d{4}$/;
  if (phonePattern.test(phone)) {
    phoneMessage.textContent = '';
    phoneMessage.className = 'validation-message';
  } else {
    phoneMessage.textContent = '000-0000-0000 형식에 맞게 입력해주세요.';
    phoneMessage.className = 'validation-message error';
  }
}

/* =============== 이메일(합치기/정규화) =============== */
function getFullEmail() {
  const local = document.querySelector('input[name="email"]').value.trim();
  const domain = document.getElementById('domainInput').value.trim();
  if (!local) return '';
  if (local.includes('@')) return local.toLowerCase();
  return domain ? (local + '@' + domain).toLowerCase() : '';
}

/* =============== 이메일 인증 상태 관리 =============== */
const emailVerifyState = { email: '', verified: false, timer: null };

function setEmailVerifyUI(ok, msg) {
  const msgEl = document.getElementById('emailVerifyMsg');
  const hidden = document.getElementById('emailVerifiedFlag');
  msgEl.textContent = msg || (ok ? '✅ 인증 완료' : '');
  msgEl.className = 'validation-message ' + (ok ? 'success' : 'error');
  if (hidden) hidden.value = ok ? 'Y' : 'N';
  emailVerifyState.verified = !!ok;
  const btn = document.getElementById('btnSendEmailVerify');
  if (btn) btn.disabled = !!ok;
}

function resetEmailVerifyIfChanged() {
  const current = getFullEmail();
  if (current !== emailVerifyState.email) {
    clearInterval(emailVerifyState.timer);
    emailVerifyState.timer = null;
    setEmailVerifyUI(false, '');
    emailVerifyState.email = current;
  }
}

async function sendEmailVerifyRequest() {
  const full = getFullEmail();
  if (!full) {
    setEmailVerifyUI(false, '이메일을 올바르게 입력해 주세요.');
    document.querySelector('input[name="email"]').focus();
    return;
  }

  const res = await fetch('/api/auth/email/send', {
    method: 'POST',
    headers: buildHeaders('application/x-www-form-urlencoded;charset=UTF-8'),
    body: new URLSearchParams({ email: full })
  });

  let data = {};
  try { data = await res.json(); } catch (_) {}
  if (!res.ok) {
    const msg = data.message || '인증메일 전송에 실패했습니다.';
    setEmailVerifyUI(false, msg);
    return;
  }

  emailVerifyState.email = full;
  setEmailVerifyUI(false, '인증메일을 발송했습니다. 메일함을 확인해 주세요.');
  startEmailVerifyPolling(full);
}

function startEmailVerifyPolling(fullEmail) {
  if (emailVerifyState.timer) clearInterval(emailVerifyState.timer);
  emailVerifyState.timer = setInterval(async () => {
    if (getFullEmail() !== fullEmail) {
      clearInterval(emailVerifyState.timer);
      emailVerifyState.timer = null;
      return;
    }
    try {
      const res = await fetch('/api/auth/email/status?email=' + encodeURIComponent(fullEmail), {
        headers: buildHeaders()
      });
      const data = await res.json();
      if (data.verified === true) {
        clearInterval(emailVerifyState.timer);
        emailVerifyState.timer = null;
        setEmailVerifyUI(true, '이메일 인증이 완료되었습니다.');
      }
    } catch (_) { /* 네트워크 오류 → 다음 주기 재시도 */ }
  }, 3000);
}

/* =============== 제출 검증 =============== */
function validateForm() {
  // 필수값 체크
  const fields = [
    { el: document.querySelector('input[name="user_id"]'), label: '아이디' },
    { el: document.querySelector('input[name="password"]'), label: '비밀번호' },
    { el: document.querySelector('input[name="password_confirm"]'), label: '비밀번호 확인' },
    { el: document.querySelector('input[name="email"]'), label: '이메일' },
    { el: document.getElementById('domainInput'), label: '이메일 도메인' },
    { el: document.getElementById('postcode'), label: '우편번호' },
    { el: document.getElementById('address'), label: '도로명 주소' },
    { el: document.getElementById('detailaddress'), label: '상세주소' },
    { el: document.querySelector('input[name="phone"]'), label: '전화번호' },
  ];

  for (const { el, label } of fields) {
    if (!el) continue;
    if (isEmpty(el.value)) {
      alert(`${label}을(를) 입력해 주세요`);
      el.focus();
      return false;
    }
  }

  // 아이디 중복확인
  if (!isIdChecked) {
    alert('아이디 중복확인을 해주세요');
    document.querySelector('input[name="user_id"]').focus();
    return false;
  }

  // 비밀번호 규칙 + 일치 여부
  const pw = document.querySelector('input[name="password"]').value;
  const pw2 = document.querySelector('input[name="password_confirm"]').value;
  if (!PW_RULE.test(pw)) {
    alert('비밀번호는 8~12자이며, 영문/숫자/특수기호를 모두 포함해야 합니다.');
    document.querySelector('input[name="password"]').focus();
    return false;
  }
  if (pw !== pw2) {
    alert('비밀번호가 일치하지 않습니다.');
    document.querySelector('input[name="password_confirm"]').focus();
    return false;
  }

  // 전화번호 형식 검사
  const phoneInput = document.querySelector('input[name="phone"]');
  const phone = phoneInput.value.trim();
  if (!/^\d{3}-\d{4}-\d{4}$/.test(phone)) {
    alert('전화번호는 000-0000-0000 형식으로 입력해 주세요.');
    phoneInput.focus();
    return false;
  }

  // 닉네임 입력 시 중복확인
  const nicknameInput = document.querySelector('input[name="nickname"]');
  if (!isEmpty(nicknameInput.value) && !isNicknameChecked) {
    alert('닉네임 중복확인을 해주세요');
    nicknameInput.focus();
    return false;
  }

  // **이메일 인증 완료 여부 체크**
  const emailVerified = document.getElementById('emailVerifiedFlag')?.value === 'Y';
  if (!emailVerified) {
    alert('이메일 인증을 완료해 주세요.');
    document.getElementById('btnSendEmailVerify')?.focus();
    return false;
  }

  return true;
}

/* =============== DOMContentLoaded 바인딩 =============== */
document.addEventListener('DOMContentLoaded', function () {
  // 아이디/닉네임 입력 변경 시 중복확인 초기화
  const userIdInput = document.querySelector('input[name="user_id"]');
  userIdInput.addEventListener('input', function () {
    isIdChecked = false;
    const messageDiv = document.getElementById('idMessage');
    messageDiv.textContent = '';
    messageDiv.className = 'validation-message';
  });

  const nicknameInput = document.querySelector('input[name="nickname"]');
  nicknameInput.addEventListener('input', function () {
    isNicknameChecked = false;
    const messageDiv = document.getElementById('nicknameMessage');
    messageDiv.textContent = '';
    messageDiv.className = 'validation-message';
  });

  // 전화번호 자동 하이픈
  const phoneInput = document.querySelector('input[name="phone"]');
  if (phoneInput) {
    phoneInput.setAttribute('maxlength', '13');
    phoneInput.addEventListener('input', function (e) {
      e.target.value = autoHyphenPhone(e.target.value);
      e.target.setSelectionRange(e.target.value.length, e.target.value.length);
    });
    phoneInput.addEventListener('paste', function (e) {
      e.preventDefault();
      const text = (e.clipboardData || window.clipboardData).getData('text');
      phoneInput.value = autoHyphenPhone(text);
    });
  }

  // 이메일 변경 시 인증상태 초기화
  const emailLocal = document.querySelector('input[name="email"]');
  const emailDomain = document.getElementById('domainInput');
  [emailLocal, emailDomain].forEach(el => {
    if (el) el.addEventListener('input', resetEmailVerifyIfChanged);
  });

  // 인증메일 보내기 버튼
  const btn = document.getElementById('btnSendEmailVerify');
  if (btn) {
    btn.addEventListener('click', () => {
      sendEmailVerifyRequest().catch(() => {
        setEmailVerifyUI(false, '인증메일 전송 중 오류가 발생했습니다.');
      });
    });
  }

  // ===== 회원가입 AJAX 제출 =====
  const form = document.getElementById('registForm') || document.querySelector('form[action="regist.do"]');
  if (form) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault(); // 기본 제출 방지

      if (!validateForm()) return;

      const fd = new FormData(form);
      // 참고: 서버에서 원하면 전체 이메일을 추가로 이용 가능
      fd.append('email_full', getFullEmail());

      const body = new URLSearchParams(fd).toString();
      const loginURL =
        document.querySelector('meta[name="login-url"]')?.content ||
        document.querySelector('.signup-link')?.getAttribute('href') ||
        '/myLogin.do';

      try {
        const res = await fetch(form.getAttribute('action') || 'regist.do', {
          method: 'POST',
          headers: buildHeaders('application/x-www-form-urlencoded;charset=UTF-8'),
          credentials: 'same-origin',
          body
        });

        // 성공 판단: 2xx 이거나 JSON으로 success/ok/status=OK
        let ok = res.ok;
        let message = '';
        const ctype = res.headers.get('content-type') || '';
        if (ctype.includes('application/json')) {
          const data = await res.json().catch(() => ({}));
          ok = ok && (data.success === true || data.ok === true || data.status === 'OK');
          message = data.message || '';
        }

        if (ok) {
          alert('회원가입이 완료되었습니다.');
          window.location.assign(loginURL);
        } else {
          const text = !ctype || ctype.includes('text/html') ? await res.text().catch(() => '') : '';
          alert(message || '회원가입 처리에 실패했습니다.\n잠시 후 다시 시도해 주세요.' + (text ? '\n\n상세:\n' + text.slice(0, 200) : ''));
        }
      } catch (err) {
        alert('네트워크 오류로 회원가입에 실패했습니다. 잠시 후 다시 시도해 주세요.');
      }
    });
  }
});

// ───── Daum 우편번호 찾기(전역) ─────
function __openDaumPostcode() {
  if (!(window.daum && window.daum.Postcode)) {
    alert('우편번호 서비스 스크립트가 아직 로드되지 않았습니다. 잠시 후 다시 시도해 주세요.');
    return;
  }
  new daum.Postcode({
    oncomplete: function (data) {
      var addr = (data.userSelectedType === 'R') ? data.roadAddress : data.jibunAddress;
      document.getElementById('postcode').value = data.zonecode;
      document.getElementById('address').value  = addr;
      document.getElementById('detailaddress').focus();
    }
  }).open();
}
// inline onclick에서 찾을 수 있도록 전역에 공개
window.execDaumPostcode = __openDaumPostcode;
