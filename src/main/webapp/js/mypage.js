(function () {
  const $ = (sel, p = document) => p.querySelector(sel);
  const $$ = (sel, p = document) => Array.from(p.querySelectorAll(sel));

  // 탭 전환
  function showTab(tabName) {
    $$("[data-section]").forEach(sec => sec.hidden = (sec.dataset.section !== tabName));
    $$(".menu-link").forEach(a => a.classList.toggle("active", a.dataset.tab === tabName));
    if (location.hash !== "#" + tabName) history.replaceState(null, "", "#" + tabName);
  }
  window.addEventListener("hashchange", () => {
    const tab = (location.hash || "#info-view").replace("#", "");
    showTab(tab);
  });

  // 조회/수정 바인딩
  function bindView() {
    const u = window.LOGIN_USER || {};
    $("#v-userId")   && ($("#v-userId").textContent   = u.userId   || "-");
    $("#v-email")    && ($("#v-email").textContent    = u.email    || "-");
    $("#v-phone")    && ($("#v-phone").textContent    = u.phone    || "-");
    $("#v-nickname") && ($("#v-nickname").textContent = u.nickname || "-");
    $("#v-joinedAt") && ($("#v-joinedAt").textContent = u.joinedAt || "-");
    $("#v-address1") && ($("#v-address1").textContent = u.address1 || "-");
    $("#v-address2") && ($("#v-address2").textContent = u.address2 || "-");
  }
  function bindEdit() {
    const u = window.LOGIN_USER || {};
    $("#userId")   && ($("#userId").value   = u.userId   || "");
    $("#email")    && ($("#email").value    = u.email    || "");
    $("#phone")    && ($("#phone").value    = u.phone    || "");
    $("#nickname") && ($("#nickname").value = u.nickname || "");
    $("#address1") && ($("#address1").value = u.address1 || "");
  }

  // 더미 데이터 (연동 전)
  const dummyFreePosts = [
    { title: "첫 자유글", likes: 3, views: 120, date: "2025-08-10", url: APP_CTX + "/board/view?idx=1" },
    { title: "UI 질문합니다", likes: 1, views: 54, date: "2025-08-15", url: APP_CTX + "/board/view?idx=2" },
  ];
  const dummyTradePosts = [
    { title: "키보드 팝니다", status: "판매중", views: 45, date: "2025-08-12", url: APP_CTX + "/transaction/view?idx=10" },
    { title: "모니터 교환 원합니다", status: "거래완료", views: 77, date: "2025-08-18", url: APP_CTX + "/transaction/view?idx=11" },
  ];
  const dummyComments = [
    { content: "저도 동의합니다.", url: APP_CTX + "/board/view?idx=1#c-5" },
    { content: "가격 조정 가능할까요?", url: APP_CTX + "/transaction/view?idx=10#c-2" },
    { content: "해결됐습니다. 감사합니다.", url: APP_CTX + "/board/view?idx=2#c-1" },
  ];
  const dummyNoticeComments = [
    { content: "공지사항 잘 확인했습니다.", url: APP_CTX + "/boardNotice/view?idx=100#c-3" },
    { content: "이 점 꼭 반영해주세요.",     url: APP_CTX + "/boardNotice/view?idx=101#c-1" },
  ];

  // 표 렌더 도우미
  function renderRows(tbody, rows, emptyEl) {
    tbody.innerHTML = "";
    if (!rows || rows.length === 0) { emptyEl && (emptyEl.hidden = false); return; }
    emptyEl && (emptyEl.hidden = true);
    rows.forEach(html => tbody.insertAdjacentHTML("beforeend", html));
  }

  function loadFreePosts() {
    const tb = $("#tbl-free-posts tbody"); if (!tb) return;
    const rows = dummyFreePosts.map(p =>
      `<tr>
        <td><a href="${p.url}">${p.title}</a></td>
        <td>${p.likes}</td>
        <td>${p.views}</td>
        <td>${p.date}</td>
      </tr>`);
    renderRows(tb, rows, $("#empty-free"));
  }
  function loadTradePosts() {
    const tb = $("#tbl-trade-posts tbody"); if (!tb) return;
    const rows = dummyTradePosts.map(p =>
      `<tr>
        <td><a href="${p.url}">${p.title}</a></td>
        <td>${p.status}</td>
        <td>${p.views}</td>
        <td>${p.date}</td>
      </tr>`);
    renderRows(tb, rows, $("#empty-trade"));
  }

  // 단일 컬럼 댓글 리스트
  function loadMyComments() {
    const tb = $("#tbl-my-comments tbody"); if (!tb) return;
    const rows = dummyComments.map(c => `<tr><td><a href="${c.url}">${c.content}</a></td></tr>`);
    renderRows(tb, rows, $("#empty-comments"));
  }
  function loadNoticeComments() {
    const tb = $("#tbl-notice-comments tbody"); if (!tb) return;
    const rows = dummyNoticeComments.map(c => `<tr><td><a href="${c.url}">${c.content}</a></td></tr>`);
    renderRows(tb, rows, $("#empty-notice-comments"));
  }

  // 저장 (연동 시 AJAX로 교체)
  function onSave() {
    const payload = {
      userId:   $("#userId")?.value.trim()   || "",
      email:    $("#email")?.value.trim()    || "",
      phone:    $("#phone")?.value.trim()    || "",
      nickname: $("#nickname")?.value.trim() || "",
      address1: $("#address1")?.value.trim() || "",
    };
    // TODO: fetch(APP_CTX + "/api/mypage/profile", { method:"POST", headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload) })
    alert("저장되었습니다. (더미)");
    Object.assign(window.LOGIN_USER, payload);
    bindView();
    showTab("info-view");
  }

  function init() {
    // 탭 전환 (사이드바 + 버튼)
    document.addEventListener("click", (e) => {
      const el = e.target.closest("[data-tab]");
      if (!el) return;
      const tab = el.getAttribute("data-tab");
      if (tab) { e.preventDefault(); showTab(tab); }
    });

    // 초기 탭: 회원정보(조회)
    const initial = (location.hash || "#info-view").replace("#", "");
    showTab(initial);

    bindEdit();
    bindView();
    loadFreePosts();
    loadTradePosts();
    loadMyComments();
    loadNoticeComments();

    $("#btn-save")?.addEventListener("click", onSave);
  }

  document.addEventListener("DOMContentLoaded", init);
})();
