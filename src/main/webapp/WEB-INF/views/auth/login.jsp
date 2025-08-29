<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link
   href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css"
   rel="stylesheet">
<link rel="stylesheet"
   href="<%=request.getContextPath()%>/css/login.css" />
</head>
<body>
   <c:set var="ctx" value="${pageContext.request.contextPath}" />

   <!-- 상단 중앙 로고 -->
   <div class="brand-hero">
      <a href="${ctx}/" class="brand-link" title="홈으로">
         <img src="${ctx}/img/logo.png" alt="홈으로" class="brand-logo">
      </a>
   </div>

   <div class="login-container">

      <c:if test="${empty user_id}" var="loginResult">
         <h2>로그인</h2>

         <c:if test="${not empty errorMsg}">
            <div class="error-message">${errorMsg}</div>
         </c:if>

         <form action="/myLoginAction.do" method="post">
            <div class="form-group">
               <input type="text" class="form-input" id="user_id" placeholder=" " name="my_id" required autocomplete="username">
               <label class="form-label" for="user_id">아이디</label>
            </div>

            <div class="form-group">
               <input type="password" class="form-input" id="user_pwd" placeholder=" " name="my_pass" required autocomplete="current-password">
               <label class="form-label" for="user_pwd">비밀번호</label>
            </div>

            <button type="submit" class="submit-btn">로그인</button>

            <div class="signup-row text-center mt-3">
               <a href="${ctx}/regist.do" class="signup-link">회원가입</a>
               <span class="mx-2">·</span>
               <a href="${ctx}/find-id.do" class="signup-link">아이디 찾기</a>
               <span class="mx-2">·</span>
               <a href="${ctx}/reset-password.do" class="signup-link">비밀번호 재설정</a>
            </div>
         </form>
      </c:if>
   </div>
</body>
</html>
