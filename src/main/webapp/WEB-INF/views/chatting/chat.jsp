<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title><c:out value="${pageTitle != null ? pageTitle : '채팅'}" /></title>

	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />
  <c:set var="ctx" value="${pageContext.request.contextPath}" />

  <link rel="stylesheet" href="${ctx}/css/chat.css" />
</head>
<body>

	<%@ include file="../include/nav.jsp" %>
	
	<div class="page-wrap">
	  <div class="chat-container">
	    <div class="chat-shell">
	
	      <aside class="left">
	        <div class="search">
	          <input id="roomSearch" class="input" placeholder="대화 검색">
	        </div>
	        <div id="threadList" class="threads"><!-- JS로 채움 --></div>
	      </aside>
	
	      <section class="center">
	        <c:choose>
	          <c:when test="${empty param.roomId}">
	            <div class="conv-header">
	              <strong id="convTitle">채팅방 선택</strong>
	            </div>
	            <div class="empty-room">
	              <h3>왼쪽 목록에서 채팅방을 선택하거나, 거래게시판에서 <b>채팅하기</b>를 눌러 방을 생성하세요.</h3>
	              <a class="btn btn-ghost" href="${ctx}/transactionList.do">거래게시판으로 가기</a>
	            </div>
	          </c:when>
	
	          <c:otherwise>
	            <div class="conv-header">
	              <%-- 여기서 HTML 주석 쓰지 말고 JSP 주석 사용 --%>
	              <div>
	                <strong id="convTitle">
	                  채팅방 #
	                  <c:out value="${param.roomName != null ? param.roomName : roomName}" />
	                </strong>
	                <span class="meta" id="convSub"></span>
	              </div>
	              <button id="roomDeleteBtn" class="btn">채팅방 삭제</button>
	            </div>
	
	            <div id="messages" class="messages"><!-- JS가 초기 메시지 로딩 --></div>

					      <div class="composer">
					        <div class="composer-grid">
					          <div class="idbox">
					            <input id="sender" class="input"
					                   value="<c:out value='${myMemberIdx}'/>"
					                   placeholder="ID">
					          </div>
					          <div>
					            <input id="content" class="input" placeholder="메시지를 입력하세요" autocomplete="off">
					          </div>
					          <!-- ✅ 진짜 보내기 버튼 -->
					          <button id="sendBtn" class="btn btn-send">보내기</button>

	                <button id="meetingBtn"
							        class="btn btn-appoint"
							        data-kakao-key="${kakaoJsKey}"
							        data-lat="${lat}" data-lng="${lng}"
											data-addr="${address}">
							  	약속잡기
								</button>
	              </div>
	            </div>
	          </c:otherwise>
	        </c:choose>
	      </section>
	
	    </div>
	  </div>
	</div>
	
	<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>



  <!-- Kakao Map Modal -->
			<div id="kmapModal" class="kmap-modal" aria-hidden="true">
			  <div class="kmap-backdrop"></div>
			  <div class="kmap-dialog" role="dialog" aria-modal="true" aria-labelledby="kmapTitle">
			    <div class="kmap-header">
			      <strong id="kmapTitle">약속 장소 선택</strong>
			      <button type='button' class="kmap-close" aria-label="닫기">&times;</button>
			    </div>
			
			    <div class="kmap-body">
			      <div id="kmap" class="kmap-canvas"></div>
			    </div>
			
			    <div class="kmap-footer">
			      <input id="kmapAddress" class="input" placeholder="지도를 클릭해서 주소 선택" readonly>
			      <div class="kmap-actions">
        <button id="kmapConfirm" class="btn btn-send">확인</button>
      </div>
    </div>
  </div>
</div>
   <!-- 실제 채팅 로직 -->
  <script>
    // 컨텍스트를 JS 전역으로 넘기면 chat.js에서 재사용 가능
    window.APP_CTX = '${ctx}';
  </script>
  <script src="${ctx}/js/chat.js"></script>
</body>
</html>
