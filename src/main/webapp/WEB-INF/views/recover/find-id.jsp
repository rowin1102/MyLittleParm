<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>아이디 찾기</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/login.css" />

<c:if test="${not empty _csrf}">
  <meta name="_csrf" content="${_csrf.token}" />
  <meta name="_csrf_header" content="${_csrf.headerName}" />
</c:if>

<!-- API 메타 -->
<meta name="email-verify-send-url"   content="<c:url value='/api/auth/email/send'/>" />
<meta name="email-verify-status-url" content="<c:url value='/api/auth/email/status'/>" />
<meta name="recover-find-id-url"     content="<c:url value='/api/recover/find-id'/>" />

<!-- 한 줄 배치용 보조 스타일 (login.css에 동일 규칙이 있으면 생략 가능) -->
<style>
  .inline-domain-row{display:flex;align-items:center;gap:8px;flex-wrap:nowrap}
  .inline-domain-row .fg-inline{position:relative;flex:1 1 0;min-width:120px}
  .inline-domain-row .at{flex:0 0 auto;color:#6b7280;padding:0 4px}
  .inline-domain-row .form-input{width:100%;min-width:0}
  /* 이 페이지 id에 맞춘 폭 규칙 */
  #emailDomain{flex:1 1 0;min-width:120px}
  #domainSelect.select-compact{flex:0 1 130px;min-width:110px;max-width:150px}
</style>
</head>
<body>
  <c:set var="ctx" value="${pageContext.request.contextPath}" />

  <!-- 상단 중앙 로고 -->
  <div class="brand-hero">
    <a href="${ctx}/" class="brand-link" title="홈으로">
      <img src="${ctx}/img/logo.png" alt="홈으로" class="brand-logo">
    </a>
  </div>

  <div class="login-container">
    <h2>아이디 찾기</h2>

    <!-- ✅ 이메일 한 줄 배치 -->
    <div class="inline-domain-row mb-2">
      <!-- 로컬 파트 + 플로팅 라벨 -->
      <div class="fg-inline">
        <input type="text" class="form-input" id="emailLocal" placeholder=" ">
        <label class="form-label" for="emailLocal">이메일</label>
      </div>

      <span class="at">@</span>

      <!-- 도메인 직접입력 -->
      <input type="text" class="form-input" id="emailDomain" placeholder="예: gmail.com">

      <!-- 도메인 선택 -->
      <select id="domainSelect" class="form-input select-compact">
        <option value="">직접입력</option>
        <option value="naver.com">naver.com</option>
        <option value="gmail.com">gmail.com</option>
        <option value="daum.net">daum.net</option>
        <option value="hanmail.net">hanmail.net</option>
      </select>
    </div>

    <button type="button" class="submit-btn" id="btnSendFI">이메일 인증하기</button>

    <div id="fiMsg" class="mt-3" style="font-size:14px;"></div>

    <!-- 결과 영역 -->
    <div id="fiResultBox" class="mt-3" style="display:none;">
      <div class="mb-2">인증된 이메일로 가입된 아이디:</div>
      <ul id="fiResultList" class="mb-3"></ul>
      <a href="${ctx}/myLogin.do" class="signup-link">로그인으로 돌아가기</a>
    </div>
  </div>
  <script defer src="<c:url value='/js/find-id.js'/>"></script>
</body>
</html>
