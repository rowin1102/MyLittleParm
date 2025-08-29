(function() {
	const $ = (s, p = document) => p.querySelector(s);

	function headers(ct) {
		const h = { 'Content-Type': ct || 'application/x-www-form-urlencoded' };
		const t = document.querySelector('meta[name="_csrf"]')?.content;
		const n = document.querySelector('meta[name="_csrf_header"]')?.content;
		if (t && n) h[n] = t;
		return h;
	}

	function fullEmail() {
		const l = ($('#emailLocal')?.value || '').trim();
		const d = ($('#emailDomain')?.value || '').trim();
		return (l && d) ? (l + '@' + d) : '';
	}

	function setMsg(el, text, ok) {
		el.textContent = text || '';
		el.style.color = ok ? '#16a34a' : '#ef4444';
	}

	// 도메인 select → 입력 싱크
	$('#domainSelect')?.addEventListener('change', e => {
		const v = e.target.value;
		const d = $('#emailDomain');
		if (!d) return;
		if (v) { d.value = v; d.readOnly = true; }
		else { d.readOnly = false; d.value = ''; }
	});

	let poll = null;
	function stopPoll() { if (poll) { clearInterval(poll); poll = null; } }

	async function pollStatus(email, onVerified) {
		stopPoll();
		const url = (document.querySelector('meta[name="email-verify-status-url"]')?.content) || '/api/auth/email/status';
		const check = async () => {
			try {
				const res = await fetch(url + '?email=' + encodeURIComponent(email), { credentials: 'same-origin' });
				if (!res.ok) return;
				const data = await res.json();
				if (data.verified === true) {
					stopPoll();
					onVerified && onVerified();
				}
			} catch (_) { }
		};
		poll = setInterval(check, 5000);
		check();
	}

	// 인증 메일 전송 + 폴링 후 아이디 조회
	$('#btnSendFI')?.addEventListener('click', async () => {
		const msg = $('#fiMsg');
		const list = $('#fiResultList');
		const box = $('#fiResultBox');
		box.style.display = 'none';
		list.innerHTML = '';

		const email = fullEmail();
		if (!email || !email.includes('@')) {
			setMsg(msg, '올바른 이메일을 입력해 주세요.', false);
			return;
		}

		// 즉시 사용자 피드백(빨간색)
		setMsg(msg, '인증 메일을 전송했습니다. 메일함을 확인해 주세요.', false);

		const sendUrl = (document.querySelector('meta[name="email-verify-send-url"]')?.content) || '/api/auth/email/send';
		try {
			await fetch(sendUrl, {
				method: 'POST',
				headers: headers(),
				credentials: 'same-origin',
				body: new URLSearchParams({ email, purpose: 'FI' }).toString()
			});
		} catch (_) { /* 전송 실패 시에도 폴링은 계속 안 함, 아래 폴링은 인증 페이지에서 완료 신호가 와야 진행 */ }

		// 폴링 → 인증 완료되면 아이디 조회
		const findUrl = (document.querySelector('meta[name="recover-find-id-url"]')?.content) || '/api/recover/find-id';
		pollStatus(email, async () => {
			setMsg(msg, '이메일 인증이 완료되었습니다.', true);
			try {
				const res = await fetch(findUrl + '?email=' + encodeURIComponent(email), { credentials: 'same-origin' });
				const data = await res.json().catch(() => ({}));

				// 서버가 userIds(기본) 또는 ids(호환)로 줄 수 있으니 둘 다 처리
				const ids = (Array.isArray(data.userIds) ? data.userIds
					: Array.isArray(data.ids) ? data.ids
						: [])
					.map(v => String(v || '').trim())
					.filter(Boolean);

				list.innerHTML = ids.length
					? ids.map(id => `<li>${id}</li>`).join('')
					: '<li>해당 이메일로 가입된 아이디가 없습니다.</li>';

				box.style.display = 'block';
			} catch (e) {
				list.innerHTML = '<li>아이디 조회 중 오류가 발생했습니다.</li>';
				box.style.display = 'block';
			}
		});
	});
})();
