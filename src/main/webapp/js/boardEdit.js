function deleteFileOne(boardIdx, sfile, index) {
	if (confirm('이 파일을 삭제하시겠습니까?')) {
		const form = document.forms['writeFrmFile'];
		form.board_idx.value = boardIdx;
		form.sfile.value = sfile;
		form.imgCount.value = index;
		form.submit();
	}
}

function deleteFileAll(boardIdx, imgCount) {
	if (confirm('모든 파일을 삭제하시겠습니까?')) {
		const form = document.forms['deleteFrmFileAll'];
		form.board_idx.value = boardIdx;
		form.submit();
	}
}

function checkFileLimit(currentCount) {
	const fileInput = document.getElementById("ofile");
	const limit = 3 - parseInt(currentCount || "0");
	if (fileInput.files.length > limit) {
		alert(`파일은 최대 ${limit}개까지 추가할 수 있습니다.`);
		fileInput.value = "";
	}
}
