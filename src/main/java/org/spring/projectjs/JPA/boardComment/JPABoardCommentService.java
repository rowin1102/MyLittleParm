// src/main/java/org/spring/projectjs/JPA/boardComment/JPABoardCommentService.java
package org.spring.projectjs.JPA.boardComment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JPABoardCommentService {

    private final JPABoardCommentRepository repo;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    public JPABoardComment create(Long commentId, String content, String writer) {
        JPABoardComment c = JPABoardComment.builder()
                .commentId(commentId)
                .commentContent(content)
                .writer(writer)
                .commentDate(new Date())
                .build();
        return repo.save(c);
    }

    @Transactional(readOnly = true)
    public Page<JPABoardComment> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "commentIdx"));
        return repo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public JPABoardComment get(Long commentIdx) {
        return repo.findById(commentIdx)
                .orElseThrow(() -> new IllegalArgumentException("comment not found: " + commentIdx));
    }

    @Transactional
    public JPABoardComment update(Long commentIdx, String content, String writer) {
        JPABoardComment c = get(commentIdx);
        if (content != null && !content.isBlank()) c.setCommentContent(content);
        if (writer  != null && !writer.isBlank())  c.setWriter(writer);
        return repo.save(c);
    }

    @Transactional
    public void delete(Long commentIdx) {
        repo.deleteById(commentIdx);
    }

    @Transactional(readOnly = true)
    public Page<JPABoardComment> search(
            Long commentId, String writer, String q,
            LocalDate from, LocalDate to,
            int page, int size
    ) {
        Date fromDate = null, toDate = null;
        if (from != null) fromDate = Date.from(from.atStartOfDay(KST).toInstant());
        if (to   != null) toDate   = Date.from(to.plusDays(1).atStartOfDay(KST).toInstant()); // [from, to)

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "commentIdx"));
        return repo.search(commentId, nn(writer), nn(q), fromDate, toDate, pageable);
    }

    private String nn(String s) { return (s == null || s.isBlank()) ? null : s; }
}
