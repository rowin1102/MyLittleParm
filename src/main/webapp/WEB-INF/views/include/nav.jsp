<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="path" value="${pageContext.request.requestURI}" />

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>마리팜</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>


	<!-- 스크롤 인디케이터 -->
	<div class="scroll-indicator">
		<div class="scroll-progress"></div>
	</div>

	<!-- 네비게이션 -->
	<!-- 네비게이션 -->
	<nav class="navbar">
		<div class="nav-container">
			<div class="logo">
				<a href="${pageContext.request.contextPath}/"><img
					src="/img/logo.png" class="brand-logo">마리팜</a>
			</div>
			<ul class="nav-menu">
				<li><a href="/ai/plantRecommend.do">AI 서비스</a></li>
				<li><a href="/plantList.do">식물 정보 페이지</a></li>
				<li><a href="/transactionList.do">거래게시판</a></li>
				<sec:authorize access="isAuthenticated()">
					<li><a href="/chat.do">채팅방</a></li>
				</sec:authorize>
				<li><a href="/boardList.do">소통게시판</a></li>
				<li><a href="/boardNoticeList.do">공지사항</a></li>

				<sec:authorize access="isAuthenticated()">
					<li><span class="user-welcome"> <sec:authentication
								property="name" /> 님
					</span></li>
					<li><a href="${pageContext.request.contextPath}/myLogout.do">로그아웃</a></li>
					<li><a href="${pageContext.request.contextPath}/myPage.do">마이페이지</a></li>
				</sec:authorize>

				<sec:authorize access="!isAuthenticated()">
					<li><a href="${pageContext.request.contextPath}/myLogin.do">로그인</a></li>
					<li><a href="${pageContext.request.contextPath}/regist.do">회원가입</a></li>
				</sec:authorize>
			</ul>
		</div>
	</nav>
	<div style="height: 96px"></div>