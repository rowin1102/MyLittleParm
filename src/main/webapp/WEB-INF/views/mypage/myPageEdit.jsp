<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>회원 정보 수정</title>

  <c:set var="ctx" value="${pageContext.request.contextPath}" />

  <!-- CSRF -->
  <c:if test="${not empty _csrf}">
    <meta name="_csrf" content="${_csrf.token}" />
    <meta name="_csrf_header" content="${_csrf.headerName}" />
  </c:if>

  <!-- 닉네임/이메일 인증 API URL (컨트롤러 수정 없이 사용) -->
  <meta name="nickname-check-url"      content="<c:url value='/mypage/nickname/check'/>" />
  <meta name="email-verify-send-url"   content="<c:url value='/api/auth/email/send'/>" />
  <meta name="email-verify-status-url" content="<c:url value='/api/auth/email/status'/>" />

  <link rel="stylesheet" href="<c:url value='/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/board.css'/>" />
  <link rel="stylesheet" href="<c:url value='/css/myPageEdit.css'/>" />

  <script defer src="<c:url value='/js/myPageEdit.js'/>"></script>
</head>
<body>
  <%@ include file="../include/nav.jsp" %>
  <div class="page-spacer"></div>

  <section class="account section">
    <div class="account-layout">
      <aside class="board-sidebar">
        <nav class="sidebar-card">
          <h3 class="sidebar-title">마리팜</h3>
          <ul class="sidebar-menu">
            <li><a href="<c:url value='/calendar.do'/>">캘린더</a></li>
            <li class="active"><a href="<c:url value='/myPage.do'/>">내 정보</a></li>
            <li><a href="<c:url value='/myPageBoardList.do'/>">자유게시판(내가 작성한 글)</a></li>
            <li><a href="<c:url value='/myPageTransactionList.do'/>">거래게시판(내가 작성한 글)</a></li>
            <li><a href="<c:url value='/myPageBoardCommentList.do'/>">내가 작성한 댓글</a></li>
			<li><a href="${ctx}/myPageMemberDelete.do">회원 탈퇴</a></li>            
          </ul>
        </nav>
      </aside>

      <main class="account-content">
        <div class="container">
          <h2>회원 정보 수정</h2>

          <form name="myPageFrm"
                method="post"
                action="<c:url value='/myPageUpdate.do'/>"
                onsubmit="return (sanitizeBeforeSubmit(this) && validateForm(this));">

            <table class="account-edit-table">
              <tr>
                <th>아이디</th>
                <td><input type="text" name="user_id" value="${member.user_id}" readonly /></td>
              </tr>

              <!-- 이메일: 로컬파트 + 도메인 + 셀렉트 + 인증버튼 -->
              <tr>
                <th>이메일</th>
                <td>
                  <div class="email-row">
                    <!-- 로컬파트 -->
                    <input type="text"
                           name="email"
                           id="emailInput"
                           class="form-input w-40"
                           value="${member.email}"
                           data-original="${member.email}" />

                    <span class="at-symbol">@</span>

                    <!-- 도메인 입력 -->
                    <input type="text"
                           name="domain"
                           id="domainInput"
                           class="form-input w-30"
                           value="${member.domain}"
                           data-original="${member.domain}" />

                    <!-- 도메인 선택 -->
                    <select name="domain_select" id="domainSelect" class="form-input w-30">
                      <option value="">직접입력</option>
                      <option value="naver.com"   ${member.domain eq 'naver.com'   ? 'selected' : ''}>naver.com</option>
                      <option value="gmail.com"   ${member.domain eq 'gmail.com'   ? 'selected' : ''}>gmail.com</option>
                      <option value="daum.net"    ${member.domain eq 'daum.net'    ? 'selected' : ''}>daum.net</option>
                      <option value="hanmail.net" ${member.domain eq 'hanmail.net' ? 'selected' : ''}>hanmail.net</option>
                    </select>

                    <!-- 이메일 인증 버튼 (초기엔 동일이므로 JS가 비활성화로 만들 것) -->
                    <button type="button" class="check-btn" id="btnSendEmailVerify">이메일 인증하기</button>
                  </div>

                  <div id="emailVerifyMsg" class="validation-message"></div>
                  <input type="hidden" id="emailVerifiedFlag" name="email_verified_pre" value="N">
                </td>
              </tr>

              <tr>
                <th>전화번호</th>
                <td><input type="tel" name="phone" value="${member.phone}" /></td>
              </tr>

              <tr>
                <th>닉네임</th>
                <td>
                  <div class="inline">
                    <input type="text"
                           name="nickname"
                           id="nickname"
                           value="${member.nickname}"
                           data-original="${member.nickname}" />
                    <button type="button" class="btn-sm" id="btnCheckNickname" disabled>중복확인</button>
                  </div>
                  <div id="nicknameMessage" class="validation-message" aria-live="polite"></div>
                </td>
              </tr>
            </table>

            <c:if test="${not empty _csrf}">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
            </c:if>

            <div class="btn-box">
              <button type="submit" class="btn btn-primary">수정 완료</button>
              <a class="btn btn-ghost" href="<c:url value='/myPage.do'/>">취소</a>
            </div>
          </form>
        </div>
      </main>
    </div>
  </section>

  <script defer src="<c:url value='/js/main.js'/>"></script>
</body>
</html>
