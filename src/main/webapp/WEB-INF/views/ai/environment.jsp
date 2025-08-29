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
            <li class="active"><a href="${pageContext.request.contextPath}/ai/environment.do">최적화 환경 서비스</a></li>
            <li><a href="${pageContext.request.contextPath}/ai/diagnosis.do">병해충 진단 서비스</a></li>
          </ul>
        </nav>
      </aside>

      <!-- 우측 콘텐츠 -->
      <main class="board-content">
        <div class="board-card">
          <h2>최적화 환경 서비스</h2>

          <!-- 현재 키우는 식물 검색 -->
         <form method="get"
               action="${pageContext.request.contextPath}/ai/environment.do"
               class="search-box plant-search">
         
           <label for="plantName" class="search-label">현재 키우는 식물을 입력하세요.</label>
         
           <div class="search-row">
             <input id="plantName"
                    name="plantName"
                    type="search"
                    class="search-input"
                    placeholder="예: 몬스테라, 산세베리아, 스투키"
                    value="${fn:escapeXml(param.plantName)}"
                    list="plantList"
                    aria-label="현재 키우는 식물 입력" />
         
             <button class="search-btn" type="submit">최적화 환경 추천</button>
           </div>
         
           <!-- (선택) 간단 자동완성 -->
           <datalist id="plantList">
             <option value="상추" />
             <option value="토마토" />
             <option value="국화" />
             <option value="미나리" />
             <option value="로즈마리" />
           </datalist>
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
