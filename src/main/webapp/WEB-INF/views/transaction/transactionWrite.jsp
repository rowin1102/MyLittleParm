<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%
String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<title>거래게시판 글쓰기</title>
<script src="<%=ctx%>/js/transactionWrite.js"></script>
<link rel="stylesheet" href="<%=ctx%>/css/main.css" />
<link rel="stylesheet" href="<%=ctx%>/css/transactionWrite.css" />
<script src="<%=ctx%>/js/main.js"></script>

</head>

<body>
	<%@ include file="../include/nav.jsp"%>

	<section class="board-layout section" style="padding-top: 2rem;">
		<div class="form-wrapper">

			<!-- 사이드바 -->
			<aside class="form-sidebar">
				<div class="sidebar-card">
					<h3>커뮤니티</h3>
					<a href="<%=ctx%>/transactionList.do" class="btn-sidebar">거래게시판</a>
				</div>
			</aside>

			<!-- 글쓰기 폼 -->
			<main class="form-main">
				<div class="form-card">
					<h2>글쓰기</h2>
					<form action="<%=ctx%>/transactionWrite.do" method="post"
						enctype="multipart/form-data">

						<div class="form-group">
							<label for="transaction_title">제목</label> <input type="text"
								id="transaction_title" name="transaction_title"
								placeholder="제목을 입력해주세요" required />
						</div>

						<div class="form-group">
							<label for="writer">작성자</label> <input id="writer" name="writer"
								type="text" value="<c:out value='${userId}'/>" readonly />
						</div>

						<div class="form-group">
							<label for="status">거래 상태</label> <select id="status"
								name="status">
								<option value="sale">판매중</option>
								<option value="sold">거래완료</option>
							</select>
						</div>

						<div class="form-group">
							<label for="transaction_price">가격 (₩)</label> <input
								type="number" id="transaction_price" name="transaction_price"
								placeholder="가격을 입력해주세요" required />
						</div>

						<div class="form-group">
							<label for="transaction_content">내용</label>
							<textarea id="transaction_content" name="transaction_content"
								placeholder="내용을 입력해주세요" rows="7" required></textarea>
						</div>

						<div class="form-group">
							<label>이미지 첨부 (최대 3장)</label> <input type="file" name="ofile" />
							<input type="file" name="ofile" /> <input type="file"
								name="ofile" />
						</div>

						<div class="form-actions">
							<button type="submit" class="btn-green">작성 완료</button>
							<button type="reset" class="btn-white">RESET</button>
							<a href="<%=ctx%>/transactionList.do" class="btn-white">목록</a>
						</div>

					</form>
				</div>
			</main>
		</div>
	</section>
	<script>
		const toggle = document.getElementById("darkModeToggle");
		toggle.addEventListener("click",
				function() {
					document.body.classList.toggle("dark-mode");
					toggle.textContent = document.body.classList
							.contains("dark-mode") ? "☀️ 라이트모드" : "🌙 다크모드";
				});
	</script>

</body>
</html>
