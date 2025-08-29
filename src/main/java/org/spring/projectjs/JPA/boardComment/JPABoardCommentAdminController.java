// src/main/java/org/spring/projectjs/JPA/boardComment/JPABoardCommentAdminController.java
package org.spring.projectjs.JPA.boardComment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/comments")
@CrossOrigin(origins = "*")
// @PreAuthorize("hasRole('ADMIN')") // 메소드 보안 쓰면 주석 해제
public class JPABoardCommentAdminController {

    private final JPABoardCommentService service;

    /** 목록 or 조건 검색 */
    @GetMapping
    public ResponseEntity<Page<JPABoardComment>> listOrSearch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long commentId,
            @RequestParam(required = false) String writer,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        boolean doSearch = commentId != null || writer != null || query != null || from != null || to != null;
        Page<JPABoardComment> pageRes = doSearch
                ? service.search(commentId, writer, query, from, to, page, size)
                : service.list(page, size);
        return ResponseEntity.ok(pageRes);
    }

    /** 단건 조회 */
    @GetMapping("/{commentIdx}")
    public ResponseEntity<JPABoardComment> get(@PathVariable Long commentIdx) {
        return ResponseEntity.ok(service.get(commentIdx));
    }

    /** 생성 */
    @PostMapping
    public ResponseEntity<JPABoardComment> create(@RequestBody CreateReq req) {
        return ResponseEntity.ok(service.create(req.commentId(), req.commentContent(), req.writer()));
    }

    /** 수정 */
    @PutMapping("/{commentIdx}")
    public ResponseEntity<JPABoardComment> update(@PathVariable Long commentIdx, @RequestBody UpdateReq req) {
        return ResponseEntity.ok(service.update(commentIdx, req.commentContent(), req.writer()));
    }

    /** 삭제 */
    @DeleteMapping("/{commentIdx}")
    public ResponseEntity<Void> delete(@PathVariable Long commentIdx) {
        service.delete(commentIdx);
        return ResponseEntity.noContent().build();
    }

    /* ===== DTOs ===== */
    public record CreateReq(Long commentId, String commentContent, String writer) {}
    public record UpdateReq(String commentContent, String writer) {}
}
