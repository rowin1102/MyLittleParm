package org.spring.projectjs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.spring.projectjs.jdbc.BoardDTO;
import org.spring.projectjs.jdbc.CommentDTO;
import org.spring.projectjs.jdbc.IMyPage;
import org.spring.projectjs.jdbc.MyPageDTO;
import org.spring.projectjs.jdbc.ParameterDTO;
import org.spring.projectjs.jdbc.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import utils.PagingUtil;

//마이페이지
@Controller
public class MyPageController {

    @Autowired
    IMyPage dao;

    // 상세보기
    @GetMapping("/myPage.do")
    public String myPageView(MyPageDTO myPageDTO, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        myPageDTO.setUser_id(userId);

        MyPageDTO myPageDTO2 = dao.myPage_view(myPageDTO);
        model.addAttribute("member", myPageDTO2);
        return "mypage/myPage";
    }

    // 수정창 띄우기
    @PostMapping("/myPageEdit.do")
    public String myPageEditGet(MyPageDTO myPageDTO, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        myPageDTO.setUser_id(userId); // ★ 본인 ID 강제

        MyPageDTO myPageDTO2 = dao.myPage_view(myPageDTO);
        model.addAttribute("member", myPageDTO2);
        return "mypage/myPageEdit";
    }

    // 수정하기
    @PostMapping("/myPageUpdate.do")
    public String myPageEditPost(MyPageDTO myPageDTO, Model model) {
        // ★ 저장 시에도 본인 ID 강제
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        myPageDTO.setUser_id(userId);

        int result = dao.updateMyPageEdit(myPageDTO);
        if (result == 1) System.out.println("수정성공");
        return "redirect:myPage.do";
    }

    /* ===================== 닉네임 중복확인(AJAX, JSON) ===================== */
    @PostMapping("/mypage/nickname/check")
    @ResponseBody
    public Map<String, Object> nicknameCheckAjax(@RequestParam String nickname, Authentication auth) {
        String userId = (auth != null ? auth.getName() : null);
        String trimmed = nickname == null ? "" : nickname.trim();

        if (userId == null || trimmed.isEmpty()) {
            return Map.of("available", false, "message", "닉네임을 입력해 주세요.");
        }

        // 현재 닉네임 조회
        MyPageDTO cond = new MyPageDTO();
        cond.setUser_id(userId);
        MyPageDTO me = dao.myPage_view(cond);
        String currentNick = (me != null ? me.getNickname() : null);

        if (currentNick != null && currentNick.equals(trimmed)) {
            // 동일 닉네임: 사용 가능하지만 새로 바꾸는 건 아님
            return Map.of(
                "available", true,
                "same", true,
                "message", "현재 사용 중인 닉네임입니다."
            );
        }

        // 본인 제외 중복 검사(기존 쿼리 재사용)
        int count = dao.checkDuplicateNicknameExceptSelf(trimmed, userId);
        boolean ok = (count == 0);
        return Map.of(
            "available", ok,
            "message", ok ? "사용 가능한 닉네임입니다." : "이미 사용 중인 닉네임입니다."
        );
    }

    /* ===================== 이하 게시판/댓글/거래 목록은 그대로 ===================== */
    @GetMapping("/myPageBoardList.do")
    public String boardMyPageListGet(Model model, HttpServletRequest req, ParameterDTO parameterDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        int totalCount = dao.myPageBoardTotalCount(parameterDTO, userId);
        int pageSize = 10;
        int blockPage = 5;
        int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals("")
                ? 1 : Integer.parseInt(req.getParameter("pageNum")));

        String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals("")
                ? "" : req.getParameter("searchField"));

        String searchKeyword = (req.getParameter("searchKeyword") == null || req.getParameter("searchKeyword").equals("")
                ? "" : req.getParameter("searchKeyword"));

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        parameterDTO.setStart(start);
        parameterDTO.setEnd(end);
        parameterDTO.setSearchField(searchField);
        parameterDTO.setSearchKeyword(searchKeyword);

        Map<String, Object> maps = new HashMap<>();
        maps.put("totalCount", totalCount);
        maps.put("pageSize", pageSize);
        maps.put("pageNum", pageNum);
        maps.put("searchField", searchField);
        maps.put("searchKeyword", searchKeyword);
        maps.put("userId", userId);
        model.addAttribute("maps", maps);

        ArrayList<BoardDTO> lists = dao.myPageBoardListPage(parameterDTO, userId);
        model.addAttribute("lists", lists);

        String pagingImg = PagingUtil.pagingImg(totalCount, pageSize, blockPage, pageNum,
                req.getContextPath() + "/myPageBoardList.do?", searchField, searchKeyword, req.getContextPath());
        model.addAttribute("pagingImg", pagingImg);

        return "mypage/myPageBoardList";
    }

    @GetMapping("/myPageBoardCommentList.do")
    public String myPageBoardCommentList(Model model, HttpServletRequest req, ParameterDTO parameterDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        int totalCount = dao.myPage_board_comment_count(parameterDTO, userId);
        int pageSize = 10;
        int blockPage = 5;
        int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals("")
                ? 1 : Integer.parseInt(req.getParameter("pageNum")));

        String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals("")
                ? "" : req.getParameter("searchField"));

        String searchKeyword = (req.getParameter("searchKeyword") == null || req.getParameter("searchKeyword").equals("")
                ? "" : req.getParameter("searchKeyword"));

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        parameterDTO.setStart(start);
        parameterDTO.setEnd(end);
        parameterDTO.setSearchField(searchField);
        parameterDTO.setSearchKeyword(searchKeyword);

        Map<String, Object> maps = new HashMap<>();
        maps.put("totalCount", totalCount);
        maps.put("pageSize", pageSize);
        maps.put("pageNum", pageNum);
        maps.put("searchField", searchField);
        maps.put("searchKeyword", searchKeyword);
        maps.put("userId", userId);
        model.addAttribute("maps", maps);

        ArrayList<CommentDTO> lists = dao.myPage_board_comment_select(parameterDTO, userId);
        model.addAttribute("lists", lists);

        String pagingImg = PagingUtil.pagingImg(totalCount, pageSize, blockPage, pageNum,
                req.getContextPath() + "/myPageBoardCommentList.do?", searchField, searchKeyword,req.getContextPath());
        model.addAttribute("pagingImg", pagingImg);

        return "mypage/myPageBoardCommentList";
    }

    @GetMapping("/myPageTransactionList.do")
    public String myPageTransactionList(Model model, HttpServletRequest req, ParameterDTO parameterDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        int totalCount = dao.myPage_transaction_count(parameterDTO, userId);
        int pageSize = 10;
        int blockPage = 5;
        int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals("")
                ? 1 : Integer.parseInt(req.getParameter("pageNum")));

        String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals("")
                ? "" : req.getParameter("searchField"));

        String searchKeyword = (req.getParameter("searchKeyword") == null || req.getParameter("searchKeyword").equals("")
                ? "" : req.getParameter("searchKeyword"));

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        parameterDTO.setStart(start);
        parameterDTO.setEnd(end);
        parameterDTO.setSearchField(searchField);
        parameterDTO.setSearchKeyword(searchKeyword);

        Map<String, Object> maps = new HashMap<>();
        maps.put("totalCount", totalCount);
        maps.put("pageSize", pageSize);
        maps.put("pageNum", pageNum);
        maps.put("searchField", searchField);
        maps.put("searchKeyword", searchKeyword);
        maps.put("userId", userId);
        model.addAttribute("maps", maps);

        ArrayList<TransactionDTO> lists = dao.myPage_transaction_select(parameterDTO, userId);
        model.addAttribute("lists", lists);

        String pagingImg = PagingUtil.pagingImg(totalCount, pageSize, blockPage, pageNum,
                req.getContextPath() + "/myPageTransactionList.do?", searchField, searchKeyword,req.getContextPath());
        model.addAttribute("pagingImg", pagingImg);

        return "mypage/myPageTransactionList";
    }

    @GetMapping("/myPageBoardNoticeCommentList.do")
    public String myPageBoardNoticeList(Model model, HttpServletRequest req, ParameterDTO parameterDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        int totalCount = dao.myPage_boardNotice_count(parameterDTO, userId);
        int pageSize = 10;
        int blockPage = 5;
        int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals("")
                ? 1 : Integer.parseInt(req.getParameter("pageNum")));

        String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals("")
                ? "" : req.getParameter("searchField"));

        String searchKeyword = (req.getParameter("searchKeyword") == null || req.getParameter("searchKeyword").equals("")
                ? "" : req.getParameter("searchKeyword"));

        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        parameterDTO.setStart(start);
        parameterDTO.setEnd(end);
        parameterDTO.setSearchField(searchField);
        parameterDTO.setSearchKeyword(searchKeyword);

        Map<String, Object> maps = new HashMap<>();
        maps.put("totalCount", totalCount);
        maps.put("pageSize", pageSize);
        maps.put("pageNum", pageNum);
        maps.put("searchField", searchField);
        maps.put("searchKeyword", searchKeyword);
        maps.put("userId", userId);
        model.addAttribute("maps", maps);

        ArrayList<CommentDTO> lists = dao.myPage_boardNotice_select(parameterDTO, userId);
        model.addAttribute("lists", lists);

        String pagingImg = PagingUtil.pagingImg(totalCount, pageSize, blockPage, pageNum,
                req.getContextPath() + "/myPageBoardNoticeCommentList.do?", searchField, searchKeyword,req.getContextPath());
        model.addAttribute("pagingImg", pagingImg);

        return "mypage/myPageBoardNoticeCommentList";
    }

    // 회원 탈퇴 확인 페이지 (폼)
    @GetMapping("/myPageMemberDelete.do")
    public String myPageMemberDeleteGet() {
        return "mypage/myPageMemberDelete"; // JSP 경로
    }

    // 실제 탈퇴 처리 (POST)
    @PostMapping("/mypage/delete")
    public String myPageMemberDeletePost(HttpServletRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        try {
            dao.myPageMemberDelete(userId); // ★ DB 삭제
        } catch (Exception e) {
            // 필요시 로깅
            e.printStackTrace();
        }

        // 세션/인증 정리
        try { req.logout(); } catch (Exception ignore) {}
        try { req.getSession(false).invalidate(); } catch (Exception ignore) {}
        SecurityContextHolder.clearContext();

        return "redirect:/";
    }
    
    
}
