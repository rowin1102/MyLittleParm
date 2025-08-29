package org.spring.projectjs.jdbc;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IBoardNotice {

	// 전체 개수 및 목록/상세
	int notice_totalCount(ParameterDTO parameterDTO);

	ArrayList<BoardDTO> notice_listPage(ParameterDTO parameterDTO);

	BoardNoticeDTO notice_view(BoardNoticeDTO boardNoticeDTO);

	// 방문자수/좋아요/싫어요
	int notice_board_visitcounter(String idx);

	int notice_board_good_count(String num);

	int notice_board_worse_count(String num);

	// 댓글
	int notice_comment_insert(@Param("board_idx") String board_idx, @Param("comment_content") String comment_content,
			@Param("writer") String writer);

	ArrayList<CommentDTO> notice_comment_select(String board_idx);

	int notice_comment_delete(String comment_idx);

	int notice_comment_write_del(String board_idx);
}
