/**
 *
 */
function validateForm(form){
    var f = form;

    if( f.transaction_title.value.trim() === "" ){
        alert("제목을 입력해 주세요");
        f.transaction_title.focus();
        return false;
    }

    if( f.transaction_content.value.trim() === "" ){
        alert("내용을 입력해 주세요");
        f.transaction_content.focus();  // 오타: transaction_contents → transaction_content
        return false;
    }

    if( f.transaction_price.value.trim() === "" ){
        alert("가격을 입력해 주세요"); // 메시지 수정
        f.transaction_price.focus();
        return false;
    }

    // confirm 결과에 따라 전송 여부 결정
    return confirm("수정하시겠습니까?");
}

//파일 하나 삭제
function transaction_deleteFileOne(transaction_idx, sfile, imgCount) {

	//alert("파일삭제 : " + transaction_idx + " : " + sfile + " : " + imgCount );

	var f = document.editFrmFile;
	//f.action="/deleteFileOne.do";
	//f.method="post";
	f.transaction_idx.value=transaction_idx;
	f.sfile.value=sfile;
	f.imgCount.value=imgCount;

	if( confirm("파일을 삭제하시겠습니까?") ){
		f.submit();
	}

}


//전체 파일 삭제
function transaction_deleteFileAll(idx, num){
	//alert("idx  = " + idx);
	var f = document.deleteFrmFileAll;
	//f.action="/deleteFileOne.do";
	//f.method="post";
	f.transaction_idx.value=idx;

	if( num == 0 ){
		alert("삭제할 파일이 없습니다.");
		return false;
	}

	if( confirm("전체 파일을 삭제하시겠습니까?") ){
		f.submit();
	}

}



//이미지 체크
function checkFileLimit(num) {
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

    if( num == 3 ){
		alert("업로드는 3개 까지만 됩니다. 기존 파일 삭제후에 업로드 할수 있습니다.");
		fileInput.value = ""; // 선택 초기화
    }

    if( num == 2 ){

    	if (files.length > 1) {
            alert("이미지는 최대 3개이고 1장더 업로드할 수 있습니다.");
            fileInput.value = ""; // 선택 초기화

        }
    }

    if( num == 1 ){

    	if (files.length > 2) {
            alert("이미지는 최대 3개이고 2장더 업로드할 수 있습니다.");
            fileInput.value = ""; // 선택 초기화

        }
    }

    if( num == 0 ){
    	if (files.length > 3) {
            alert("이미지는 최대 3장까지만 업로드할 수 있습니다.");
            fileInput.value = ""; // 선택 초기화

        }
    }


    if (files.length > 3) {
        alert("이미지는 최대 3장까지만 업로드할 수 있습니다.");
        fileInput.value = ""; // 선택 초기화

    }

}


function formatPrice(input) {
    // 숫자만 추출
    let value = input.value.replace(/[^0-9]/g, '');

    // 3자리마다 , 찍기
    input.value = value.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}