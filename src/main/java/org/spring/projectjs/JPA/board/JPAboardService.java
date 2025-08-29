package org.spring.projectjs.JPA.board;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JPAboardService {

    private final JPAboardRepository boardRepository;

    // 생성자 주입
    public JPAboardService(JPAboardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // 전체 게시글 조회
    public List<JPAboard> getAllBoards() {
        return boardRepository.findAll();
    }

    // boardId 기준 전체 게시글 조회
    public List<JPAboard> getBoardsByBoardId(Long boardId) {
        return boardRepository.findByBoardId(boardId);
    }

    // boardId + 제목 검색
    public List<JPAboard> searchBoardsByBoardIdAndTitle(Long boardId, String keyword) {
        return boardRepository.findByBoardIdAndTitleContaining(boardId, keyword);
    }

    // boardId + 작성자 검색
    public List<JPAboard> searchBoardsByBoardIdAndWriter(Long boardId, String writer) {
        return boardRepository.findByBoardIdAndWriterContaining(boardId, writer);
    }

    // 게시글 생성
    public JPAboard addBoard(JPAboard board) {
        return boardRepository.save(board);
    }

    // 게시글 삭제
    public void deleteBoard(Long boardIdx) {
        if (!boardRepository.existsById(boardIdx)) {
            throw new RuntimeException("게시글이 없습니다. boardIdx=" + boardIdx);
        }
        boardRepository.deleteById(boardIdx);
    }

    // 게시글 개별 조회 (boardIdx 기준)
    public JPAboard getBoardById(Long boardIdx) {
        return boardRepository.findById(boardIdx)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다. boardIdx=" + boardIdx));
    }

    // 게시글 수정
    @Transactional
    public JPAboard updateBoard(Long boardIdx, JPAboard updatedBoard) {
        JPAboard existingBoard = boardRepository.findById(boardIdx)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다. boardIdx=" + boardIdx));

        existingBoard.setBoardTitle(updatedBoard.getBoardTitle());
        existingBoard.setBoardContent(updatedBoard.getBoardContent());
        existingBoard.setBoardId(updatedBoard.getBoardId());
        existingBoard.setVisitCount(updatedBoard.getVisitCount());
        existingBoard.setBoardDate(updatedBoard.getBoardDate());
        existingBoard.setWriter(updatedBoard.getWriter());

        // 필요한 경우 OFILE/SFILE 필드도 추가
        existingBoard.setOfile1(updatedBoard.getOfile1());
        existingBoard.setSfile1(updatedBoard.getSfile1());
        existingBoard.setOfile2(updatedBoard.getOfile2());
        existingBoard.setSfile2(updatedBoard.getSfile2());
        existingBoard.setOfile3(updatedBoard.getOfile3());
        existingBoard.setSfile3(updatedBoard.getSfile3());

        return existingBoard;
    }
}
