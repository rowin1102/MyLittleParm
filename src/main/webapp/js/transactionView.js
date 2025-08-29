// js/transactionView.js  (JSP/EL 금지, data-*만 사용)
(() => {
  const root = document.getElementById('txView');
  if (!root) return;

  // ===== 데이터 주입 =====
  const CTX      = root.dataset.ctx || '';
  const loginId  = root.dataset.loginId || '';
  const writerId = root.dataset.writerId || '';

  let imageList = [];
  try {
    imageList = JSON.parse(root.dataset.images || '[]')
      .map(s => (s || '').trim())
      .filter(Boolean);
  } catch (_e) {
    imageList = [];
  }

  // ===== 엘리먼트 캐시 =====
  const imgTag  = document.getElementById('img01');
  const noImage = document.getElementById('noImage');
  const prevBtn = document.getElementById('btnPrevImage');
  const nextBtn = document.getElementById('btnNextImage');
  const thumbs  = document.getElementById('thumbs');
  const chatBtn = document.getElementById('chatOpenBtn');

  let currentIndex = 0;

  // ===== 유틸 =====
  const SHOW = (el, on) => { if (el) el.style.display = on ? 'flex' : 'none'; };
  const buildImageUrl = (name) =>
    name ? CTX + '/uploads/' + encodeURIComponent(String(name).trim()) : '';

  function highlightActiveThumb() {
    document.querySelectorAll('#thumbs img').forEach((el, i) => {
      el.style.outline = (i === currentIndex) ? '2px solid #22c55e' : 'none';
      el.style.opacity = (i === currentIndex) ? '1' : '0.7';
    });
  }

  function updateImage() {
    if (!imgTag) return;

    if (!imageList.length) {
      // 서버가 처음 렌더링한 이미지가 있으면 그것만 보여주기
      if (imgTag.getAttribute('src')) {
        imgTag.style.display = 'block';
        if (noImage) noImage.style.display = 'none';
      } else {
        imgTag.style.display = 'none';
        if (noImage) noImage.style.display = 'block';
      }
      SHOW(prevBtn, false);
      SHOW(nextBtn, false);
      return;
    }

    const url = buildImageUrl(imageList[currentIndex]);
    if (!url) {
      imgTag.style.display = 'none';
      if (noImage) noImage.style.display = 'block';
      SHOW(prevBtn, false);
      SHOW(nextBtn, false);
      return;
    }

    imgTag.src = url;
    imgTag.style.display = 'block';
    // ✅ 크기는 CSS가 담당하도록 인라인 고정 제거
    imgTag.style.width = '';
    imgTag.style.height = '';
    imgTag.style.objectFit = '';
    imgTag.style.borderRadius = '';
    if (noImage) noImage.style.display = 'none';

    const hasMultiple = imageList.length > 1;
    SHOW(prevBtn, hasMultiple);
    SHOW(nextBtn, hasMultiple);

    highlightActiveThumb();
  }

  function renderGallery() {
    if (!thumbs) return;
    thumbs.innerHTML = '';

    if (!imageList.length) {
      thumbs.style.display = 'none';
      return;
    }
    thumbs.style.display = 'flex';

    imageList.forEach((name, idx) => {
      const url = buildImageUrl(name);

      const btn = document.createElement('button');
      btn.type = 'button';
      btn.style.border = '0';
      btn.style.padding = '0';
      btn.style.background = 'transparent';
      btn.style.cursor = 'pointer';

      const img = document.createElement('img');
      img.src = url;
      img.alt = 'thumb-' + (idx + 1);
      img.style.width = '72px';
      img.style.height = '72px';
      img.style.objectFit = 'cover';
      img.style.borderRadius = '8px';
      img.style.opacity = '0.7';

      btn.addEventListener('click', () => {
        currentIndex = idx;
        updateImage();
      });

      btn.appendChild(img);
      thumbs.appendChild(btn);
    });

    highlightActiveThumb();
  }

  // ===== 내비 버튼 =====
  prevBtn?.addEventListener('click', () => {
    if (!imageList.length) return;
    currentIndex = (currentIndex - 1 + imageList.length) % imageList.length;
    updateImage();
  });

  nextBtn?.addEventListener('click', () => {
    if (!imageList.length) return;
    currentIndex = (currentIndex + 1) % imageList.length;
    updateImage();
  });

  // ===== 전역에서 호출되는 함수들(window에 바인딩) =====
  window.transactionEdit = function (idx) {
    const url = CTX + '/transactionEdit.do?transaction_idx=' + encodeURIComponent(idx);
    if (confirm('수정 하시겠습니까?')) {
      location.href = url;
    }
    return false;
  };

  window.transaction_deletePost = function (idx, s1, s2, s3) {
    // 히든 폼 확보(없으면 즉시 생성)
    let f = document.forms['transactionDeleteFrm'];
    if (!f) {
      f = document.createElement('form');
      f.method = 'post';
      f.style.display = 'none';
      f.id = 'transactionDeleteFrm';
      f.name = 'transactionDeleteFrm';

      ['transaction_idx','sfile1','sfile2','sfile3'].forEach(n => {
        const i = document.createElement('input');
        i.type = 'hidden';
        i.name = n;
        f.appendChild(i);
      });
      document.body.appendChild(f);
    }

    // 실제 매핑 경로에 맞춰 주세요 (예: /transaction_Delete.do 또는 /transaction/delete.do)
    f.action = CTX + '/transaction_Delete.do';

    f.elements['transaction_idx'].value = idx || '';
    f.elements['sfile1'].value = s1 || '';
    f.elements['sfile2'].value = s2 || '';
    f.elements['sfile3'].value = s3 || '';

    if (confirm('삭제하시겠습니까?')) {
      f.submit();
    }
    return false;
  };

  window.transaction_purchase = function (idx) {
    if (confirm('구매 하시겠습니까?')) {
      location.href = CTX + '/transaction_purchase.do?transaction_idx=' + encodeURIComponent(idx);
    }
    return false;
  };

  // ===== 채팅 열기 =====
  chatBtn?.addEventListener('click', async () => {
    try {
      const body = new URLSearchParams();
      body.set('writerUserId', writerId);
      body.set('loginUserId',  loginId);

      const res = await fetch(`${CTX}/chat/room/open`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body
      });

      if (res.ok) {
        const data = await res.json();
        const roomId   = data.roomId   ?? '';
        const roomName = data.roomName ?? '';
        location.href = `${CTX}/chat.do?roomId=${encodeURIComponent(roomId)}&roomName=${encodeURIComponent(roomName)}`;
        return;
      }

      // 폴백(GET)
      location.href = `${CTX}/chat/room/open?writerUserId=${encodeURIComponent(writerId)}&loginUserId=${encodeURIComponent(loginId)}`;
    } catch (e) {
      alert((e && e.message) || '채팅방 생성 중 오류');
    }
  });

  // 초기 렌더
  renderGallery();
  updateImage();
})();
