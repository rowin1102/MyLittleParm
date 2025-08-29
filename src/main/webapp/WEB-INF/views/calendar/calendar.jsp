<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Calendar</title>

<meta name="_csrf" content="${_csrf.token}">
<meta name="_csrf_header" content="${_csrf.headerName}">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css" />
<!-- 게시판 전용 스타일(사이드바/그리드/다크모드 포함) -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/board.css" />
<link rel="stylesheet"
	href="https://uicdn.toast.com/calendar/v2.1.3/toastui-calendar.min.css" />
<script
	src="https://uicdn.toast.com/calendar/v2.1.3/toastui-calendar.min.js"></script>
<script>window.APP_CTX = '<%=request.getContextPath()%>';</script>

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/calendar.css" />
</head>
<body>
	<%@ include file="/WEB-INF/views/include/nav.jsp"%>

	<!-- ===== 사이드바 + 본문 2열 레이아웃 ===== -->
	<section class="board-layout section" style="padding-top: 2rem;">
		<div class="board-container">

			<!-- 사이드바 (board.css 컴포넌트 사용) -->
			<aside class="board-sidebar">
				<nav class="sidebar-card">
					<h3 class="sidebar-title">마이페이지</h3>
					<ul class="sidebar-menu">
						<li class="active"><a href="${ctx}/calendar.do">캘린더</a></li>
						<li><a href="${ctx}/myPage.do">내 정보</a></li>
						<li><a href="${ctx}/myPageBoardList.do">자유게시판(내가 작성한 글)</a></li>
						<li><a href="${ctx}/myPageTransactionList.do">거래게시판(내가
								작성한 글)</a></li>
						<li><a href="${ctx}/myPageBoardCommentList.do">내가 작성한 댓글</a></li>
						<li><a href="${ctx}/myPageMemberDelete.do">회원탈퇴</a></li>   
					</ul>
				</nav>
			</aside>

			<!-- 우측 콘텐츠 -->
			<main class="board-content">

				<div class="cal-toolbar">
					<button id="btnToday">오늘</button>
					<button id="btnPrev">〈</button>
					<button id="btnNext">〉</button>
					<span id="currentMonth"></span>
				</div>

				<div id="calendar"></div>

				<!-- 식물 검색 모달 -->
				<div id="plantSearchModal" class="modal-card"
					style="display: none; position: absolute;">
					<h3>식물 검색</h3>
					<p id="selectedDateText"></p>
					<form id="plantSearchForm">
						<input type="text" id="plantKeyword" placeholder="식물 이름 입력" />
						<button type="submit">검색</button>
					</form>
					<div id="searchResult"></div>
					<div class="modal-actions">
						<button type="button" onclick="closeModal()">닫기</button>
					</div>
				</div>

				<!-- 캘린더 등록 모달 -->
				<div id="detailModal" class="modal-card"
					style="display: none; position: absolute;">
					<h3>캘린더 등록</h3>
					<form id="detailForm" class="modal-form">
						<input type="hidden" id="dDate"> <input type="hidden"
							id="dPlant"> <input type="hidden" id="dGrow">

						<div class="selected-date">
							선택한 날짜: <span id="dDateText"></span>
						</div>

						<div class="form-row">
							<label for="dTitle">제목</label> <input type="text" id="dTitle"
								maxlength="50" placeholder="제목을 입력해주세요">
						</div>

						<div class="form-row">
							<label for="dMemo">메모</label>
							<textarea id="dMemo" rows="4" maxlength="500"
								placeholder="메모를 입력하세요"></textarea>
						</div>

						<div class="form-row">
							<label for="dColor">색상</label> <select id="dColor" required>
								<option value="pink">pink</option>
								<option value="blue">blue</option>
								<option value="green">green</option>
								<option value="yellow">yellow</option>
							</select>
						</div>

						<div class="modal-actions">
							<button type="submit">저장</button>
							<button type="button" onclick="closeDetail()">취소</button>
						</div>
					</form>
				</div>

				<div id="eventDetailModal" class="modal-card"
					style="display: none; position: absolute;">
					<div class="modal-form">
						<input type="hidden" id="vId">

						<div class="form-row">
							<label>심은 식물</label> <input type="text" id="vPlant" readonly>
						</div>

						<div class="form-row">
							<label>예상 수확일</label> <input type="text" id="vHarvest" readonly>
						</div>

						<div class="form-row">
							<label>제목</label> <input type="text" id="vTitle" readonly>
						</div>

						<div class="form-row" id="memoRow">
							<label>메모</label>
							<textarea id="vMemo" rows="4" readonly></textarea>
						</div>
					</div>

					<div class="modal-actions">
						<button type="button" id="btnEdit">수정</button>
						<button type="button" id="btnDelete">삭제</button>
						<button type="button" id="btnSave" style="display: none">저장</button>
						<button type="button" id="btnCancel" style="display: none">취소</button>
					</div>
				</div>

				<div id="modalBackdrop"
					style="display: none; position: fixed; inset: 0; background: rgba(0, 0, 0, 0.15); z-index: 900;">
				</div>
				<script src="<%=request.getContextPath()%>/js/calendar.js"></script>
			</main>

		</div>
	</section>

	<!-- 다크모드 토글 버튼 -->
	<button id="themeToggle" data-dark-toggle class="dark-mode-toggle"
		aria-label="다크모드 전환">🌙 다크모드</button>

	<!-- 공통 스크립트 -->
	<script src="${pageContext.request.contextPath}/js/main.js"></script>

	<!-- 폴백: main.js 미로드 시 -->
	<script>
    (function(){
      if (window.showToast) return;
      const KEY='theme';
      const btn=document.querySelector('[data-dark-toggle]');
      const root=document.body;
      const saved=localStorage.getItem(KEY);
      if (saved==='dark') root.classList.add('dark-mode');
      const sync=()=>{ btn.textContent = root.classList.contains('dark-mode') ? '☀️ 라이트모드' : '🌙 다크모드'; };
      sync();
      btn.addEventListener('click', ()=>{
        const isDark=root.classList.toggle('dark-mode');
        localStorage.setItem(KEY, isDark ? 'dark' : 'light');
        sync();
      });
    })();
  </script>
</body>
</html>
