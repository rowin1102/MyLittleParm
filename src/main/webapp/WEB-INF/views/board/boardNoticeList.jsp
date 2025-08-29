 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>공지사항</title>
  <c:set var="ctx" value="${pageContext.request.contextPath}" />
  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/board.css" />
</head>
<body>

<%@ include file="../include/nav.jsp" %>

<section class="board-layout section" style="padding-top:2rem;">
  <div class="board-container">
	
    <!-- 사이드바 -->
    <aside class="board-sidebar">
      <nav class="sidebar-card">
        <h3 class="sidebar-title">커뮤니티</h3>
        <ul class="sidebar-menu">
          <li><a href="${ctx}/boardList.do">소통게시판</a></li>
          <li class="active"><a href="${ctx}/boardNoticeList.do">공지사항</a></li>
        </ul>
      </nav>
    </aside>
	
    <!-- 콘텐츠 -->
    <main class="board-content">
      <div class="board-card">
        <h2>공지사항</h2>
	
        <!-- 검색 -->
        <form method="get" action="${ctx}/boardNoticeList.do">
          <div class="board-actions">
         <select name="searchField">
           <option value="title" <c:if test="${maps.searchField eq 'title'}">selected</c:if>>제목</option>
           <option value="content" <c:if test="${maps.searchField eq 'content'}">selected</c:if>>내용</option>
         </select>
            <input type="text" name="searchKeyword"
                   value="${fn:escapeXml(maps.searchKeyword)}"
                   placeholder="검색어를 입력하세요" />
            <input type="submit" value="검색하기" />
            <span class="spacer"></span>
            <c:if test="${sessionScope.userRole eq 'ADMIN'}">
              <button type="button" class="btn primary"
                      onclick="location.href='${ctx}/boardNoticeWrite.do'">
                글쓰기
              </button>
            </c:if>
          </div>
        </form>
		
        <!-- 게시판 목록 -->
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th width="10%">번호</th>
                <th>제목</th>
                <th width="16%">작성일</th>
                <th width="12%">조회수</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty lists}">
                  <tr>
                    <td colspan="4">
                      <div class="board-empty">등록된 게시글이 없습니다.</div>
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="row" items="${lists}" varStatus="st">
                    <tr>
                      <!-- 게시글 번호 -->
                      <td style="text-align:center;">
                        ${totalCount - (((pageNum - 1) * 10) + st.index)}
                      </td>

                      <!-- 게시글 제목 / 링크 -->
                      <td style="text-align:left;">
                        <a href="${ctx}/boardNoticeView.do?board_idx=${row.board_idx}&pageNum=${maps.pageNum}&searchField=${maps.searchField}&searchKeyword=${fn:escapeXml(maps.searchKeyword)}">
                          ${row.board_title}
                        </a>
                      </td>

                      <!-- 작성일 -->
                      <td style="text-align:center;">
                        <fmt:formatDate value="${row.board_date}" pattern="yyyy-MM-dd" />
                      </td>

                      <!-- 조회수 -->
                      <td style="text-align:center;">${row.board_visitcount}</td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </div>

        <!-- 페이징 -->
        <div class="pagination">
          ${pagingImg}
        </div>
      </div>
    </main>
  </div>
</section>
<script src="${ctx}/js/main.js"></script>
</body>
</html>
