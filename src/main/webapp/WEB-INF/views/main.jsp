<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>마리팜</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<!-- 스크롤 인디케이터 -->
<div class="scroll-indicator">
	<div class="scroll-progress"></div>
</div>

<%@ include file="/WEB-INF/views/include/nav.jsp" %>

<!-- 히어로 섹션 (CTA 버튼 제거) -->
<section class="hero" id="home">
	<div class="hero-content">
		<h1>우리 집이 작은<br>농장이 되는 순간</h1>
		<p>앱과 함께 씨앗부터 수확까지, 게임처럼 즐기며 키우는 나만의 반려식물
		<br>하루 몇 분의 돌봄이 쌓여, 특별한 힐링과 성취를 선물합니다</p>
	</div>
</section>

<!-- 주요 기능 섹션 (버튼 사이즈 통일 + 링크 정리) -->
<section class="features section" id="features">
	<h2 class="section-title">마리팜의 핵심 기능</h2>
	<div class="features-grid">

		<div class="feature-card">
			<div class="feature-icon">📊</div>
			<h3>회원 관리 시스템</h3>
			<p>직관적인 인터페이스와 안전한 인증으로 편리하고 안전한 이용이 가능합니다.</p>
			<c:if test="${empty sessionScope.user}">
				<a href="${pageContext.request.contextPath}/myLogin.do" class="feature-btn">로그인하기</a>
			</c:if>
		</div>

		<div class="feature-card">
			<div class="feature-icon">📈</div>
			<h3>거래게시판</h3>
			<p>농산물 직거래와 실시간 소통으로 더 빠르고 효율적인 거래를 지원합니다.</p>
			<a href="${pageContext.request.contextPath}/transactionList.do" class="feature-btn">거래게시판 보기</a>
		</div>

		<div class="feature-card">
			<div class="feature-icon">💬</div>
			<h3>소통게시판</h3>
			<p>농업 정보와 일상을 공유하는 커뮤니티 공간입니다.</p>
			<a href="${pageContext.request.contextPath}/boardList.do" class="feature-btn">자유게시판 보기</a>
		</div>

		<div class="feature-card">
			<div class="feature-icon">📢</div>
			<h3>공지사항</h3>
			<p>업데이트와 중요한 소식을 빠르게 확인하세요.</p>
			<a href="${pageContext.request.contextPath}/boardNoticeList.do" class="feature-btn">공지사항 보기</a>
		</div>

		<div class="feature-card">
			<div class="feature-icon">🌱</div>
			<h3>식물 정보</h3>
			<p>작물별 재배 방법과 관리 팁을 한 곳에서.</p>
			<a href="${pageContext.request.contextPath}/plantList.do" class="feature-btn">식물정보 보기</a>
		</div>

	</div>
</section>

<!-- 커뮤니티 소식: 공지 | 자유 -->
<section class="community-section tight">
  <div class="community-block">
    <div class="community-head">
      <h3>커뮤니티 소식</h3>
      <div class="community-tabs">
        <button type="button" class="tab-pill is-active" data-target="#tab-notice">공지</button>
        <button type="button" class="tab-pill" data-target="#tab-free">자유</button>
      </div>
    </div>

    <!-- 공지 Top 5 -->
    <div id="tab-notice" class="feed-panel is-active">
      <ul class="feed-grid">
        <c:forEach var="row" items="${noticeTop}">
          <li class="feed-card">
            <a class="feed-link"
               href="${pageContext.request.contextPath}/boardNoticeView.do?board_idx=${row.board_idx}">
              <span class="feed-title">${row.board_title}</span>
              <span class="feed-date">
                <fmt:formatDate value="${row.board_date}" pattern="yyyy-MM-dd"/>
              </span>
            </a>
          </li>
        </c:forEach>
        <c:if test="${empty noticeTop}">
          <li class="feed-empty">등록된 공지글이 없습니다.</li>
        </c:if>
      </ul>
      <div class="feed-more">
        <a class="btn-pill" href="${pageContext.request.contextPath}/boardNoticeList.do">더보기</a>
      </div>
    </div>

    <!-- 자유 Top 5 -->
    <div id="tab-free" class="feed-panel">
      <ul class="feed-grid">
        <c:forEach var="row" items="${freeTop}">
          <li class="feed-card">
            <a class="feed-link"
               href="${pageContext.request.contextPath}/boardView.do?board_idx=${row.board_idx}">
              <span class="feed-title">${row.board_title}</span>
              <span class="feed-date">
                <fmt:formatDate value="${row.board_date}" pattern="yyyy-MM-dd"/>
              </span>
            </a>
          </li>
        </c:forEach>
        <c:if test="${empty freeTop}">
          <li class="feed-empty">등록된 게시글이 없습니다.</li>
        </c:if>
      </ul>
      <div class="feed-more">
        <a class="btn-pill" href="${pageContext.request.contextPath}/boardList.do">더보기</a>
      </div>
    </div>
  </div>
</section>

<script>
  // 탭 토글
  document.querySelectorAll('.community-tabs .tab-pill').forEach(btn=>{
    btn.addEventListener('click', ()=>{
      document.querySelectorAll('.community-tabs .tab-pill').forEach(b=>b.classList.remove('is-active'));
      document.querySelectorAll('.feed-panel').forEach(p=>p.classList.remove('is-active'));
      btn.classList.add('is-active');
      document.querySelector(btn.dataset.target).classList.add('is-active');
    });
  });
</script>


<!-- AI 서비스 섹션 (그대로) -->
<section class="ai-services section" id="ai">
	<h2 class="section-title">AI 식물 추천 서비스</h2>
	<div class="ai-grid">
		<div class="ai-card">
			<h4>식물 추천 시스템</h4>
			<p>사용자 환경/선호를 분석해 최적의 작물을 추천합니다.</p>
			<div class="ai-features">
				<span class="ai-badge">AI 추천</span>
				<span class="ai-badge">식물정보</span>
				<span class="ai-badge">재배기간</span>
			</div>
			<c:if test="${not empty sessionScope.user}">
				<a href="${pageContext.request.contextPath}/ai/plant-recommend" class="ai-link">추천받기</a>
			</c:if>
		</div>
		<div class="ai-card">
			<h4>환경 최적화</h4>
			<p>작물별 최적 생장 환경을 실시간 분석/제안합니다.</p>
			<div class="ai-features">
				<span class="ai-badge">온도</span>
				<span class="ai-badge">습도</span>
				<span class="ai-badge">스마트 제어</span>
			</div>
			<c:if test="${not empty sessionScope.user}">
				<a href="${pageContext.request.contextPath}/ai/environment" class="ai-link">최적화하기</a>
			</c:if>
		</div>
		<div class="ai-card">
			<h4>병해충 진단</h4>
			<p>이미지 분석 기반 진단과 맞춤형 솔루션을 제공합니다.</p>
			<div class="ai-features">
				<span class="ai-badge">이미지 분석</span>
				<span class="ai-badge">진단 AI</span>
				<span class="ai-badge">영양제 추천</span>
			</div>
			<c:if test="${not empty sessionScope.user}">
				<a href="${pageContext.request.contextPath}/ai/diagnosis" class="ai-link">진단받기</a>
			</c:if>
		</div>
	</div>
</section>

<!-- 나작농 앱 소개 섹션 (앱 다운로드/자세히 보기 버튼 제거) -->
<section class="app-promo section">
	<h2 class="section-title">📱 나작농 앱과 함께, 새싹이랑 놀아보세요</h2>
	<p class="app-description">
		<strong>귀엽고 초록초록한 친구 <span style="color:#22c55e">‘새싹’</span>과 함께</strong><br>
		매일 출석하고, 방도 꾸미고, 나만의 작은 농장을 가꿔보세요 🌿<br>
		앱에서는 <em>게임 + 출석 + 환경 서비스</em>까지 한 번에!
	</p>
	<div class="saessak-image-container">
		<img src="${pageContext.request.contextPath}/img/main.png" alt="새싹 캐릭터" class="saessak-image">
	</div>
	<ul class="app-feature-list">
		<li> 씨앗 캐릭터 ‘새싹’과 함께 성장하는 게임</li>
		<li> 방꾸미기 기능으로 아늑한 공간 만들기</li>
		<li> 이미지 캘린더 기반 출석 보상 시스템</li>
		<li> 오늘의 날씨, 공기질을 알려주는 환경 경고 서비스</li>
	</ul>
</section>

<!-- ✅ 스마트팜의 성과 섹션 제거됨 -->

<!-- 최근 게시글 섹션 (있으면 표시) -->
<c:if test="${not empty recentPosts}">
	<section class="recent-posts section">
		<h2 class="section-title">최근 게시글</h2>
		<div class="posts-grid">
			<c:forEach var="post" items="${recentPosts}">
				<div class="post-card">
					<div class="post-category">${post.category}</div>
					<h4>${post.title}</h4>
					<p>${post.summary}</p>
					<div class="post-meta">
						<span class="post-author">${post.author}</span>
						<span class="post-date">${post.createdDate}</span>
					</div>
					<a href="${pageContext.request.contextPath}/board/view/${post.id}" class="post-link">자세히 보기</a>
				</div>
			</c:forEach>
		</div>
	</section>
</c:if>

<!-- CTA 섹션(유지) -->
<section class="cta-section">
	<h2>지금 바로 나만의 스마트 농장 시작하기</h2>
	<p>앱으로 키우고, 게임처럼 즐기며, 수확의 즐거움까지 경험하세요</p>
	<div class="cta-buttons">
		<c:choose>
			<c:when test="${empty sessionScope.user}">
				<a href="${pageContext.request.contextPath}/regist.do" class="btn-primary">내 농장 시작하기</a>
			</c:when>
			<c:otherwise>
				<a href="${pageContext.request.contextPath}/ai/plant-recommend" class="btn-primary">AI 추천 받기</a>
				<a href="${pageContext.request.contextPath}/transactionList.do" class="btn-secondary">거래게시판</a>
			</c:otherwise>
		</c:choose>
	</div>
</section>

<!-- 푸터 -->
<footer class="footer">
	<div class="footer-content">
		<div class="footer-section">
			<h4>SmartFarm</h4>
			<p>나만의 작은 농장을 만들어보세요</p>
		</div>
		<div class="footer-section">
			<h4>서비스</h4>
			<ul>
				<li><a href="${pageContext.request.contextPath}/ai/plantRecommend.do">AI 식물추천</a></li>
				<li><a href="${pageContext.request.contextPath}/plantList.do">식물 정보 페이지</a></li>
				<li><a href="${pageContext.request.contextPath}/transactionList.do">거래게시판</a></li>
			</ul>
		</div>
		<div class="footer-section">
			<h4>커뮤니티</h4>
			<ul>
				<li><a href="${pageContext.request.contextPath}/boardList.do">자유게시판</a></li>
				<li><a href="${pageContext.request.contextPath}/boardNoticeList.do">공지사항</a></li>
			</ul>
		</div>
	</div>
	<div class="footer-bottom">
		<p>&copy; 2025 YoungJinFarm. 모든 권리 보유.</p>
	</div>
</footer>

<!-- 다크모드 토글 버튼 --> 
	<button id="darkModeToggle" style="position: fixed; bottom: 100px; right: 20px; z-index: 9999; background: #22c55e; 
	color: white; border: none; padding: 10px 15px; border-radius: 50px; 
	cursor: pointer; box-shadow: 0 4px 15px rgba(0,0,0,0.2);"> 🌙 다크모드 </button>
<!-- 맨 위로 버튼 --> 
	<button id="scrollToTopBtn" style="position: fixed; bottom: 40px; right: 20px; z-index: 9999; 
	display: none; background: #16a34a; color: white; border: none; padding: 10px 15px; border-radius: 50px; 
	cursor: pointer; box-shadow: 0 4px 15px rgba(0,0,0,0.2);"> ⬆ 맨 위로 </button>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
