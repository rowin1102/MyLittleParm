<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <title>게시글 수정</title>
  <link rel="stylesheet" href="${ctx}/css/main.css"/>
  <link rel="stylesheet" href="${ctx}/css/board.css"/>
</head>
<body>

<%@ include file="/WEB-INF/views/include/nav.jsp" %>

<section class="board-layout section" style="padding-top:2rem;">
  <div class="board-container">

    <aside class="board-sidebar">
      <nav class="sidebar-card">
        <h3 class="sidebar-title">커뮤니티</h3>
        <ul class="sidebar-menu">
          <li class="active"><a href="${ctx}/boardList.do">자유게시판</a></li>
        </ul>
      </nav>
    </aside>

    <main class="board-content">
      <div class="board-card">
        <div class="header-row">
          <h2>게시글 수정</h2>
          <div style="display:flex; gap:8px;">
            <a class="btn ghost" href="${ctx}/boardView.do?board_idx=${boardDTO.board_idx}">취소</a>
            <a class="btn danger" href="${ctx}/boardDelete.do?board_idx=${boardDTO.board_idx}"
               onclick="return confirm('삭제하시겠습니까?');">삭제</a>
          </div>
        </div>

        <form method="post" action="${ctx}/boardEdit.do"
              class="board-form" enctype="multipart/form-data" autocomplete="off">
          <input type="hidden" name="board_idx" value="${boardDTO.board_idx}" />
          <input type="hidden" name="imgCount" value="${imgCount}" />

          <div class="form-row">
            <label for="board_title">제목</label>
            <input type="text" id="board_title" name="board_title"
                   value="${boardDTO.board_title}" maxlength="200" required />
          </div>

          <div class="form-row">
            <label for="writer">작성자</label>
            <input type="text" id="writer" name="writer"
                   value="${boardDTO.writer}" readonly />
          </div>

          <div class="form-row">
            <label for="board_content">내용</label>
            <textarea id="board_content" name="board_content" rows="10"
                      maxlength="2000" required>${boardDTO.board_content}</textarea>
          </div>

          <!-- 기존 첨부파일 미리보기 -->
          <c:forEach var="i" begin="1" end="3">
            <c:set var="ofile" value="${fileMap['ofile' + i]}" />
            <c:set var="sfile" value="${fileMap['sfile' + i]}" />
            <c:set var="cate" value="${fileMap['cate' + i]}" />

            <c:if test="${not empty ofile}">
              <div class="form-row">
                <label>기존 첨부파일 ${i}</label>

                <c:choose>
                  <c:when test="${cate eq 'img'}">
                    <img src="${ctx}/uploads/${sfile}" width="100" style="border-radius:8px; box-shadow:0 4px 8px rgba(0,0,0,0.1);" /><br/>
                  </c:when>
                  <c:when test="${cate eq 'video'}">
                    <video width="320" height="240" controls style="border-radius:8px;">
                      <source src="${ctx}/uploads/${sfile}" type="video/mp4" />
                    </video><br/>
                  </c:when>
                  <c:when test="${cate eq 'audio'}">
                    <audio controls style="width: 320px;">
                      <source src="${ctx}/uploads/${sfile}" type="audio/mpeg" />
                    </audio><br/>
                  </c:when>
                </c:choose>

                <a href="${ctx}/boardDownload.do?ofile=${ofile}&sfile=${sfile}&idx=${boardDTO.board_idx}"
                   class="file-link">
                  ${ofile}
                </a>

                <button type="button" class="btn danger"
                        onclick="deleteFileOne('${boardDTO.board_idx}', '${sfile}', '${i}')">삭제</button>
              </div>
            </c:if>
          </c:forEach>

          <!-- 새 파일 추가 -->
          <div class="form-row">
            <label for="ofile">파일 추가 (최대 3개)</label>
            <input type="file" id="ofile" name="ofile" multiple
                   onchange="checkFileLimit('${imgCount}')" />
          </div>

          <div class="form-actions">
            <button type="submit" class="btn primary">수정 완료</button>
            <a href="${ctx}/boardList.do" class="btn ghost">목록</a>
            <button type="button" class="btn danger"
                    onclick="deleteFileAll('${boardDTO.board_idx}','${imgCount}')">전체 파일 삭제</button>
          </div>
        </form>
      </div>
    </main>

  </div>
</section>

<script src="${ctx}/js/boardEdit.js"></script>
<script src="${ctx}/js/main.js"></script>
<!-- 개별 파일 삭제용 숨은 폼 -->
<form name="writeFrmFile" method="post" action="${ctx}/boardDelete.do" style="display:none;">
  <input type="hidden" name="board_idx" />
  <input type="hidden" name="sfile" />
  <input type="hidden" name="imgCount" />
</form>

<!-- 전체 파일 삭제용 숨은 폼 -->
<form name="deleteFrmFileAll" method="post" action="${ctx}/boardDeleteFileAll.do" style="display:none;">
  <input type="hidden" name="board_idx" />
</form>

</body>
</html>
