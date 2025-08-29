/**
 *
 */
/*
function deletePost(idx, sfile1, sfile2, sfile3 ){
   //alert("11111");

   //alert( idx + " : " + sfile1 + " : " + sfile2 + " : " + sfile3 );

   //alert("11111111");

   var f = document.writeFrm;
   f.action="/boardDelete.do";
   f.method="post";
   f.board_idx.value=idx;
   f.sfile1.value=sfile1;
   f.sfile2.value=sfile2;
   f.sfile3.value=sfile3;

   if( confirm("삭제하시겠습니까?") ){
       f.submit();
   }
}
*/

//댓글등록
function notice_commentWrite(){

	//alert("7777777");

	var f = document.commentFrm;
	//f.action="/boardDelete.do";
	//f.method="post";
	if ( f.comment_content.value == '' ){
		alert("댓글을 입력해주세요");
		f.comment_content.focus();
		return;
	}

	if( confirm("댓글을 등록 하시겠습니까?") ){
		f.submit();
	}

}

//댓글삭제
function notice_commentDelete(comment_idx, board_idx, pageNum, searchField, searchKeyword){
	//alert( comment_idx + " : " + board_idx + " : " + pageNum + " : " + searchField + " : " + searchKeyword );
	//alert("555555");
	//var f = document.commentDeleteFrm;

	if( confirm("댓글을 삭제하시겠습니까?") ){

		location.href="./notice_commentDelete.do"
			+ "?comment_idx="+comment_idx
			+ "&board_idx="+board_idx
			+ "&pageNum="+pageNum
			+ "&searchField="+searchField
			+ "&searchKeyword="+searchKeyword;

	}
}

/*
function editPage(idx){
	//alert("아이디엑스 : " + idx);
	if(confirm("수정 하시겠습니까?")){
		location.href='./boardEdit.do?board_idx='+idx;
	}
}*/