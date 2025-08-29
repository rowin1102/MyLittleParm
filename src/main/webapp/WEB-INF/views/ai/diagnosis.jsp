<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Spring Security 태그 미사용 시 그대로 주석 유지
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
--%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>게시판 목록</title>
  <!-- 공통 네비/기본 스타일 -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
  <!-- 게시판 전용 스타일(사이드바/그리드/다크모드 포함) -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AIservice.css" />
  <script defer src="${pageContext.request.contextPath}/js/aiService.js"></script>
</head>
<body>
 <%@ include file="/WEB-INF/views/include/nav.jsp" %>


  <!-- ===== 사이드바 + 본문 2열 레이아웃 ===== -->
  <section class="board-layout section" style="padding-top: 2rem;">
    <div class="board-container">

      <!-- 좌측 사이드바 -->
      <aside class="board-sidebar">
        <nav class="sidebar-card">
          <h3 class="sidebar-title">AI 추천서비스</h3>
          <ul class="sidebar-menu">
            <li ><a href="${pageContext.request.contextPath}/ai/plantRecommend.do">식물 추천 서비스</a></li>
            <li><a href="${pageContext.request.contextPath}/ai/environment.do">최적화 환경 서비스</a></li>
            <li class="active"><a href="${pageContext.request.contextPath}/ai/diagnosis.do">병해충 진단 서비스</a></li>
          </ul>
        </nav>
      </aside>

      <!-- 우측 콘텐츠 -->
      <main class="board-content">
        <div class="board-card">
			<form method="post"
			    action="${pageContext.request.contextPath}/ai/diagnosis.do"
			    enctype="multipart/form-data"
			    class="pest-upload-form">
			
			  <h2 class="upload-title">병해충 진단 이미지 업로드</h2>
				<!-- Spring Security 쓰면 CSRF 토큰 추가 -->
			  <c:if test="${_csrf != null}">
			    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			  </c:if>
			  <!-- 업로드 영역 -->
			  <label for="pestImage" class="upload-box">
			    <span class="upload-instruction">이미지를 여기에 드래그하거나 클릭하여 업로드</span>
			    <input type="file" id="pestImage" name="pestImage" accept="image/*" hidden onchange="previewImage(event)" />
			    <img id="preview" class="preview-img" />
			  </label>
			
			  <!-- 제출 버튼 -->
			  <div class="upload-btn-wrap">
			    <button type="submit" class="upload-btn">진단 시작</button>
			  </div>
			</form>
		
        </div>
      </main>

    </div>
  </section>

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
