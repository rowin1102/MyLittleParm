package org.spring.projectjs.JPA.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JPAboardRepository extends JpaRepository<JPAboard, Long> {

    // 제목 검색
    List<JPAboard> findByBoardTitleContaining(String boardTitle);

    // 내용 검색
    List<JPAboard> findByBoardContentContaining(String boardContent);

    // 작성자 검색
    List<JPAboard> findByWriterContaining(String writer);

    // boardId로 조회 (JPQL)
    @Query("SELECT b FROM JPAboard b WHERE b.boardId = :boardId")
    List<JPAboard> findByBoardId(@Param("boardId") Long boardId);

    // boardId + 제목 검색
    @Query("SELECT b FROM JPAboard b WHERE b.boardId = :boardId AND b.boardTitle LIKE %:keyword%")
    List<JPAboard> findByBoardIdAndTitleContaining(@Param("boardId") Long boardId,
                                                   @Param("keyword") String keyword);

    // boardId + 작성자 검색
    @Query("SELECT b FROM JPAboard b WHERE b.boardId = :boardId AND b.writer LIKE %:writer%")
    List<JPAboard> findByBoardIdAndWriterContaining(@Param("boardId") Long boardId,
                                                    @Param("writer") String writer);
}