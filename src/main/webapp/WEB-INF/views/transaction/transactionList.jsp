<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>거래게시판</title>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<link rel="stylesheet" href="${ctx}/css/main.css" />
<link rel="stylesheet" href="${ctx}/css/transactionList.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/board.css" />
</head>
<body>

	<%@ include file="../include/nav.jsp"%>

	<section class="board-layout section" style="padding-top: 2rem;">
		<div class="transaction-board-container">

			<!-- 사이드바 -->
			<aside class="board-sidebar">
				<nav class="sidebar-card">
					<h3 class="sidebar-title">거래게시판</h3>
					<ul class="sidebar-menu">
					</ul>
				</nav>
			</aside>

			<!-- 메인 -->
			<main class="transaction-board-content">
				<h2>거래게시판</h2>

				<!-- 검색 -->
				<div class="board-search">
					<form action="${ctx}/transactionList.do" method="get">
						<select name="searchField" class="search-select">
							<option value="title">제목</option>
							<option value="content">내용</option>
						</select> <input type="text" name="searchKeyword" placeholder="검색어 입력"
							class="search-input" />
						<button type="submit" class="btn">검색</button>
						<sec:authorize access="isAuthenticated()">
							<a href="${ctx}/transactionWrite.do" class="btn">글쓰기</a>
						</sec:authorize>
					</form>
				</div>

				<!-- 카드 리스트 -->
				<div class="transaction-card-grid">
					<c:forEach var="dto" items="${lists}">
						<div class="transaction-card">
							<div class="transaction-card-image">
								<c:choose>
									<c:when test="${not empty dto.sfile1}">
										<img src="${ctx}/uploads/${dto.sfile1}"
											alt="${dto.transaction_title}" />
									</c:when>
									<c:otherwise>
										<div class="transaction-no-image">이미지 없음</div>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="transaction-card-body">
								<div class="transaction-status">
									<c:choose>
										<c:when test="${dto.transaction_istrans eq 'N'}">판매중</c:when>
										<c:when test="${dto.transaction_istrans eq 'Y'}">거래완료</c:when>
										<c:otherwise>미정</c:otherwise>
									</c:choose>
								</div>
								<h3 class="transaction-card-title">
									<a
										href="${ctx}/transactionView.do?transaction_idx=${dto.transaction_idx}">
										${dto.transaction_title} </a>
								</h3>
								<div class="transaction-card-price">
									<c:choose>
										<c:when test="${dto.transaction_price > 0}">
                      ${dto.transaction_price}원
                    </c:when>
										<c:otherwise>가격 미정</c:otherwise>
									</c:choose>
								</div>
								<div class="transaction-card-date">${dto.transaction_date}</div>
							</div>
						</div>
					</c:forEach>
				</div>

				<!-- 페이지네이션 -->
				<div class="transaction-pagination">${pagingImg}</div>
			</main>
		</div>
	</section>

	<script src="${ctx}/js/main.js"></script>
</body>
</html>
