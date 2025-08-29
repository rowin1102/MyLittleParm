<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>회원 상세보기</title>
  <c:set var="ctx" value="${pageContext.request.contextPath}" />

  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/board.css" />
  <link rel="stylesheet" href="${ctx}/css/myPage.css" />

  <!-- 다크모드: 저장된 상태 선적용 -->
  <script>
    (function () {
      try {
        if (localStorage.getItem('darkMode') === 'true') {
          document.addEventListener('DOMContentLoaded', function(){
            document.body.classList.add('dark-mode');
          });
          document.documentElement.classList.add('dark-start');
        }
      } catch (e) {}
    })();
  </script>
</head>
<body>

  <%@ include file="../include/nav.jsp" %>

  <section class="account section">
    <div class="account-layout">

      <!-- 사이드바 -->
      <aside class="board-sidebar">
        <nav class="sidebar-card">
          <h3 class="sidebar-title">마이페이지</h3>
          <ul class="sidebar-menu">
            <li><a href="${ctx}/calendar.do">캘린더</a></li>
            <li class="active"><a href="${ctx}/myPage.do">내 정보</a></li>
            <li><a href="${ctx}/myPageBoardList.do">소통게시판(내가 작성한 글)</a></li>
            <li><a href="${ctx}/myPageTransactionList.do">거래게시판(내가 작성한 글)</a></li>
            <li><a href="${ctx}/myPageBoardCommentList.do">내가 작성한 댓글</a></li>
            <!-- 탈퇴는 확인페이지로 이동 후 POST 전송 -->
            <li><a href="${ctx}/myPageMemberDelete.do">회원탈퇴</a></li>
          </ul>
        </nav>
      </aside>

      <!-- 콘텐츠 -->
      <main class="account-content">
        <div class="profile-card">

          <!-- 헤더 -->
          <div class="profile-header">
            <div class="avatar">🌱</div>
            <div class="title-wrap">
              <h2 class="profile-name">
                <c:choose>
                  <c:when test="${not empty member.nickname}">
                    <c:out value="${member.nickname}" />
                  </c:when>
                  <c:otherwise>
                    <c:out value="${member.user_id}" />
                  </c:otherwise>
                </c:choose>
                <span class="role-badge">
                  <c:choose>
                    <c:when test="${member.member_auth eq 'ROLE_ADMIN'}">관리자</c:when>
                    <c:otherwise>일반회원</c:otherwise>
                  </c:choose>
                </span>
              </h2>
              <p class="sub">
                아이디 <c:out value="${member.user_id}" /> · 가입일 <c:out value="${member.postdate}" />
              </p>
            </div>
          </div>

          <!-- 상세 정보 (정상 dl/dt/dd 구조) -->
          <dl class="detail-list">
            <dt>아이디</dt>
            <dd><c:out value="${member.user_id}" /></dd>

            <dt>이메일</dt>
            <dd>
              <c:set var="emailClean"  value="${fn:replace(fn:trim(member.email),  ' ', '')}" />
              <c:set var="domainClean" value="${fn:replace(fn:trim(member.domain), ' ', '')}" />
              <c:choose>
                <c:when test="${fn:contains(emailClean,'@')}">
                  <c:out value="${emailClean}" />
                </c:when>
                <c:when test="${not empty domainClean}">
                  <c:out value="${emailClean}" />@<c:out value="${domainClean}" />
                </c:when>
                <c:otherwise>
                  <c:out value="${emailClean}" />
                </c:otherwise>
              </c:choose>
            </dd>

            <dt>전화번호</dt>
            <dd><c:out value="${member.phone}" /></dd>

            <dt>닉네임</dt>
            <dd><c:out value="${member.nickname}" /></dd>

            <dt>주소</dt>
            <dd><c:out value="${member.address}" /></dd>

            <dt>상세 주소</dt>
            <dd><c:out value="${member.detailaddress}" /></dd>
          </dl>

          <!-- 액션 -->
          <div class="actions">
            <!-- ✅ 관리자만 보이는 버튼 (정보 수정 왼쪽) -->
            <sec:authorize access="hasRole('ROLE_ADMIN')">
              <a href="http://localhost:5173/" class="btn btn-admin">관리자 페이지</a>
            </sec:authorize>

            <form method="post" action="${ctx}/myPageEdit.do" style="display:inline">
              <input type="hidden" name="user_id" value="<c:out value='${member.user_id}'/>"/>
              <c:if test="${not empty _csrf}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              </c:if>
              <button type="submit" class="btn btn-primary">정보 수정</button>
            </form>
          </div>

        </div>
      </main>

    </div>
  </section>

  <script defer src="${ctx}/js/main.js"></script>
</body>
</html>
