<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>식물 추천 결과</title>

  <!-- 공통/페이지 스타일 -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AIservice.css" />

  <!-- 버튼 동작(조건 전송) -->
  <script defer src="${pageContext.request.contextPath}/js/aiService.js"></script>
</head>
<body data-ctx="${pageContext.request.contextPath}">
  <!-- 상단 네비 -->
  <%@ include file="/WEB-INF/views/include/nav.jsp" %>
  <!-- 메인 2열 레이아웃 -->
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

      <!-- 우측 콘텐츠(결과 포함) -->
      <main class="board-content">
        <div class="board-card">
          <c:set var="btnLabel">
            <c:choose>
              <c:when test="${selectedBtn=='PRETTY'}">예쁜 식물 추천</c:when>
              <c:when test="${selectedBtn=='EASY'}">키우기 쉬운 식물 추천</c:when>
              <c:when test="${selectedBtn=='RESISTANT'}">병해충에 강한 식물 추천</c:when>
              <c:when test="${selectedBtn=='REPELLENT'}">해충 퇴치 효과 식물 추천</c:when>
              <c:when test="${selectedBtn=='INTERIOR'}">인테리어 효과 식물 추천</c:when>
              <c:when test="${selectedBtn=='PRACTICAL'}">실용적(식용/허브) 식물 추천</c:when>
              <c:otherwise>식물 추천 결과</c:otherwise>
            </c:choose>
          </c:set>

          <h2><c:out value="${btnLabel}"/></h2>

          <!-- 결과 요약/액션 -->
          <div style="display:flex; justify-content:space-between; align-items:center; gap:12px; margin: 6px 0 10px;">
            <div style="font-weight:700; color:#16a34a;">
              총 <c:out value="${fn:length(plantList)}"/>건
            </div>
            <div style="display:flex; gap:8px;">
              <a class="pill-btn" style="text-decoration:none; padding:8px 12px; background:#fefce8; border:1px solid rgba(250,204,21,.45);"
                 href="${pageContext.request.contextPath}/ai/plantRecommend.do">뒤로가기</a>
            </div>
          </div>

          <!-- 결과 테이블 -->
          <c:choose>
            <c:when test="${not empty plantList}">
              <div class="result-wrap">
				  <table class="table board-table">
				    <!-- 고정 폭 비율 (원하는 대로 살짝씩 조정 가능) -->
				    <colgroup>
				      <col style="width:26%;">  <!-- 식물명 -->
				      <col style="width:18%;">  <!-- 분류 -->
				      <col style="width:12%;">  <!-- 난이도 -->
				      <col style="width:22%;">  <!-- 적정온도 -->
				      <col style="width:22%;">  <!-- 생장일수 -->
				    </colgroup>
				
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
				          <td class="td-name"><c:out value="${p.name}"/></td>
				          <td class="td-series"><c:out value="${p.series}"/></td>
				          <td class="td-num"><c:out value="${p.difficulty}"/></td>
				          <td class="td-num">
				            <c:out value="${p.minTemp}"/> ~ <c:out value="${p.maxTemp}"/>℃
				          </td>
				          <td class="td-num">
				            <c:out value="${p.minGrowDays}"/> ~ <c:out value="${p.maxGrowDays}"/>일
				          </td>
				        </tr>
				      </c:forEach>
				    </tbody>
				  </table>
				</div>
            </c:when>
            <c:otherwise>
              <div class="empty" style="padding:18px; border-radius:12px; background:#fff; border:1px solid #e5e7eb;">
                조건에 맞는 결과가 없어요. 다른 조건으로 다시 검색해 보세요. 🙂
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </main>
    </div>
  </section>

  <!-- 공통 스크립트 -->
  <script src="${pageContext.request.contextPath}/js/main.js"></script>

  <!-- 폴백(메인 스크립트 미로드 시) -->
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
