<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>최적화 환경 결과</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/board.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/AIservice.css" />
  <script defer src="${pageContext.request.contextPath}/js/aiService.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/views/include/nav.jsp" %>

<section class="board-layout section" style="padding-top:2rem;">
  <div class="board-container">

    <aside class="board-sidebar">
      <nav class="sidebar-card">
        <h3 class="sidebar-title">AI 추천서비스</h3>
        <ul class="sidebar-menu">
          <li><a href="${pageContext.request.contextPath}/ai/plantRecommend.do">식물 추천 서비스</a></li>
          <li class="active"><a href="${pageContext.request.contextPath}/ai/environment.do">최적화 환경 서비스</a></li>
          <li><a href="${pageContext.request.contextPath}/ai/diagnosis.do">병해충 진단 서비스</a></li>
        </ul>
      </nav>
    </aside>

    <main class="board-content">
      <div class="board-card">
        <h2>최적화 환경 결과</h2>

        <div class="env-card" style="margin-bottom:14px">
          <h3 style="margin:0">
            <strong>${p.name}</strong>
            <small style="color:#6b7280">/ ${p.englishName}</small>
          </h3>
          <div class="env-badges">
            <span class="badge">분류: ${p.series}</span>
            <span class="badge">빛: ${lightLabel}</span>
            <span class="badge">온도: ${tempLabel}</span>
            <span class="badge">습도: ${humidityLabel}</span>
          </div>
          <p class="hint">${locationTip}</p>
          <p class="hint">${careTip}</p>
        </div>

        <div class="env-grid">
          <div class="env-card">
            <h4>권장 환경 수치</h4>
            <table class="env-table">
              <thead>
              <tr><th>구분</th><th>권장 범위</th></tr>
              </thead>
              <tbody>
              <tr><td>온도</td><td>${p.minTemp} ~ ${p.maxTemp}℃</td></tr>
              <tr><td>습도</td><td>${p.minHumidity} ~ ${p.maxHumidity}%</td></tr>
              <tr><td>광량</td><td>${p.amountLight} lx (권장: ${lightLabel})</td></tr>
              <tr><td>생장일수</td><td>${p.minGrowDays} ~ ${p.maxGrowDays}일</td></tr>
              </tbody>
            </table>
          </div>

          <div class="env-card">
            <h4>검색어 & 유사한 결과</h4>
            <div class="aside">검색어: <strong>"${query}"</strong></div>
            <c:if test="${not empty candidates}">
              <ul style="margin:.5rem 0 0 1rem; line-height:1.9;">
                <c:forEach var="c" items="${candidates}">
                  <li>
                    <a href="${pageContext.request.contextPath}/ai/environment.do?plantName=${c.name}">
                      ${c.name} <span style="color:#6b7280">/ ${c.englishName}</span>
                    </a>
                  </li>
                </c:forEach>
              </ul>
            </c:if>
          </div>
        </div>

        <div class="back-wrap">
          <a class="btn" href="${pageContext.request.contextPath}/ai/environment.do">다시 검색</a>
        </div>
      </div>
    </main>

  </div>
</section>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
