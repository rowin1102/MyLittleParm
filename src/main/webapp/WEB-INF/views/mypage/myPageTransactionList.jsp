<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
  <title>마이페이지 내가올린 거래글</title>

  <c:set var="ctx" value="${pageContext.request.contextPath}" />
  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/board.css" />
  <!-- 위젯/그리드 유틸 -->
  <link rel="stylesheet" href="${ctx}/css/mypage.widgets.css" />
  <!-- 거래글 카드형 전용 스타일 -->
  <link rel="stylesheet" href="${ctx}/css/myPageTransactionList.css" />
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
            <li><a href="${ctx}/myPageBoardList.do">소통게시판(내가 작성한 글)</a></li>
            <li class="active"><a href="${ctx}/myPageTransactionList.do">거래게시판(내가 작성한 글)</a></li>
            <li><a href="${ctx}/myPageBoardCommentList.do">내가 작성한 댓글</a></li>
			<li><a href="${ctx}/myPageMemberDelete.do">회원 탈퇴</a></li>            
          </ul>
        </nav>
      </aside>

      <!-- 본문 -->
      <main class="board-content board-card">
        <h2 class="board-title">마이페이지 내가올린 거래글</h2>

        <!-- 검색 -->
        <form method="get" action="${ctx}/myPageTransactionList.do" class="search-bar">
          <select name="searchField">
            <option value="transaction_title"   <c:if test="${maps.searchField == 'transaction_title'}">selected</c:if>>제목</option>
            <option value="transaction_content" <c:if test="${maps.searchField == 'transaction_content'}">selected</c:if>>내용</option>
          </select>
          <input type="text" name="searchKeyword" value="${maps.searchKeyword}" />
          <button type="submit" class="btn btn-primary">검색하기</button>
        </form>

        <!-- 카드 그리드 -->
        <c:choose>
          <c:when test="${empty lists}">
            <div class="empty">등록된 게시물이 없습니다.</div>
          </c:when>
          <c:otherwise>
            <div class="tx-grid">
              <c:forEach items="${lists}" var="row" varStatus="loop">
                <article class="tx-card">
                  <a class="tx-thumb"
                     href="${ctx}/transactionView.do?pageNum=${maps.pageNum}&transaction_idx=${row.transaction_idx}&searchField=${maps.searchField}&searchKeyword=${maps.searchKeyword}">
                    <c:choose>
                      <c:when test="${not empty row.ofile1}">
                        <img src="${ctx}/uploads/${row.sfile1}" alt="썸네일">
                      </c:when>
                      <c:when test="${not empty row.ofile2}">
                        <img src="${ctx}/uploads/${row.sfile2}" alt="썸네일">
                      </c:when>
                      <c:when test="${not empty row.ofile3}">
                        <img src="${ctx}/uploads/${row.sfile3}" alt="썸네일">
                      </c:when>
                      <c:otherwise>
                        <div class="thumb-placeholder">No Image</div>
                      </c:otherwise>
                    </c:choose>
                  </a>

                  <div class="tx-body">
                    <div class="tx-status">
                      <c:choose>
                        <c:when test="${fn:trim(row.transaction_istrans) == 'n'}"><span class="status sale">판매중</span></c:when>
                        <c:when test="${fn:trim(row.transaction_istrans) == 'y'}"><span class="status sold">판매완료</span></c:when>
                        <c:otherwise><span class="status unknown">상태 미정</span></c:otherwise>
                      </c:choose>
                    </div>

                    <div class="tx-meta">
                      <span class="tx-no">No. ${maps.totalCount - (((maps.pageNum - 1) * maps.pageSize) + loop.index)}</span>
                      <a class="tx-title"
                         href="${ctx}/transactionView.do?pageNum=${maps.pageNum}&transaction_idx=${row.transaction_idx}&searchField=${maps.searchField}&searchKeyword=${maps.searchKeyword}">
                        <c:set var="title" value="${row.transaction_title}" />
                        <c:choose>
                          <c:when test="${fn:length(title) > 15}">${fn:substring(title,0,15)}...</c:when>
                          <c:otherwise>${title}</c:otherwise>
                        </c:choose>
                      </a>
                    </div>

                    <div class="tx-price">₩ <fmt:formatNumber value="${row.transaction_price}" type="number" groupingUsed="true"/></div>
                    <div class="tx-sub">작성일 ${row.transaction_date} · ${row.writer}</div>
                  </div>
                </article>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>

        <!-- 페이지네이션 -->
        <div class="pagination">${pagingImg}</div>
      </main>
    </div>
  </section>
  <script defer src="${ctx}/js/main.js"></script>
</body>
</html>
