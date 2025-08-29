<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<title>게시글 보기</title>
<link rel="stylesheet" href="${ctx}/css/board.css" />
</head>
<body>

    <%@ include file="/WEB-INF/views/include/nav.jsp"%>

    <!-- 로그인 사용자 아이디를 loginId 변수로 꺼내둠(필요시 사용) -->
    <sec:authorize access="isAuthenticated()">
        <sec:authentication property="name" var="loginId" />
    </sec:authorize>

    <section class="board-layout section" style="padding-top: 2rem;">
        <div class="board-container">

            <aside class="board-sidebar">
                <nav class="sidebar-card">
                    <h3 class="sidebar-title">커뮤니티</h3>
                    <ul class="sidebar-menu">
                        <li class="active"><a href="${ctx}/boardList.do">소통게시판</a></li>
                        <li><a href="${ctx}/boardNoticeList.do">공지사항</a></li>
                    </ul>
                </nav>
            </aside>

            <main class="board-content">
                <div class="board-card">

                    <!-- 제목 + 메타정보 -->
                    <div class="header-row">
                        <h2>${boardDTO.board_title}</h2>
                        <div class="meta">
                            <span>작성자: ${boardDTO.writer}</span>
                            <span>작성일: <fmt:formatDate value="${boardDTO.board_date}" pattern="yyyy-MM-dd HH:mm" /></span>
                            <span>조회수: ${boardDTO.board_visitcount}</span>
                            <span>좋아요: ${boardDTO.board_good}</span>
                            <span>싫어요: ${boardDTO.board_worse}</span>
                        </div>
                    </div>

                    <!-- 내용 -->
                    <div class="board-view-body">
                        <p style="white-space: pre-wrap;">${boardDTO.board_content}</p>
                    </div>

                    <!-- 버튼 영역 -->
                    <div class="form-actions">
                        <a href="${ctx}/boardList.do?pageNum=${maps.pageNum}" class="btn ghost">목록</a>

                        <!-- ✅ 관리자이거나 작성자면 수정/삭제 노출 -->
                        <sec:authorize access="hasRole('ADMIN') or authentication.name == #boardDTO.writer">
                            <a href="${ctx}/boardEdit.do?board_idx=${boardDTO.board_idx}" class="btn primary">수정</a>

                            <form action="${ctx}/boardDelete.do" method="post"
                                  style="display: inline;"
                                  onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                <input type="hidden" name="board_idx" value="${boardDTO.board_idx}" />
                                <button type="submit" class="btn ghost">삭제</button>
                            </form>
                        </sec:authorize>
                    </div>

                    <!-- 좋아요 / 싫어요 -->
                    <sec:authorize access="isAuthenticated()">
                        <div class="reactions">
                            <a href="${ctx}/boardView.do?board_idx=${boardDTO.board_idx}&num=5"
                               class="btn"
                               style="background: #e0f7e9; color: #0f5132; font-weight: 600; border: 1px solid #2ecc71; padding: 8px 16px; border-radius: 8px;">
                                👍 좋아요 (${boardDTO.board_good})
                            </a>
                            <a href="${ctx}/boardView.do?board_idx=${boardDTO.board_idx}&num=6"
                               class="btn"
                               style="background: #fdecea; color: #842029; font-weight: 600; border: 1px solid #e74c3c; padding: 8px 16px; border-radius: 8px;">
                                👎 싫어요 (${boardDTO.board_worse})
                            </a>
                        </div>
                    </sec:authorize>

                    <!-- 댓글 섹션 -->
                    <div class="comment-section">
                        <h3>댓글</h3>

                        <!-- 댓글 목록 -->
                        <c:choose>
                            <c:when test="${empty lists}">
                                <p class="comment-empty">등록된 댓글이 없습니다.</p>
                                <br />
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="comment" items="${lists}">
                                    <div class="comment-item">
                                        <div class="comment-meta">
                                            <strong>${comment.writer}</strong>
                                            <span> | <fmt:formatDate value="${comment.comment_date}" pattern="yyyy-MM-dd HH:mm" /></span>
                                        </div>
                                        <div style="margin-top: 0.3rem;">${comment.comment_content}</div>

                                        <div style="text-align: right; margin-top: 0.3rem;">
                                            <!-- ✅ 관리자 또는 댓글 작성자에게만 ‘삭제’ 노출 -->
                                            <sec:authorize access="hasRole('ADMIN') or authentication.name == #comment.writer">
                                                <a href="${ctx}/commentDelete.do?comment_idx=${comment.comment_idx}
                                                         &board_idx=${boardDTO.board_idx}
                                                         &pageNum=${maps.pageNum}
                                                         &searchField=${maps.searchField}
                                                         &searchKeyword=${maps.searchKeyword}"
                                                   class="comment-delete">삭제</a>
                                            </sec:authorize>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>

                        <!-- 댓글 작성 -->
                        <form name="commentFrm" method="post" action="${ctx}/commentWrite.do" class="comment-form">
                            <input type="hidden" name="board_idx" value="${boardDTO.board_idx}" />
                            <input type="hidden" name="pageNum" value="${maps.pageNum}" />
                            <input type="hidden" name="searchField" value="${maps.searchField}" />
                            <input type="hidden" name="searchKeyword" value="${maps.searchKeyword}" />

                            <sec:authorize access="isAuthenticated()">
                                <textarea name="comment_content" placeholder="댓글을 입력하세요"></textarea>
                                <div style="text-align: right;">
                                    <button type="submit" class="submit-btn">등록</button>
                                </div>
                            </sec:authorize>
                        </form>
                    </div>

                    <sec:authorize access="isAnonymous()">
                        <c:set var="currentUri" value="${pageContext.request.requestURI}" />
                        <c:set var="q" value="${pageContext.request.queryString}" />
                        <c:set var="redirect" value="${currentUri}${empty q ? '' : '?'}${q}" />
                        <br />
                        <a href="#" class="comment-login-cta"
                           onclick="location.href='${ctx}/myLogin.do?redirect=' + encodeURIComponent('${redirect}'); return false;">
                            로그인 후 작성이 가능합니다.
                        </a>
                    </sec:authorize>

                </div>
            </main>

        </div>
    </section>
    <script src="${ctx}/js/main.js"></script>
</body>
</html>
