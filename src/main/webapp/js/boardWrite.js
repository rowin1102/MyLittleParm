function validateForm(form) {
	const title = form.board_title.value.trim();
	const content = form.board_content.value.trim();
	const writer = form.writer.value.trim();

	if (!title) {
		alert("제목을 입력해주세요.");
		form.board_title.focus();
		return false;
	}
	if (!writer) {
		alert("작성자를 입력해주세요.");
		form.writer.focus();
		return false;
	}
	if (!content) {
		alert("내용을 입력해주세요.");
		form.board_content.focus();
		return false;
	}
	return true;
}

function checkFileLimit() {
	const fileInput = document.getElementById("ofile");
	const maxFiles = 3;
	if (fileInput.files.length > maxFiles) {
		alert("이미지는 최대 3장까지만 업로드할 수 있습니다.");
		fileInput.value = "";
	}
}
