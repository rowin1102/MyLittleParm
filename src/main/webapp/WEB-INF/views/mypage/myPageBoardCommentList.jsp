<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
	 <script>
    // 페이지 뜨자마자 저장된 상태를 body에 적용 (버튼은 main에 있음)
    (function () {
      try {
        if (localStorage.getItem('darkMode') === 'true') {
          // body가 아직 없을 때를 대비해 DOMContentLoaded에도 한 번 더 보강
          document.addEventListener('DOMContentLoaded', function(){ 
            document.body.classList.add('dark-mode');
          });
          // 일부 브라우저 페인트 전에 바로 적용
          document.documentElement.classList.add('dark-start'); // 선택 사항
        }
      } catch (e) {}
    })();
  </script>
  <meta charset="UTF-8">
  <title>댓글 목록</title>

  <c:set var="ctx" value="${pageContext.request.contextPath}" />
  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/board.css" />
  <link rel="stylesheet" href="${ctx}/css/mypage.widgets.css" />
  <!-- 댓글 목록 전용 보정 (보더/격자 강조) -->
  <link rel="stylesheet" href="${ctx}/css/myPageBoardCommentList.css" />
</head>
<body>
  <%@ include file="../include/nav.jsp" %>
  <div class="page-spacer"></div>

  <section class="board-layout section">
    <div class="board-container mypage-wrap">
      <!-- 사이드바 -->
      <aside class="board-sidebar">
        <nav class="sidebar-card">
          <h3 class="sidebar-title">마이페이지</h3>
          <ul class="sidebar-menu">
            <li><a href="${ctx}/calendar.do">캘린더</a></li>
            <li><a href="${ctx}/myPage.do">내 정보</a></li>
            <li><a href="${ctx}/myPageBoardList.do">자유게시판(내가 작성한 글)</a></li>
            <li><a href="${ctx}/myPageTransactionList.do">거래게시판(내가 작성한 글)</a></li>
            <li class="active"><a href="${ctx}/myPageBoardCommentList.do">내가 작성한 댓글</a></li>
            <li><a href="${ctx}/myPageMemberDelete.do">회원 탈퇴</a></li>
          </ul>
        </nav>
      </aside>

      <!-- 본문 -->
      <main class="board-content board-card">
        <h2 class="board-title">댓글 목록</h2>

        <!-- 검색 -->
        <form method="get" action="${ctx}/myPageBoardCommentList.do" class="search-bar">
          <select name="searchField">
            <!-- 값은 소문자로 통일 -->
            <option value="comment_content"
              <c:if test="${maps.searchField == 'comment_content'}">selected</c:if>>내용</option>
          </select>
          <input type="text" name="searchKeyword" value="${maps.searchKeyword}" />
          <button type="submit" class="btn btn-primary">검색하기</button>
        </form>

        <!-- 목록 -->
        <table class="table cmt-table">
          <thead>
            <tr>
              <th style="width:20%">작성자</th>
              <th style="width:20%">날짜</th>
              <th>댓글내용</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty lists}">
                <tr><td colspan="3" class="empty">등록된 댓글이 없습니다.</td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach items="${lists}" var="row">
                  <tr>
                    <td>${row.writer}</td>
                    <td>${row.comment_date}</td>
                    <td class="text-left">
                      <a href="${ctx}/boardView.do?pageNum=${maps.pageNum}&board_idx=${row.comment_id}&searchField=${maps.searchField}&searchKeyword=${maps.searchKeyword}">
                        ${row.comment_content}
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>

        <!-- 페이지네이션 -->
        <div class="pagination">${pagingImg}</div>
      </main>
    </div>
  </section>
  <script defer src="${ctx}/js/main.js"></script>
</body>
</html>
