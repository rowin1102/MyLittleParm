// chat.js — chat logic + Kakao Map modal

(() => {
	// ===== 환경 =====
	const urlParams = new URLSearchParams(location.search);
	const roomId = urlParams.get('roomId');
	const roomName = urlParams.get('roomName');

	const ctx = window.APP_CTX || '';
	const el = {
		messages: document.getElementById('messages'),
		threadList: document.getElementById('threadList'),
		convTitle: document.getElementById('convTitle'),
		content: document.getElementById('content'),
		sendBtn: document.querySelector('.composer #sendBtn')
	};

	// ✅ 로그인 ID는 sender 인풋 그대로 사용 (문자열 비교, 숫자 변환 X)
	// ===== 환경 =====


	if (el.convTitle && roomId) el.convTitle.textContent = `${roomName}`;

	// ===== 유틸 =====
	const fmtTime = ts => new Date(ts || Date.now()).toLocaleTimeString();
	const sanitize = t => (t ?? '').toString();
	const scrollBottom = () => {
		if (!el.messages) return;
		el.messages.scrollTop = el.messages.scrollHeight;
	};

	// ===== 스레드 목록 (데모/예시) =====
	function drawThreads(roomList) {
		const listEl = document.getElementById('threadList');
		if (!listEl) return;

		listEl.innerHTML = '';
		roomList.forEach(room => {
			const row = document.createElement('div');
			row.className = 'thread';
			row.dataset.roomId = room.roomId;
			row.innerHTML = `
        <div class="avatar">${(room.targetUserId || '?')[0].toUpperCase()}</div>
        <div class="flex-grow-1">
          <div class="d-flex justify-content-between">
            <div class="name">${room.targetUserId}</div>
            <div class="meta"></div>
          </div>
          <div class="preview text-truncate">채팅방 입장</div>
        </div>
      `;
			row.onclick = () => {
				location.href =
					`${ctx}/chat.do?roomId=${encodeURIComponent(room.roomId)}&roomName=${encodeURIComponent(room.roomName)}`;
			};
			listEl.appendChild(row);
		});
	}

	document.addEventListener('DOMContentLoaded', async () => {
		try {
			const res = await fetch(`${ctx}/chat/rooms`);
			if (res.ok) {
				const rooms = await res.json();
				drawThreads(Array.isArray(rooms) ? rooms : []);
			}
		} catch (e) {
			console.error('채팅방 목록 불러오기 실패:', e);
		}
	});

	const me = document.getElementById('sender')?.value ?? ''; // ← 문자열 그대로 사용
	function pushMsg({ me: isMe, text, ts }) {
		if (!el.messages) return;

		const row = document.createElement('div');
		row.className = `msg-row ${isMe ? 'me' : 'other'}`;
		const bubble = document.createElement('div');
		bubble.className = 'bubble';

		const body = document.createElement('div');
		body.className = 'text';
		body.textContent = sanitize(text);

		const metaWrap = document.createElement('div');
		metaWrap.className = `d-flex ${isMe ? 'justify-content-end' : ''}`;

		const meta = document.createElement('div');
		meta.className = 'meta';
		meta.textContent = fmtTime(ts);
		metaWrap.appendChild(meta);

		bubble.appendChild(metaWrap);
		bubble.appendChild(body);
		row.appendChild(bubble);
		el.messages.appendChild(row);
	}


	// ===== 최근 이력 =====
	async function loadRecent() {
		if (!roomId) return;
		try {
			const res = await fetch(`${ctx}/api/messages/${roomId}/recent`);
			const list = res.ok ? await res.json() : [];
			(Array.isArray(list) ? list : []).forEach(m => {
				const mine = String(m.senderMemberId ?? '') === me; // ← 문자열 비교
				pushMsg({
					me: mine,
					text: sanitize(m.content),
					ts: m.sentAt ?? m.createdAt ?? Date.now()
				});
			});
			scrollBottom();
		} catch (e) {
			console.error('최근 이력 로딩 실패:', e);
		}
	}

	// ===== WebSocket =====
	let stomp = null;
	function connectWS() {
		if (!roomId) return;
		const sock = new SockJS(`${ctx}/ws-stomp`);
		stomp = Stomp.over(sock);
		stomp.connect({}, () => {
			// ✅ 구독
			stomp.subscribe(`/topic/rooms.${roomId}`, frame => {
				const msg = JSON.parse(frame.body);
				const mine = String(msg.senderMemberId ?? '') === me; // ← 문자열 비교
				pushMsg({
					me: mine,
					text: sanitize(msg.content),
					ts: msg.sentAt ?? Date.now()
				});
				scrollBottom();
			});
		});
	}

	// ===== 전송 =====
	function send(text) {
		if (!stomp || !roomId) return;
		const payload = {
			roomId,
			senderMemberId: me,              // ← 숫자 변환 없음, 문자열 그대로
			content: sanitize(text).trim()
		};
		if (!payload.content) return;
		stomp.send('/app/chat.send', {}, JSON.stringify(payload));
	}

	// ===== 삭제 공통 유틸 (CSRF) =====
	function getCsrfHeaders() {
		const token = document.querySelector('meta[name="_csrf"]')?.content;
		const header = document.querySelector('meta[name="_csrf_header"]')?.content;
		const h = { 'Content-Type': 'application/json' };
		if (token && header) h[header] = token;
		return h;
	}

	// ===== 하드 삭제 =====
	async function roomHardDelete() {
		if (!roomId) return;
		const res = await fetch(`${ctx}/chat/room/${encodeURIComponent(roomId)}`, {
			method: 'DELETE',
			headers: getCsrfHeaders()
		});
		if (res.status === 401) { alert('로그인이 필요합니다.'); return; }
		if (!res.ok) {
			const msg = await res.text().catch(() => '');
			throw new Error(msg || '채팅방 삭제 실패');
		}
	}

	// ===== 삭제 후 UI 정리 =====
	function afterDeleteUI() {
		try { stomp?.disconnect?.(); } catch (e) { }
		// 왼쪽 스레드 목록에서 현재 방 제거(있으면)
		const row = document.querySelector(`#threadList .thread[data-room-id="${CSS.escape(String(roomId))}"]`);
		row?.parentElement?.removeChild(row);

		// 메시지/타이틀 초기화
		if (el.messages) el.messages.innerHTML = '';
		if (el.convTitle) el.convTitle.textContent = '채팅방 선택';

		// “빈 상태” 화면으로 이동(파라미터 제거)
		location.href = `${ctx}/chat.do`; // 필요 시 경로 조정
	}

	// ===== UI 바인딩 =====
	function bindUI() {
		if (el.sendBtn) {
			el.sendBtn.addEventListener('click', () => {
				if (!el.content) return;
				const txt = el.content.value.trim();
				if (!txt) return;
				send(txt);
				el.content.value = '';
				el.content.focus();
			});
		} else {
			console.warn('[ui] 보내기 버튼을 찾지 못했습니다.');
		}

		if (el.content) el.content.addEventListener('keydown', (e) => {
			if (e.key === 'Enter' && !e.shiftKey) {
				e.preventDefault();
				const txt = el.content.value.trim();
				if (!txt) return;
				send(txt);
				el.content.value = '';
			}
		});

		window.addEventListener('load', () => {
			el.content?.focus();
			scrollBottom();
		});

		const delBtn = document.getElementById('roomDeleteBtn');
		if (delBtn && roomId) {
			delBtn.addEventListener('click', async (e) => {
				try {
					if (!confirm('정말 이 채팅방을 완전히 삭제할까요?\n(메시지 포함 영구 삭제)')) return;
					await roomHardDelete();

					alert('처리되었습니다.');
					afterDeleteUI();
				} catch (err) {
					console.error(err);
					alert(err.message || '삭제 처리 중 오류가 발생했습니다.');
				}
			});
		}
	}

	// 공개 API
	window.Chat = { send, pushMsg, scrollBottom };

	// 부트스트랩
	loadRecent();
	connectWS();
	bindUI();
})();


// ===== Kakao Map Modal =====
(() => {
	const modal = document.getElementById('kmapModal');
	if (!modal) return;

	const openBtn = document.getElementById('meetingBtn');   // 약속잡기 버튼
	const backdrop = modal.querySelector('.kmap-backdrop');
	const closeBtn = modal.querySelector('.kmap-close');
	const canvas = modal.querySelector('#kmap');             // 모달 내부 지도 캔버스
	const addrInput = document.getElementById('kmapAddress');
	const confirmBtn = document.getElementById('kmapConfirm');
	const ctx = window.APP_CTX || '';

	let map, marker, overlay, geocoder, inited = false;

	function ensureSdk() {
		return new Promise((resolve, reject) => {
			if (window.kakao?.maps?.services) return resolve();
			const key = openBtn?.dataset.kakaoKey || canvas?.dataset.kakaoKey;
			if (!key) return reject(new Error('kakaoJsKey 미지정 (data-kakao-key 누락)'));
			const s = document.createElement('script');
			s.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${encodeURIComponent(key)}&libraries=services&autoload=false`;
			s.onload = () => kakao.maps.load(resolve);
			s.onerror = reject;
			document.head.appendChild(s);
		});
	}

	async function saveMeeting({ roomId, lat, lng, address }) {
		const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
		const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

		const headers = { 'Content-Type': 'application/json' };
		if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;

		const res = await fetch(`${ctx}/api/meetings`, {
			method: 'POST',
			headers,
			body: JSON.stringify({ roomId, lat, lng, address })
		});

		if (!res.ok) {
			const msg = await res.text().catch(() => '');
			throw new Error(msg || '약속 저장 실패');
		}
		return res.json();
	}

	function sendToChat({ lat, lng, address }) {
		const name = address || '선택 좌표';
		const kakaoLink = `https://map.kakao.com/link/map/${encodeURIComponent(name)},${lat},${lng}`;
		const text = `\n${name}\n 여기에서 거래해요~`;
		if (window.Chat?.send) window.Chat.send(text);
	}

	function initMap() {
		const baseLat = parseFloat(openBtn?.dataset.lat) || parseFloat(canvas?.dataset.lat) || 37.5665;
		const baseLng = parseFloat(openBtn?.dataset.lng) || parseFloat(canvas?.dataset.lng) || 126.9780;
		const initLL = new kakao.maps.LatLng(baseLat, baseLng);
		const urlParams = new URLSearchParams(location.search);
		const roomId = urlParams.get('roomId');

		map = new kakao.maps.Map(canvas, { center: initLL, level: 3 });
		map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);
		map.addControl(new kakao.maps.MapTypeControl(), kakao.maps.ControlPosition.TOPRIGHT);

		setTimeout(() => { map.relayout(); map.setCenter(initLL); }, 0);
		window.addEventListener('resize', () => map && map.relayout());

		marker = new kakao.maps.Marker({ position: initLL, map });
		geocoder = new kakao.maps.services.Geocoder();

		const btnEl = document.createElement('div');
		btnEl.className = 'pin-button';
		btnEl.textContent = '약속 잡기';
		btnEl.onclick = function(e) {
			e.stopPropagation();
			const pos = marker.getPosition();

			geocoder.coord2Address(pos.getLng(), pos.getLat(), function(result, status) {
				const lat = pos.getLat();
				const lng = pos.getLng();
				let addr = '';

				if (status === kakao.maps.services.Status.OK && result.length > 0) {
					addr = result[0].road_address
						? result[0].road_address.address_name
						: result[0].address.address_name;
				}

				const question = addr
					? `현재 위치 정보\n주소: ${addr}\n이 장소에서 약속을 잡으시겠습니까?`
					: `주소를 찾지 못했습니다.\n이 좌표로 약속을 잡으시겠습니까?`;

				if (!confirm(question)) return;

				addrInput.value = addr;
				addrInput.dataset.lat = lat;
				addrInput.dataset.lng = lng;

				(async () => {
					try {
						if (roomId) {
							await saveMeeting({ roomId, lat, lng, address: addr });
						}
						sendToChat({ lat, lng, address: addr });
						alert('약속이 설정되었습니다! 🎉');
						closeModal();
					} catch (err) {
						console.error(err);
						alert('약속 저장에 실패했습니다. 잠시 후 다시 시도해주세요.');
					}
				})();
			});
		};

		overlay = new kakao.maps.CustomOverlay({
			yAnchor: 2,
			content: btnEl
		});

		kakao.maps.event.addListener(map, 'click', function(e) {
			const ll = e.latLng;
			marker.setPosition(ll);
			marker.setMap(map);
			overlay.setPosition(ll);
			overlay.setMap(map);
		});

		inited = true;
	}

	function openModal() {
		modal.classList.add('open');
		ensureSdk()
			.then(() => {
				if (!inited) initMap();
				setTimeout(() => kakao.maps.event.trigger(map, 'resize'), 80);
			})
			.catch(err => {
				console.error('Kakao SDK 로드 실패:', err);
				alert('지도를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.');
				modal.classList.remove('open');
			});
	}

	function closeModal() { modal.classList.remove('open'); }

	// 이벤트 바인딩
	openBtn?.addEventListener('click', openModal);
	closeBtn?.addEventListener('click', closeModal);
	backdrop?.addEventListener('click', closeModal);
	confirmBtn?.addEventListener('click', () => {
		closeModal();
	});
})();
