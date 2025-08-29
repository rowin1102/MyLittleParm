/**
 *
 */

function validateForm(form){
	var f = form;

	//transaction_title transaction_content transaction_price

	if( f.transaction_title.value == "" ){
		alert("제목을 입력해 주세요");
		f.transaction_title.focus();
		return false;
	}

	if( f.transaction_content.value == "" ){
		alert("내용을 입력해 주세요");
		f.transaction_content.focus();
		return false;
	}

	if( f.transaction_price.value == "" ){
		alert("가격을 입력해 주세요");
		f.transaction_price.focus();
		return false;
	}
}

/*
<input type="file" name="ofile" id="ofile" multiple onchange="checkFileLimit()" />
*/

function checkFileLimit() {
    const fileInput = document.getElementById("ofile");
    const files = fileInput.files;

    // 이미지 파일인지 검사
    for (let i = 0; i < files.length; i++) {
        if (!files[i].type.startsWith("image/")) {
            alert("이미지 파일만 업로드할 수 있습니다.");
            fileInput.value = ""; // 파일 선택 초기화
            return;
        }
    }

    if (files.length > 3) {
        alert("이미지는 최대 3장까지만 업로드할 수 있습니다.");
        fileInput.value = ""; // 선택 초기화

    }

}