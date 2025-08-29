package org.spring.projectjs.JPA.board;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/board")
public class JPAboardController {

    private final JPAboardService boardService;

    public JPAboardController(JPAboardService boardService) {
        this.boardService = boardService;
    }

    // boardId 기준 전체 게시판 조회 + 선택적 검색(keyword)
    @GetMapping
    public List<JPAboard> getBoards(
            @RequestParam(required = false) Long boardId,
            @RequestParam(required = false) String searchKeyword
    ) {
        if (boardId != null && searchKeyword != null && !searchKeyword.isEmpty()) {
            // boardId + 제목 검색
            return boardService.searchBoardsByBoardIdAndTitle(boardId, searchKeyword);
        } else if (boardId != null) {
            // boardId 기준 전체 글 조회
            return boardService.getBoardsByBoardId(boardId);
        } else {
            // 전체 게시판 조회
            return boardService.getAllBoards();
        }
    }

    // 게시글 등록
    @PostMapping
    public JPAboard addBoard(@RequestBody JPAboard board) {
        return boardService.addBoard(board);
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{boardIdx}")
    public void deleteBoard(@PathVariable Long boardIdx) {
        boardService.deleteBoard(boardIdx);
    }

    // 게시글 수정
    @PutMapping("/edit/{boardIdx}")
    public JPAboard editBoard(@PathVariable Long boardIdx, @RequestBody JPAboard board) {
        return boardService.updateBoard(boardIdx, board);
    }
}
