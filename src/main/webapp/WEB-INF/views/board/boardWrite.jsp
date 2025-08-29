<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>글쓰기</title>
  <link rel="stylesheet" href="${ctx}/css/main.css" />
  <link rel="stylesheet" href="${ctx}/css/board.css" />
  <script src="${ctx}/js/boardWrite.js"></script>
</head>
<body>

<%@ include file="/WEB-INF/views/include/nav.jsp" %>

<section class="board-layout section" style="padding-top:2rem;">
  <div class="board-container">

    <!-- 사이드바 -->
    <aside class="board-sidebar">
      <nav class="sidebar-card">
        <h3 class="sidebar-title">커뮤니티</h3>
        <ul class="sidebar-menu">
          <!-- 수정 ①: /boardlist.do -> /boardList.do -->
          <li class="active"><a href="${ctx}/boardList.do">자유게시판</a></li>
        </ul>
      </nav>
    </aside>

    <!-- 메인 콘텐츠 -->
    <main class="board-content">
      <div class="board-card">
        <div class="header-row">
          <h2>글쓰기</h2>
          <!-- 수정 ②: /board/list -> /boardList.do -->
          <a class="btn ghost" href="${ctx}/boardList.do">목록</a>
        </div>

        <!-- 수정 ③: action="./boardWrite.do" -> action="${ctx}/boardWrite.do" -->
        <form name="writeFrm" method="post" action="${ctx}/boardWrite.do"
              onsubmit="return validateForm(this);" enctype="multipart/form-data"
              class="board-form" autocomplete="off">

          <div class="form-row">
            <label for="board_title">제목</label>
            <input type="text" id="board_title" name="board_title" required maxlength="200"
                   placeholder="제목을 입력해주세요" />
          </div>

          <div class="form-row">
            <label for="board_content">내용</label>
            <textarea id="board_content" name="board_content" rows="10" required maxlength="2000"
                      placeholder="내용을 입력해주세요"></textarea>
          </div>

          <div class="form-row">
            <label for="ofile">이미지 첨부 (최대 3장)</label>
            <input type="file" id="ofile" name="ofile" accept="image/*" multiple onchange="checkFileLimit()" />
          </div>

          <div class="form-actions">
            <button type="submit" class="btn primary">작성 완료</button>
            <button type="reset" class="btn ghost">RESET</button>
            <button type="button" class="btn ghost"
                    onclick="location.href='${ctx}/boardList.do';">목록</button>
          </div>
        </form>
      </div>
    </main>

  </div>
</section>
<script src="${ctx}/js/main.js"></script>
</body>
</html>
