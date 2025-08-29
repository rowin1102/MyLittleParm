package org.spring.projectjs.jdbc;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IBoard {

    public int totalCount(ParameterDTO parameterDTO);
    public ArrayList<BoardDTO> listPage(ParameterDTO parameterDTO);

    public MemberDTO member_view(String userId);

    public int insert(BoardDTO boardDTO);
    public BoardDTO view(BoardDTO boardDTO);
    public int update(BoardDTO boardDTO);
    public int delete(String idx);
    public int deleteFileOne(String idx, String imgCount);
    public int updateFile(BoardDTO boardDTO);
    public int updateTitleContent(String board_title, String board_content, String board_idx);
    public int board_visitcounter(String idx);
    public int board_good_count(String num);
    public int board_worse_count(String num);
    public int boardDeleteFileAll(String idx);

    //댓글등록
    public int comment_insert( Long boardIdx, String comment_content, String writer );
    public ArrayList<CommentDTO> comment_select( String board_idx );
    public int comment_delete(String comment_idx);
    public int comment_write_del(String board_idx);

}
