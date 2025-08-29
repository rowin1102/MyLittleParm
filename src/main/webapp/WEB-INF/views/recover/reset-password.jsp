<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<title>비밀번호 재설정</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/login.css" />

<c:if test="${not empty _csrf}">
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />
</c:if>

<!-- API 엔드포인트 (컨트롤러 매핑과 동일) -->
<meta name="email-verify-status-url"
	content="<c:url value='/api/auth/email/status'/>" />
<meta name="recover-reset-request-url"
	content="<c:url value='/api/recover/reset-password/request'/>" />
<meta name="recover-reset-confirm-url"
	content="<c:url value='/api/recover/reset-password/confirm'/>" />
<meta name="login-url" content="<c:url value='/myLogin.do'/>" />
</head>
<body class="page-reset">
	<c:set var="ctx" value="${pageContext.request.contextPath}" />

	<!-- 상단 중앙 로고 -->
	<div class="brand-hero">
		<a href="${ctx}/" class="brand-link" title="홈으로"> <img
			src="${ctx}/img/logo.png" alt="홈으로" class="brand-logo">
		</a>
	</div>

	<div class="login-container">
		<h2>비밀번호 재설정</h2>

		<!-- ✅ 폼으로 감싸 브라우저 경고 제거 -->
		<form id="rpForm" action="javascript:void(0)" autocomplete="off"
			novalidate>
			<!-- STEP 1: 본인 확인 -->
			<div id="rpStep1">
				<div class="step-title">1) 본인 확인</div>

				<div class="form-group">
					<input type="text" class="form-input" id="rpUserId" name="userId"
						placeholder=" " autocomplete="username" autocapitalize="off"
						spellcheck="false"> <label class="form-label"
						for="rpUserId">아이디</label>
				</div>

				<!-- ✅ 이메일 한 줄 배치 -->
				<div class="inline-domain-row mb-2">
					<!-- 로컬 파트 -->
					<div class="fg-inline email-local">
						<input type="text" class="form-input" id="rpEmailLocal"
							name="emailLocal" placeholder=" " autocomplete="off"
							inputmode="email" autocapitalize="off" spellcheck="false">
						<label class="form-label" for="rpEmailLocal">이메일</label>
					</div>

					<span class="at">@</span>

					<!-- 도메인 직접입력 -->
					<input type="text" class="form-input" id="rpEmailDomain"
						name="emailDomain" placeholder="예: gmail.com" autocomplete="off"
						inputmode="email" autocapitalize="off" spellcheck="false">

					<!-- 도메인 선택 -->
					<select id="rpDomainSelect" name="domainSelect"
						class="form-input select-compact">
						<option value="">직접입력</option>
						<option value="naver.com">naver.com</option>
						<option value="gmail.com">gmail.com</option>
						<option value="daum.net">daum.net</option>
						<option value="hanmail.net">hanmail.net</option>
					</select>
				</div>

				<button type="button" class="submit-btn" id="btnSendRP">이메일
					인증하기</button>
				<div id="rpMsg" class="note" aria-live="polite"></div>
			</div>

			<hr class="form-sep">

			<!-- STEP 2: 새 비밀번호 -->
			<div id="rpStep2" style="display: none;">
				<div class="step-title">2) 새 비밀번호 설정</div>

				<div class="form-group">
					<input type="password" class="form-input" id="rpNewPwd"
						name="newPwd" placeholder=" " autocomplete="new-password">
					<label class="form-label" for="rpNewPwd">새 비밀번호</label>
				</div>

				<div class="form-group">
					<input type="password" class="form-input" id="rpNewPwd2"
						name="newPwd2" placeholder=" " autocomplete="new-password">
					<label class="form-label" for="rpNewPwd2">새 비밀번호 확인</label>
				</div>

				<div id="rpPwMsg" class="note" aria-live="polite"></div>

				<button type="button" class="submit-btn" id="btnDoReset" disabled>비밀번호
					재설정</button>
				<div id="rpDoneMsg" class="note" style="margin-top: 10px;"
					aria-live="polite"></div>
			</div>
		</form>

		<div class="signup-row text-center mt-3">
			<a href="${ctx}/myLogin.do" class="signup-link">로그인으로 돌아가기</a>
		</div>
	</div>

	<!-- 분리된 스크립트 -->
	<script defer src="<c:url value='/js/reset-password.js'/>"></script>
</body>
</html>
