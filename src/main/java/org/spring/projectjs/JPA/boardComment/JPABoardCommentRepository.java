// src/main/java/org/spring/projectjs/JPA/boardComment/JPABoardCommentRepository.java
package org.spring.projectjs.JPA.boardComment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface JPABoardCommentRepository extends JpaRepository<JPABoardComment, Long> {

    // 기본 조회들
    Page<JPABoardComment> findByCommentId(Long commentId, Pageable pageable);

    Page<JPABoardComment> findByWriterContainingIgnoreCase(String writer, Pageable pageable);

    Page<JPABoardComment> findByCommentContentContainingIgnoreCase(String q, Pageable pageable);

    // 관리용 통합 검색 (파라미터 선택적)
    @Query("""
      select c from JPABoardComment c
       where (:commentId is null or c.commentId = :commentId)
         and (:writer    is null or lower(c.writer) like lower(concat('%', :writer, '%')))
         and (:q         is null or lower(c.commentContent) like lower(concat('%', :q, '%')))
         and (:fromDate  is null or c.commentDate >= :fromDate)
         and (:toDate    is null or c.commentDate <  :toDate)
    """)
    Page<JPABoardComment> search(
            @Param("commentId") Long commentId,
            @Param("writer") String writer,
            @Param("q") String q,
            @Param("fromDate") Date fromDate,   // 포함
            @Param("toDate") Date toDate,       // 미포함(다음날 0시로 세팅 추천)
            Pageable pageable
    );
}
