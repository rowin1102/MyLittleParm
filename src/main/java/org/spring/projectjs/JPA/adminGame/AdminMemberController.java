package org.spring.projectjs.JPA.adminGame;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.JPA.App.Entity.Member;
import org.spring.projectjs.JPA.App.Repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** 회원(게임 정보) 관리자 */
@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberRepository memberRepository;

    /** 🔎 목록 + 간단 검색 (단수 경로 /api/admin/member) */
    @GetMapping
    public ResponseEntity<Page<Member>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // 1) userId 정확 일치
        if (userId != null && !userId.isBlank()) {
            return ResponseEntity.ok(
                    memberRepository.findByUserId(userId, pageable)
            );
        }

        // 2) nickname/email 부분검색
        if ((nickname != null && !nickname.isBlank()) ||
                (email != null && !email.isBlank())) {
            String nk = nickname == null ? "" : nickname;
            String em = email == null ? "" : email;
            return ResponseEntity.ok(
                    memberRepository.findByNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(nk, em, pageable)
            );
        }

        // 3) 자유검색 q: userId/nickname/email 포함
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(
                    memberRepository.findByUserIdContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            q, q, q, pageable
                    )
            );
        }

        // 4) 필터 없음 → 전체 페이지네이션
        return ResponseEntity.ok(memberRepository.findAll(pageable));
    }

    /** 🔎 단건 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** ✏️ 게임 정보 부분 수정 (코인/경험치/레벨) */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateGameInfo(
            @PathVariable Long id,
            @RequestBody MemberGameUpdateReq req) {

        Optional<Member> opt = memberRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Member m = opt.get();

        // Member 필드가 Long 타입인 경우 (권장: DTO도 Long)
        if (req.gamePoint() != null) m.setGamePoint(req.gamePoint());
        if (req.gameExp()   != null) m.setGameExp(req.gameExp());
        if (req.gameLevel() != null) m.setGameLevel(req.gameLevel());

        return ResponseEntity.ok(memberRepository.save(m));
    }

    /** 요청 DTO: null 이면 기존값 유지 */
    public record MemberGameUpdateReq(Long gamePoint, Long gameExp, Long gameLevel) {}
}
