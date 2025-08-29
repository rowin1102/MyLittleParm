package org.spring.projectjs.JPA.member;

import jakarta.transaction.Transactional;
import org.spring.projectjs.JPA.App.Entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JPAMemberService {

    JPAMemberRepository memberRepository;

    // 생성자 주입
    public JPAMemberService(JPAMemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }

    // 회원 전체 조회
    public List<Member> getAllMembers() {

        return memberRepository.findAll();
    }

    // 회원 생성 메서드
    public Member createMember(Member member) {

        return memberRepository.save(member);
    }

    //회원 개별조회
    public Member getMemberByNickname(String nickname) {
        return memberRepository.findMemberByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("회원이 없습니다. id=" + nickname));
    }

    //회원 수정
    @Transactional
    public Member updatedMember(Long memberIdx, Member updatedMember) {
        Member existingMember = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new RuntimeException("회원이 없습니다. id=" + memberIdx));

        // 필요한 필드만 수정 (예시)
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setPhone(updatedMember.getPhone());
        existingMember.setNickname(updatedMember.getNickname());
        existingMember.setPassword(updatedMember.getPassword());
        existingMember.setDomain(updatedMember.getDomain());
        existingMember.setMemberAuth(updatedMember.getMemberAuth());
        existingMember.setGamePoint(updatedMember.getGamePoint());

        return existingMember;
    }

    //회원 삭제
    public void deleteMember(Long memberIdx) {
        if (!memberRepository.existsById(memberIdx)) {
            throw new RuntimeException("회원이 없습니다. memberIdx=" + memberIdx);
        }
        memberRepository.deleteById(memberIdx);
    }
}
