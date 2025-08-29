package org.spring.projectjs.JPA.member;

import org.spring.projectjs.JPA.App.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPAMemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByNickname(String nickname);
}
