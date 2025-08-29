<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>거래게시판 글 수정</title>
  <c:set var="ctx" value="${pageContext.request.contextPath}" />
  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/transactionEdit.css" />
  <!-- 필요시 defer 권장 -->
  <script defer src="${ctx}/js/transactionEdit.js"></script>
</head>
<body>

  <%@ include file="../include/nav.jsp" %>

  <section class="board-layout section" style="padding-top:2rem;">
    <div class="board-container">

      <!-- 메인 -->
      <main class="board-main">
        <div class="transaction-form">
          <div class="form-container">
            <h2>글 수정하기</h2>

            <!-- 수정/삭제 모두 이 한 폼에서 처리 -->
            <form action="${ctx}/transactionEdit.do" method="post" enctype="multipart/form-data">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
              <input type="hidden" name="transaction_idx" value="${transactionDTO.transaction_idx}" />

              <!-- 제목 -->
              <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="transaction_title"
                       value="${transactionDTO.transaction_title}" required />
              </div>

              <!-- 가격 -->
              <div class="form-group">
                <label for="price">가격</label>
                <input type="number" id="price" name="transaction_price"
                       value="${transactionDTO.transaction_price}" min="0" step="1" required />
              </div>

              <!-- 상태 -->
              <div class="form-group">
                <label for="status">상태</label>
                <select id="status" name="transaction_istrans">
                  <option value="N" ${transactionDTO.transaction_istrans eq 'N' ? 'selected' : ''}>판매중</option>
                  <option value="Y" ${transactionDTO.transaction_istrans eq 'Y' ? 'selected' : ''}>거래완료</option>
                </select>
              </div>

              <!-- 내용 -->
              <div class="form-group">
                <label for="content">내용</label>
                <!-- required 뒤의 불필요한 쌍따옴표 제거 -->
                <textarea id="content" name="transaction_content" rows="8" required>${transactionDTO.transaction_content}</textarea>
              </div>

              <!-- 첨부파일 (1~3) : 미리보기 + 개별 삭제 버튼 + 새 파일 업로드 -->
              <c:forEach var="i" begin="1" end="3">
                <c:set var="sKey" value="${'sfile'}${i}" />
                <c:set var="oKey" value="${'ofile'}${i}" />

                <div class="form-group">
                  <label>첨부파일 ${i}</label>

                  <c:choose>
                    <c:when test="${not empty transactionDTO[sKey]}">
                      <div class="file-preview" style="display:flex;gap:8px;align-items:center;">
                        현재 파일:
                        <a href="${ctx}/transactionDownload.do
                                 ?transaction_idx=${transactionDTO.transaction_idx}
                                 &ofile1=${transactionDTO[oKey]}
                                 &sfile1=${transactionDTO[sKey]}"
                           target="_blank">
                          ${transactionDTO[oKey]}
                        </a>

                        <!-- ✅ 개별 삭제: 같은 폼에서 별도 액션으로 전송 -->
                        <button type="submit"
                                class="btn-small"
                                formaction="${ctx}/transaction_DeleteFileOne.do?sfile=${transactionDTO[sKey]}&imgCount=${i}"
                                formmethod="post"
                                formnovalidate>
                          삭제
                        </button>
                      </div>
                    </c:when>
                    <c:otherwise>
                      <div class="file-preview">현재 파일 없음</div>
                    </c:otherwise>
                  </c:choose>

                  <!-- 새 파일 업로드 (컨트롤러는 name="ofile" 로만 읽음) -->
                  <input type="file" name="ofile" />
                </div>
              </c:forEach>

              <!-- ✅ 전체 파일 삭제: 같은 폼에서 별도 액션으로 전송 -->
              <div class="form-group">
                <button type="submit"
                        class="btn-outline"
                        formaction="${ctx}/transaction_DeleteFileAll.do"
                        formmethod="post"
                        formnovalidate>
                  첨부 전체 삭제
                </button>
              </div>

              <!-- 버튼 -->
              <div class="form-actions">
                <button type="submit" class="btn-submit">수정 완료</button>
                <a href="${ctx}/transactionView.do?transaction_idx=${transactionDTO.transaction_idx}" class="btn-cancel">취소</a>
              </div>

            </form>
          </div>
        </div>
      </main>

    </div>
  </section>

</body>
</html>
