<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>거래게시판 - 상세보기</title>
  <link rel="stylesheet" href="<c:url value='/css/transactionView.css'/>?v=5" />
  <c:set var="ctx" value="${pageContext.request.contextPath}" />
</head>
<body class="txViewPage">

  <%@ include file="../include/nav.jsp" %>

  <!-- 로그인 사용자 아이디 확보 -->
  <sec:authorize access="isAuthenticated()">
    <sec:authentication property="name" var="loginId"/>
  </sec:authorize>

  <!-- 공통 루트 -->
  <div id="txView"
       class="view-container"
       data-ctx="${ctx}"
       data-login-id="${fn:escapeXml(loginId)}"
       data-writer-id="${fn:escapeXml(transactionDTO.writer)}"
       data-images='${fn:escapeXml(imageJson)}'>

    <!-- 2열: 왼쪽(이미지) / 오른쪽(텍스트) -->
    <div class="view-main">
      <!-- 왼쪽: 메인 이미지 + 썸네일 -->
      <div class="view-left">
        <div class="view-image">
          <button type="button" id="btnPrevImage" class="nav-btn nav-prev" aria-label="이전 이미지">‹</button>

          <c:choose>
            <c:when test="${not empty imageList}">
              <img id="img01"
                   src="${ctx}/uploads/${fn:escapeXml(imageList[0])}"
                   alt="상품 이미지"/>
              <div class="no-image" id="noImage" style="display:none;">이미지 없음</div>
            </c:when>
            <c:otherwise>
              <img id="img01" src="" alt="상품 이미지" style="display:none;"/>
              <div class="no-image" id="noImage">이미지 없음</div>
            </c:otherwise>
          </c:choose>

          <button type="button" id="btnNextImage" class="nav-btn nav-next" aria-label="다음 이미지">›</button>
        </div>

        <!-- 썸네일 -->
        <div id="thumbs" class="thumbs"></div>
      </div>

      <!-- 오른쪽: 제목 → (작성자·등록일) → (가격·상태) → 내용 -->
      <div class="view-right">
        <!-- 제목 -->
        <h1 class="title">
          <c:out value="${transactionDTO.transaction_title}" default="제목 없음"/>
        </h1>

        <!-- 작성자·등록일 한 줄 -->
        <div class="meta-row">
          <span class="meta-item">작성자: <strong><c:out value="${transactionDTO.writer}" default="알수없음"/></strong></span>
          <span class="dot">·</span>
          <span class="meta-item">등록일: <c:out value="${transactionDTO.transaction_date}" default="-"/></span>
        </div>

        <!-- 가격·상태 한 줄 -->
        <div class="price-row">
          <div class="price">
            <c:choose>
              <c:when test="${transactionDTO.transaction_price > 0}">
                <strong><c:out value="${transactionDTO.transaction_price}"/></strong> 원
              </c:when>
              <c:otherwise>가격 알수없음</c:otherwise>
            </c:choose>
          </div>

          <span class="status-badge ${transactionDTO.transaction_istrans eq 'y' ? 'sold' : (transactionDTO.transaction_istrans eq 'n' ? 'selling' : 'unknown')}">
            <c:choose>
              <c:when test="${transactionDTO.transaction_istrans eq 'n'}">판매중</c:when>
              <c:when test="${transactionDTO.transaction_istrans eq 'y'}">판매완료</c:when>
              <c:otherwise>알수없음</c:otherwise>
            </c:choose>
          </span>
        </div>

        <!-- 내용 -->
        <div class="content">
          <c:out value="${transactionDTO.transaction_content}" default="내용 없음" escapeXml="false"/>
        </div>
      </div>
    </div>

    <!-- 하단 버튼 -->
    <div class="view-buttons">
      <a href="${ctx}/transactionList.do" class="btn-back">목록</a>

      <!-- ✅ 수정/삭제: ADMIN 이면 항상 표시, 일반 사용자는 작성자일 때만 -->
      <sec:authorize access="hasRole('ADMIN') or (isAuthenticated() and authentication.name == #transactionDTO.writer)">
        <a href="#" class="btn-edit"
           onclick="return transactionEdit('<c:out value="${transactionDTO.transaction_idx}"/>')">수정</a>

        <a href="#" class="btn-delete"
           onclick="return transaction_deletePost(
             '<c:out value="${transactionDTO.transaction_idx}"/>',
             '<c:out value="${transactionDTO.sfile1}"/>',
             '<c:out value="${transactionDTO.sfile2}"/>',
             '<c:out value="${transactionDTO.sfile3}"/>'
           )">삭제</a>
      </sec:authorize>

      <!-- ✅ 채팅하기: ADMIN 은 항상 표시, 일반 사용자는 작성자가 아닐 때만 -->
      <sec:authorize access="hasRole('ADMIN') or (isAuthenticated() and authentication.name != #transactionDTO.writer)">
        <button id="chatOpenBtn"
                class="btn btn-chat"
                data-writer-userid="${fn:escapeXml(transactionDTO.writer)}"
                data-login-userid="${fn:escapeXml(loginId)}">
          채팅하기
        </button>
      </sec:authorize>
    </div>
  </div>

  <!-- 삭제용 히든 폼 -->
  <form id="transactionDeleteFrm" name="transactionDeleteFrm" method="post" style="display:none;">
    <input type="hidden" name="transaction_idx">
    <input type="hidden" name="sfile1">
    <input type="hidden" name="sfile2">
    <input type="hidden" name="sfile3">
  </form>

  <!-- 외부 스크립트 -->
  <script src="${ctx}/js/transactionView.js?v=2"></script>
</body>
</html>
