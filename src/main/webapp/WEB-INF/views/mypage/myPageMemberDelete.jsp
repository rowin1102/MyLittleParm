<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>회원 탈퇴</title>
  <c:set var="ctx" value="${pageContext.request.contextPath}" />
  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/board.css" />
</head>
<body>
  <%@ include file="../include/nav.jsp" %>

  <section class="section" style="max-width:720px; margin:2rem auto;">
    <div class="board-card">
      <h2>회원 탈퇴</h2>
      <p style="margin-top:.5rem;">정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.</p>

      <div class="form-actions" style="margin-top:1rem; display:flex; gap:.5rem;">
        <a href="${ctx}/myPage.do" class="btn ghost">취소</a>

        <form action="${ctx}/mypage/delete" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');" style="display:inline;">
          <c:if test="${not empty _csrf}">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          </c:if>
          <button type="submit" class="btn primary">탈퇴하기</button>
        </form>
      </div>
    </div>
  </section>

</body>
</html>
