<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>공지사항 - 상세보기</title>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- 공용 스타일 + 상세 보기 전용 스타일 -->
<link rel="stylesheet" href="${ctx}/css/boardNoticeView.css" />
<link rel="stylesheet" href="${ctx}/css/board.css" />
</head>
<body>

	<%@ include file="../include/nav.jsp"%>

	<!-- 로그인 유저 아이디를 변수로 가져오기 -->
	<sec:authorize access="isAuthenticated()">
		<sec:authentication property="name" var="loginId" />
	</sec:authorize>

	<section class="board-layout section" style="padding-top: 2rem;">
		<div class="board-container">

			<aside class="board-sidebar">
				<nav class="sidebar-card">
					<h3 class="sidebar-title">커뮤니티</h3>
					<ul class="sidebar-menu">
						<li><a href="${ctx}/boardList.do">소통게시판</a></li>
						<li class="active"><a href="${ctx}/boardNoticeList.do">공지사항</a></li>
					</ul>
				</nav>
			</aside>

			<div class="view-container">
				<!-- 제목 / 메타 -->
				<div class="view-header">
					<h2>
						<c:out value="${boardNoticeDTO.board_title}" default="제목 없음" />
					</h2>
					<div class="view-meta">
						<span>작성자: <c:out value="${boardNoticeDTO.writer}"
								default="-" />
						</span> <span>등록일: <c:out value="${boardNoticeDTO.board_date}"
								default="-" />
						</span> <span>조회수: <c:out
								value="${boardNoticeDTO.board_visitcount}" default="0" />
						</span>
					</div>
				</div>

				<!-- 본문 -->
				<div class="view-body">
					<c:out value="${boardNoticeDTO.board_content}" escapeXml="false" />
				</div>

				<!-- 첨부파일/미리보기 -->
				<c:if
					test="${not empty boardNoticeDTO.ofile1 or not empty boardNoticeDTO.ofile2 or not empty boardNoticeDTO.ofile3}">
					<div class="attach-box">
						<div class="attach-title">첨부파일</div>
						<div class="attach-preview">
							<!-- 파일1 -->
							<c:if test="${not empty boardNoticeDTO.ofile1}">
								<c:choose>
									<c:when test="${maps.cate1 eq 'img'}">
										<img
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile1)}"
											alt="${fn:escapeXml(boardNoticeDTO.ofile1)}" />
									</c:when>
									<c:when test="${maps.cate1 eq 'video'}">
										<video controls
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile1)}"></video>
									</c:when>
									<c:when test="${maps.cate1 eq 'audio'}">
										<audio controls
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile1)}"></audio>
									</c:when>
									<c:otherwise>
										<a class="btn"
											href="<c:url value='/noticeBoardDownload.do'>
                                            <c:param name='idx' value='${fn:trim(boardNoticeDTO.board_idx)}'/>
                                            <c:param name='ofile1' value='${boardNoticeDTO.ofile1}'/>
                                            <c:param name='sfile1' value='${boardNoticeDTO.sfile1}'/>
                                         </c:url>">
											${boardNoticeDTO.ofile1} </a>
									</c:otherwise>
								</c:choose>
							</c:if>

							<!-- 파일2 -->
							<c:if test="${not empty boardNoticeDTO.ofile2}">
								<c:choose>
									<c:when test="${maps.cate2 eq 'img'}">
										<img
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile2)}"
											alt="${fn:escapeXml(boardNoticeDTO.ofile2)}" />
									</c:when>
									<c:when test="${maps.cate2 eq 'video'}">
										<video controls
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile2)}"></video>
									</c:when>
									<c:when test="${maps.cate2 eq 'audio'}">
										<audio controls
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile2)}"></audio>
									</c:when>
									<c:otherwise>
										<a class="btn"
											href="<c:url value='/noticeBoardDownload.do'>
                                            <c:param name='idx' value='${fn:trim(boardNoticeDTO.board_idx)}'/>
                                            <c:param name='ofile1' value='${boardNoticeDTO.ofile2}'/>
                                            <c:param name='sfile1' value='${boardNoticeDTO.sfile2}'/>
                                         </c:url>">
											${boardNoticeDTO.ofile2} </a>
									</c:otherwise>
								</c:choose>
							</c:if>

							<!-- 파일3 -->
							<c:if test="${not empty boardNoticeDTO.ofile3}">
								<c:choose>
									<c:when test="${maps.cate3 eq 'img'}">
										<img
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile3)}"
											alt="${fn:escapeXml(boardNoticeDTO.ofile3)}" />
									</c:when>
									<c:when test="${maps.cate3 eq 'video'}">
										<video controls
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile3)}"></video>
									</c:when>
									<c:when test="${maps.cate3 eq 'audio'}">
										<audio controls
											src="${ctx}/uploads/${fn:escapeXml(boardNoticeDTO.sfile3)}"></audio>
									</c:when>
									<c:otherwise>
										<a class="btn"
											href="<c:url value='/noticeBoardDownload.do'>
                                            <c:param name='idx' value='${fn:trim(boardNoticeDTO.board_idx)}'/>
                                            <c:param name='ofile1' value='${boardNoticeDTO.ofile3}'/>
                                            <c:param name='sfile1' value='${boardNoticeDTO.sfile3}'/>
                                         </c:url>">
											${boardNoticeDTO.ofile3} </a>
									</c:otherwise>
								</c:choose>
							</c:if>
						</div>
					</div>
				</c:if>

				<!-- 목록 버튼만 -->
				<div class="view-buttons">
					<a class="btn"
						href="<c:url value='/boardNoticeList.do'>
                        <c:param name='pageNum' value='${fn:trim(maps.pageNum)}'/>
                        <c:param name='searchField' value='${maps.searchField}'/>
                        <c:param name='searchKeyword' value='${maps.searchKeyword}'/>
                    </c:url>">목록</a>
				</div>

				<!-- 댓글 -->
				<div class="comment-wrap">
					<h3>댓글</h3>

					<!-- 댓글 목록 -->
					<c:forEach var="cmt" items="${lists}">
						<div class="comment-item">
							<div class="comment-meta">
								<span> <c:choose>
										<c:when test="${not empty cmt.writer}">
											<c:out value="${cmt.writer}" />
										</c:when>
										<c:otherwise>익명</c:otherwise>
									</c:choose>
								</span> <span> · <c:out value="${cmt.comment_date}" default="-" /></span>
							</div>

							<div class="comment-body">
								<c:out value="${cmt.comment_content}" escapeXml="false" />
							</div>

							<div class="comment-actions">
								<!-- 삭제 URL 준비 -->
								<c:url var="delUrl" value="/notice_commentDelete.do">
									<c:param name="board_idx"
										value="${fn:trim(boardNoticeDTO.board_idx)}" />
									<c:param name="comment_idx" value="${cmt.comment_idx}" />
									<c:param name="pageNum" value="${fn:trim(maps.pageNum)}" />
									<c:param name="searchField" value="${maps.searchField}" />
									<c:param name="searchKeyword" value="${maps.searchKeyword}" />
								</c:url>

								<!-- ✅ 관리자면 항상, 일반 사용자는 본인 댓글일 때만 '삭제' 노출 -->
								<sec:authorize access="hasRole('ADMIN') or (isAuthenticated() and authentication.name == #cmt.writer)">
									<a href="${delUrl}" onclick="return confirm('댓글을 삭제할까요?')">삭제</a>
								</sec:authorize>
							</div>
						</div>
					</c:forEach>

					<!-- 댓글 없음 -->
					<c:if test="${empty lists}">
						<div class="comment-item" style="color: #888;">등록된 댓글이 없습니다.</div>
					</c:if>

					<!-- 댓글 작성 -->
					<div class="comment-form">
						<form method="post"
							action="<c:url value='/notice_commentWrite.do'/>">
							<input type="hidden" name="board_idx"
								value="${fn:trim(boardNoticeDTO.board_idx)}"> <input
								type="hidden" name="pageNum" value="${fn:trim(maps.pageNum)}">
							<input type="hidden" name="searchField"
								value="${maps.searchField}"> <input type="hidden"
								name="searchKeyword" value="${maps.searchKeyword}">

							<!-- 로그인 상태에서만 작성 영역 + writer 값 설정 -->
							<sec:authorize access="isAuthenticated()">
								<input type="hidden" name="writer" value="${loginId}">
								<textarea name="comment_content" placeholder="댓글을 입력하세요"
									required></textarea>
								<div
									style="margin-top: .5rem; display: flex; justify-content: flex-end;">
									<button type="submit" class="btn primary">등록</button>
								</div>
							</sec:authorize>
						</form>

						<!-- 비로그인 안내 -->
						<sec:authorize access="isAnonymous()">
							<c:set var="currentUri" value="${pageContext.request.requestURI}" />
							<c:set var="q" value="${pageContext.request.queryString}" />
							<c:set var="redirect"
								value="${currentUri}${empty q ? '' : '?'}${q}" />
							<a href="#" class="comment-login-cta"
								onclick="location.href='${ctx}/myLogin.do?redirect=' + encodeURIComponent('${redirect}'); return false;">
								로그인 후 작성이 가능합니다. </a>
						</sec:authorize>
					</div>
				</div>
			</div>
</body>
</html>
