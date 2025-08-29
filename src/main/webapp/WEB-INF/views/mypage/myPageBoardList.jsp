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
  <title>게시판 목록</title>

  <c:set var="ctx" value="${pageContext.request.contextPath}" />
  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/board.css" />
  <link rel="stylesheet" href="${ctx}/css/mypage.widgets.css" />
  <link rel="stylesheet" href="${ctx}/css/boardList.css" />
  <link rel="stylesheet" href="${ctx}/css/myPageBoardList.css" />
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
            <li class="active"><a href="${ctx}/myPageBoardList.do">소통게시판(내가 작성한 글)</a></li>
            <li><a href="${ctx}/myPageTransactionList.do">거래게시판(내가 작성한 글)</a></li>
            <li><a href="${ctx}/myPageBoardCommentList.do">내가 작성한 댓글</a></li>
			<li><a href="${ctx}/myPageMemberDelete.do">회원 탈퇴</a></li>            
          </ul>
        </nav>
      </aside>

      <!-- 본문 -->
      <main class="board-content board-card">
        <h2 class="board-title">게시판 목록</h2>

        <!-- 검색 -->
        <form method="get" action="${ctx}/myPageBoardList.do" class="search-bar">
          <select name="searchField">
            <option value="board_title"   <c:if test="${maps.searchField == 'board_title'}">selected</c:if>>제목</option>
            <option value="board_content" <c:if test="${maps.searchField == 'board_content'}">selected</c:if>>내용</option>
          </select>
          <input type="text" name="searchKeyword" value="${maps.searchKeyword}" />
          <button type="submit" class="btn btn-primary">검색하기</button>
        </form>

        <!-- 목록 -->
        <table class="board-table">
          <thead>
            <tr>
              <th style="width:8%">번호</th>
              <th style="width:12%">이미지</th>
              <th>제목</th>
              <th style="width:12%">작성자</th>
              <th style="width:10%">조회수</th>
              <th style="width:14%">작성일</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty lists}">
                <tr><td colspan="6" class="empty">등록된 게시물이 없습니다.</td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach items="${lists}" var="row" varStatus="loop">
                  <tr>
                    <td>
                      ${maps.totalCount - (((maps.pageNum-1) * maps.pageSize) + loop.index)}
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${not empty row.ofile1 && (fn:endsWith(row.ofile1,'.jpg') or fn:endsWith(row.ofile1,'.jpeg') or fn:endsWith(row.ofile1,'.png') or fn:endsWith(row.ofile1,'.gif') or fn:endsWith(row.ofile1,'.webp') or fn:endsWith(row.ofile1,'.bmp') or fn:endsWith(row.ofile1,'.tiff') or fn:endsWith(row.ofile1,'.svg'))}">
                          <img src="${ctx}/uploads/${row.sfile1}" alt="이미지" class="thumb">
                        </c:when>
                        <c:when test="${not empty row.ofile2 && (fn:endsWith(row.ofile2,'.jpg') or fn:endsWith(row.ofile2,'.jpeg') or fn:endsWith(row.ofile2,'.png') or fn:endsWith(row.ofile2,'.gif') or fn:endsWith(row.ofile2,'.webp') or fn:endsWith(row.ofile2,'.bmp') or fn:endsWith(row.ofile2,'.tiff') or fn:endsWith(row.ofile2,'.svg'))}">
                          <img src="${ctx}/uploads/${row.sfile2}" alt="이미지" class="thumb">
                        </c:when>
                        <c:when test="${not empty row.ofile3 && (fn:endsWith(row.ofile3,'.jpg') or fn:endsWith(row.ofile3,'.jpeg') or fn:endsWith(row.ofile3,'.png') or fn:endsWith(row.ofile3,'.gif') or fn:endsWith(row.ofile3,'.webp') or fn:endsWith(row.ofile3,'.bmp') or fn:endsWith(row.ofile3,'.tiff') or fn:endsWith(row.ofile3,'.svg'))}">
                          <img src="${ctx}/uploads/${row.sfile3}" alt="이미지" class="thumb">
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                      </c:choose>
                    </td>
                    <td class="text-left">
                      <a href="${ctx}/boardView.do?pageNum=${maps.pageNum}&board_idx=${row.board_idx}&searchField=${maps.searchField}&searchKeyword=${maps.searchKeyword}">
                        ${row.board_title}
                      </a>
                    </td>
                    <td>${maps.userId}</td>
                    <td>${row.board_visitcount}</td>
                    <td>${row.board_date}</td>
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
