package org.spring.projectjs.JPA.App.Repository;

import org.spring.projectjs.JPA.App.Entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId); // ✅ 단건
    Page<Member> findAll(Pageable pageable); // ✅ 목록
    Optional<Member> findByNickname(String nickname); // ✅ 단건 (컨트롤러에서는 안씀)
    Page<Member> findByNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nickname, String email, Pageable pageable); // ✅ 부분검색
    Page<Member> findByUserIdContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String userIdPart, String nicknamePart, String emailPart, Pageable pageable); // ✅ 자유검색
    Page<Member> findByUserId(String userId, Pageable pageable);  // ← 이 한 줄 추가
}
