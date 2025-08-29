package org.spring.projectjs.JPA.member;

import org.spring.projectjs.JPA.App.Entity.Member;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/member")
public class JPAMemberController {

    private final JPAMemberService memberService;

    public JPAMemberController(JPAMemberService memberService) {
        this.memberService = memberService;
    }

    /** 전체 회원 조회  GET /api/member */
    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    /**
     * 검색(프론트 호환)  GET /api/member/search?keyword=...
     * - 현재 서비스에 닉네임 단건 조회만 있으므로,
     *   keyword를 닉네임으로 간주해서 단건을 리스트 형태로 반환
     * - 없으면 빈 배열 반환
     */
    @GetMapping("/search")
    public List<Member> searchMembers(@RequestParam String keyword) {
        try {
            Member m = memberService.getMemberByNickname(keyword);
            return Collections.singletonList(m);
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }

    /** 회원 생성  POST /api/member */
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    /**
     * (선택) 닉네임 단건 조회  GET /api/member/by-nickname/{nickname}
     * - 필요하면 프론트에서 직접 이 경로도 사용할 수 있음
     */
    @GetMapping("/by-nickname/{nickname}")
    public Member getMemberByNickname(@PathVariable String nickname) {
        return memberService.getMemberByNickname(nickname);
    }

    /** 회원 수정  PUT /api/member/editMember/{memberIdx} */
    @PutMapping("/editMember/{memberIdx}")
    public Member updateMember(@PathVariable Long memberIdx, @RequestBody Member member) {
        return memberService.updatedMember(memberIdx, member);
    }

    /** 회원 삭제  DELETE /api/member/deleteMember/{memberIdx} */
    @DeleteMapping("/deleteMember/{memberIdx}")
    public void deleteMember(@PathVariable Long memberIdx) {
        memberService.deleteMember(memberIdx);
    }
}
