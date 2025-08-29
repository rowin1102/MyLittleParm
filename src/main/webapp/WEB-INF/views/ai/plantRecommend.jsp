<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
<body data-ctx="${pageContext.request.contextPath}">
  <!-- 네비게이션 -->
  <%@ include file="/WEB-INF/views/include/nav.jsp" %>

  <!-- ===== 사이드바 + 본문 2열 레이아웃 ===== -->
  <section class="board-layout section" style="padding-top: 2rem;">
    <div class="board-container">

      <!-- 좌측 사이드바 -->
      <aside class="board-sidebar">
        <nav class="sidebar-card">
          <h3 class="sidebar-title">AI 추천서비스</h3>
          <ul class="sidebar-menu">
            <li class="active"><a href="${pageContext.request.contextPath}/ai/plantRecommend.do">식물 추천 서비스</a></li>
            <li><a href="${pageContext.request.contextPath}/ai/environment.do">최적화 환경 서비스</a></li>
            <li><a href="${pageContext.request.contextPath}/ai/diagnosis.do">병해충 진단 서비스</a></li>
          </ul>
        </nav>
      </aside>

      <!-- 우측 콘텐츠 -->
      <main class="board-content">
        <div class="board-card">
          <h2>식물 추천 서비스</h2>

          <!-- 버튼 6개: 2열 × 3행 -->
          <div class="btn-grid">
            <button class="pill-btn" type="button" id="pretty">예쁜 식물을 키우고 싶어요!</button>
            <button class="pill-btn" type="button" id="easy">키우기 좋은 식물이 좋아요!</button>
            <button class="pill-btn" type="button" id="resistant">병해충에 강했으면 좋겠어요!</button>
            <button class="pill-btn" type="button" id="repellent">해충 퇴치 효과가 있으면 좋겠어요!</button>
            <!-- 라벤더, 로즈마리, 레몬밤 -->
            <button class="pill-btn" type="button" id="interior">인테리어 효과가 있으면 좋겠어요!</button>
            <button class="pill-btn" type="button" id="practical">실용적인 식물이였으면 좋겠어요!</button>
          </div>

			<!-- 라벨 카드 3개 (각각: 실내/실외/온실 라디오 3개) -->
			<form action="${pageContext.request.contextPath}/ai/plantRecommend.do" method="get" id="condForm">
			  <div class="label-one">
			    <fieldset class="label-card label-card--one">
			      <legend class="label-card__title">환경 조건</legend>
			
			      <div class="range-row range-row--stacked">
			        <!-- 온도: 단일 값 -->
			        <div class="range-item">
			          <span class="range-label">온도(℃)</span>
			          <div class="env-range-inputs small">
			            <div class="input-wrap">
			              <input type="number" name="temp" id="temp"
			                     inputmode="numeric" placeholder="값 입력"
			                     value="${param.temp}"/>
			              <span class="unit">℃</span>
			            </div>
			          </div>
			        </div>
			
			        <!-- 습도: 단일 값 (가운데 행) -->
			        <div class="range-item range-item--center">
			          <span class="range-label">습도(%)</span>
			          <div class="env-range-inputs small">
			            <div class="input-wrap">
			              <input type="number" name="humidity" id="humidity"
			                     min="0" max="100" inputmode="numeric" placeholder="값 입력"
			                     value="${param.humidity}"/>
			              <span class="unit">%</span>
			            </div>
			          </div>
			        </div>
			
			        <!-- 광량: 최대만 -->
			        <div class="range-item">
			          <span class="range-label">광량(lx)</span>
			          <div class="env-range-inputs small">
			            <div class="input-wrap">
			              <input type="number" name="maxLight" id="maxLight"
			                     inputmode="numeric" placeholder="최대"
			                     value="${param.maxLight}" list="lightHints"/>
			              <span class="unit">lx</span>
			            </div>
			            <datalist id="lightHints">
			              <option value="500"/><option value="1000"/><option value="3000"/><option value="10000"/>
			            </datalist>
			          </div>
			        </div>
			      </div>
			    </fieldset>
			  </div>

  <div class="search-btn-wrap">
    <button class="search-btn" type="submit" name="search" value="1">검색</button>
  </div>
</form>

          <%-- 필요 시: 목록 테이블 + 페이징 --%>
          <%-- <table>...</table>
              <div class="pagination">${pagingImg}</div> --%>
        </div>
      </main>

    </div>
  </section>
  <c:if test="${not empty plantList}">
  <div style="margin-top:16px"></div>
  <table class="table board-table">
    <thead>
      <tr>
        <th>식물명</th>
        <th>분류</th>
        <th>난이도</th>
        <th>적정온도</th>
        <th>생장일수</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="p" items="${plantList}">
        <tr>
          <td>${p.name}</td>
          <td>${p.series}</td>
          <td>${p.difficulty}</td>
          <td>
            <c:out value="${p.minTemp}"/> ~ <c:out value="${p.maxTemp}"/>℃
          </td>
          <td>
            <c:out value="${p.minGrowDays}"/> ~ <c:out value="${p.maxGrowDays}"/>일
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</c:if>

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
