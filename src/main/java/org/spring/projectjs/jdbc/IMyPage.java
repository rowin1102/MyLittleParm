package org.spring.projectjs.jdbc;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMyPage {
	
	public MyPageDTO myPage_view(MyPageDTO myPageDTO);
	public int updateMyPageEdit(MyPageDTO myPageDTO);
	int checkDuplicateNicknameExceptSelf(String nickname, String user_id);
	
	//public int myPageBoardTotalCount(ParameterDTO parameterDTO);
	public int myPageBoardTotalCount(ParameterDTO parameterDTO, String userId);	
	public ArrayList<BoardDTO> myPageBoardListPage(ParameterDTO parameterDTO, String userId);
	
	//자유 게시판 내가쓴근 총 갯수
	public int myPage_board_comment_count(ParameterDTO parameterDTO, String userId);
	//자유 게시판 댓글
	public ArrayList<CommentDTO> myPage_board_comment_select(ParameterDTO parameterDTO, String userId);
	
	// 거래 게시판 총갯수
	public int myPage_transaction_count(ParameterDTO parameterDTO, String userId);
	// 거래 게시판 내가 쓴글
	public ArrayList<TransactionDTO> myPage_transaction_select(ParameterDTO parameterDTO, String userId);
	
	//공지사항 댓글
	public int myPage_boardNotice_count(ParameterDTO parameterDTO, String userId);
	public ArrayList<CommentDTO> myPage_boardNotice_select(ParameterDTO parameterDTO, String userId);
	
	int myPageMemberDelete(@Param("userId") String userId);
}
