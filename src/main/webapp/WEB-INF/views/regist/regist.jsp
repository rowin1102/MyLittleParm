<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>회원가입</title>

<meta name="_csrf" content="${_csrf.token}">
<meta name="_csrf_header" content="${_csrf.headerName}">
<!-- 로그인 이동 경로 (변경 시 여기만 수정) -->
<meta name="login-url" content="<c:url value='/myLogin.do'/>" />

<link rel="stylesheet" href="/css/regist.css">

<!-- Daum 우편번호 -->
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js" defer></script>
<!-- 회원가입 스크립트 -->
<script src="/js/regist.js" defer></script>
</head>
<body>
  <c:set var="ctx" value="${pageContext.request.contextPath}" />

  <!-- 상단 중앙 로고 -->
  <div class="brand-hero">
    <a href="${ctx}/" class="brand-link" title="홈으로">
      <img src="${ctx}/img/logo.png" alt="홈으로" class="brand-logo">
    </a>
  </div>

  <div class="signup-container">
    <h2>회원가입</h2>

    <!-- onsubmit 제거: JS에서 검증 + 제출 -->
    <form id="registForm" action="regist.do" method="post" novalidate>
      <table class="form-table">
        <tr>
          <td>아이디</td>
          <td>
            <div class="input-container">
              <input type="text" name="user_id" class="form-input input-field"
                     required placeholder="아이디를 입력하세요" autocomplete="username">
              <button type="button" class="check-btn"
                      onclick="checkIdDuplicate(this)">중복확인</button>
            </div>
            <div id="idMessage" class="validation-message"></div>
          </td>
        </tr>

        <tr>
          <td>비밀번호</td>
          <td>
            <input type="password" name="password" class="form-input"
                   required placeholder="비밀번호를 입력하세요" autocomplete="new-password">
          </td>
        </tr>

        <tr>
          <td>비밀번호 확인</td>
          <td>
            <input type="password" name="password_confirm" class="form-input"
                   required placeholder="비밀번호를 다시 입력하세요"
                   onblur="checkPasswordMatch()" autocomplete="new-password">
            <div id="passwordMessage" class="validation-message"></div>
          </td>
        </tr>

        <tr>
          <td>닉네임</td>
          <td>
            <div class="input-container">
              <input type="text" name="nickname" class="form-input input-field"
                     placeholder="닉네임을 입력하세요" autocomplete="nickname">
              <button type="button" class="check-btn"
                      onclick="checkNicknameDuplicate(this)">중복확인</button>
            </div>
            <div id="nicknameMessage" class="validation-message"></div>
          </td>
        </tr>

        <tr>
          <td>이메일</td>
          <td>
            <div class="email-row">
              <input type="text" name="email" class="form-input w-40" required
                     placeholder="이메일" autocomplete="email">
              <span class="at-symbol">@</span>
              <input type="text" name="domain" class="form-input w-30"
                     placeholder="직접입력" id="domainInput">
              <select name="domain_select" class="form-input w-30"
                      onchange="handleDomainSelect(this.value)">
                <option value="">직접입력</option>
                <option value="naver.com">naver.com</option>
                <option value="gmail.com">gmail.com</option>
                <option value="daum.net">daum.net</option>
                <option value="hanmail.net">hanmail.net</option>
              </select>
              <button type="button" class="check-btn" id="btnSendEmailVerify">
                이메일 인증하기
              </button>
            </div>
            <div id="emailVerifyMsg" class="validation-message"></div>
            <input type="hidden" id="emailVerifiedFlag" name="email_verified_pre" value="N">
          </td>
        </tr>

        <tr>
          <td>전화번호</td>
          <td>
            <input type="tel" name="phone" class="form-input"
                   placeholder="010-1234-5678" onblur="validatePhoneFormat()"
                   inputmode="numeric" maxlength="13" autocomplete="tel"
                   pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}">
            <div id="phoneMessage" class="validation-message"></div>
          </td>
        </tr>

        <tr>
          <td>주소</td>
          <td>
            <div class="input-container">
              <input type="text" id="postcode" name="postcode"
                     class="form-input input-field w-50" placeholder="우편번호" readonly
                     autocomplete="postal-code">
              <button type="button" class="check-btn"
                      onclick="execDaumPostcode()">우편번호 찾기</button>
            </div>

            <input type="text" id="address" name="address"
                   class="form-input full-width mt-8" placeholder="도로명 주소" readonly
                   autocomplete="address-line1">

            <div class="address-row mt-8">
              <input type="text" id="detailaddress" name="detailaddress"
                     class="form-input half" placeholder="상세주소"
                     autocomplete="address-line2">
            </div>
          </td>
        </tr>

        <tr>
          <td colspan="2">
            <input type="submit" value="회원가입" class="submit-btn">
          </td>
        </tr>
      </table>
    </form>
  </div>
</body>
</html>
