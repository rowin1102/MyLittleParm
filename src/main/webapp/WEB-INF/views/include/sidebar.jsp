<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<aside class="board-sidebar">
  <nav class="sidebar-card">
    <h3 class="sidebar-title">커뮤니티</h3>
    <ul class="sidebar-menu">
      <li class="${activeMenu eq 'list' ? 'active' : ''}">
        <a href="${ctx}/board/list">게시판 목록</a>
      </li>

      <!-- 글 작성: 공지(notice)면 관리자만 보이도록 -->
      <c:choose>
        <!-- 공지 페이지가 아닌 경우: 모두 표시 -->
        <c:when test="${boardType ne 'notice'}">
          <li class="${activeMenu eq 'write' ? 'active' : ''}">
            <a href="${ctx}/board/write">글 작성</a>
          </li>
        </c:when>
        <!-- 공지 페이지인 경우: 관리자만 표시 -->
        <c:otherwise>
          <c:if test="${loginUser ne null and loginUser.role eq 'ADMIN'}">
            <li class="${activeMenu eq 'write' ? 'active' : ''}">
              <!-- 공지 작성 경로로 안내 -->
              <a href="${ctx}/notice/write">글 작성</a>
            </li>
          </c:if>
        </c:otherwise>
      </c:choose>

      <li class="${activeMenu eq 'notice' ? 'active' : ''}">
        <a href="${ctx}/board/notice">공지사항</a>
      </li>
    </ul>
  </nav>
</aside>
