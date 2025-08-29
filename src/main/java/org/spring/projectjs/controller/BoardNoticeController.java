package org.spring.projectjs.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.Principal;

import org.spring.projectjs.jdbc.BoardDTO;
import org.spring.projectjs.jdbc.BoardNoticeDTO;
import org.spring.projectjs.jdbc.CommentDTO;
import org.spring.projectjs.jdbc.IBoardNotice;
import org.spring.projectjs.jdbc.ParameterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.CookieManager;
import utils.FileUtil;
import utils.PagingUtil;

@Controller
public class BoardNoticeController {

    @Autowired
    IBoardNotice dao;

    // 목록 가져오기
    @GetMapping("/boardNoticeList.do")
    public String member2(Model model, HttpServletRequest req, ParameterDTO parameterDTO) {

        int totalCount = dao.notice_totalCount(parameterDTO);
        int pageSize = 10;
        int blockPage = 5;
        int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals(""))
                ? 1
                : Integer.parseInt(req.getParameter("pageNum"));

        String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals(""))
                ? ""
                : req.getParameter("searchField");

        String searchKeyword = (req.getParameter("searchKeyword") == null || req.getParameter("searchKeyword").equals(""))
                ? ""
                : req.getParameter("searchKeyword");

        System.out.println("필드 키워드 : " + searchField + " : " + searchKeyword + " : " + pageNum);

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
        model.addAttribute("maps", maps);

        ArrayList<BoardDTO> lists = dao.notice_listPage(parameterDTO);
        model.addAttribute("lists", lists);

        String pagingImg = PagingUtil.pagingImg(totalCount, pageSize, blockPage, pageNum,
                req.getContextPath() + "/boardNoticeList.do?", searchField, searchKeyword,req.getContextPath());
        System.out.println("컨텍스트패스 : " + req.getContextPath() + "/boardNoticeList.do?");
        model.addAttribute("pagingImg", pagingImg);

        return "board/boardNoticeList";
    }

    // 상세보기
    @GetMapping("/boardNoticeView.do")
    public String memberGet3(HttpServletResponse resp, HttpServletRequest req, BoardNoticeDTO boardNoticeDTO, Model model) {

        String idx = req.getParameter("board_idx");

        int num = (req.getParameter("num") == null || req.getParameter("num").equals(""))
                ? 0
                : Integer.parseInt(req.getParameter("num"));

        System.out.println("아이디 엑스 : " + idx);
        System.out.println("넘버 : " + num);

        String boName = "visit";// 설정값
        String ckName = boName + "-" + idx;
        int result = cookieOneDay(req, model, resp, ckName);

        if (result == 1) {
            int count = dao.notice_board_visitcounter(idx);
            if (count == 1)
                System.out.println("방문자수 증가");
        }

        if (num == 5) {
            String boName2 = "good";// 설정값
            String ckName2 = boName2 + "-" + idx;
            int result2 = cookieOneDay(req, model, resp, ckName2);
            System.out.println("굿 레졸트 " + result2);
            if (result2 == 1) {
                int count2 = dao.notice_board_good_count(idx);
                if (count2 == 1)
                    System.out.println("좋아요 숫자 증가");
            }
        } else if (num == 6) {
            String boName3 = "worse";// 설정값
            String ckName3 = boName3 + "-" + idx;
            int result3 = cookieOneDay(req, model, resp, ckName3);
            System.out.println("싫어요 레졸트 " + result3);
            if (result3 == 1) {
                int count3 = dao.notice_board_worse_count(idx);
                if (count3 == 1)
                    System.out.println("싫어요 숫자 증가");
            }
        }

        String searchField = req.getParameter("searchField");
        String searchKeyword = req.getParameter("searchKeyword");
        String pageNum = req.getParameter("pageNum");

        System.out.println("뷰페이지 : " + searchField + " : " + searchKeyword + " :" + pageNum);

        Map<String, String> maps = new HashMap<>();
        maps.put("searchField", searchField);
        maps.put("searchKeyword", searchKeyword);
        maps.put("pageNum", pageNum);

        boardNoticeDTO = dao.notice_view(boardNoticeDTO);
        boardNoticeDTO.setBoard_content(boardNoticeDTO.getBoard_content().replace("\r\n", "<br>"));

        String cate1 = fileCheckName(boardNoticeDTO.getOfile1());
        String cate2 = fileCheckName(boardNoticeDTO.getOfile2());
        String cate3 = fileCheckName(boardNoticeDTO.getOfile3());

        System.out.println(cate1 + " : " + cate2 + " : " + cate3);

        if (cate1 != null) {
            maps.put("cate1", cate1);
        }

        if (cate2 != null) {
            maps.put("cate2", cate2);
        }

        if (cate3 != null) {
            maps.put("cate3", cate3);
        }

        ArrayList<CommentDTO> comment_list = dao.notice_comment_select(idx);

        model.addAttribute("maps", maps);
        model.addAttribute("boardNoticeDTO", boardNoticeDTO);
        model.addAttribute("lists", comment_list);

        return "board/boardNoticeView";
    }

    // 댓글쓰기
    @PostMapping("/notice_commentWrite.do")
    public String commentWrite(HttpServletRequest req, CommentDTO comment, Model model, Principal principal) {

        String board_idx = req.getParameter("board_idx");
        String comment_content = req.getParameter("comment_content");

        // 작성자는 로그인 정보로 서버에서 채움 (NOT NULL 방지)
        String writer = (principal != null ? principal.getName() : null);

        System.out.println("댓글등록 공지 : " + board_idx + " : " + comment_content + " :  작성자 : " + writer);

        if (writer == null || writer.isBlank()) {
            return "redirect:/myLogin.do";
        }

        int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals(""))
                ? 1
                : Integer.parseInt(req.getParameter("pageNum"));

        String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals(""))
                ? ""
                : req.getParameter("searchField");

        String searchKeyword = (req.getParameter("searchKeyword") == null || req.getParameter("searchKeyword").equals(""))
                ? ""
                : req.getParameter("searchKeyword");

        System.out.println("커멘트 쓰기 : " + board_idx + " : " + comment_content + " : " + writer + " : " + pageNum + " : "
                + searchField + " : " + searchKeyword);

        if (comment_content != null) {
            comment_content = comment_content.replace("\r\n", "<br>");
        }

        dao.notice_comment_insert(board_idx, comment_content, writer);

        String encoded = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        return "redirect:boardNoticeView.do?board_idx=" + board_idx + "&pageNum=" + pageNum +
                "&searchField=" + searchField + "&searchKeyword=" + encoded;
    }

    // 댓글 삭제
    @GetMapping("/notice_commentDelete.do")
    public String commentDelete(HttpServletRequest req, CommentDTO comment, Model model) {
        System.out.println("111111111112@22222222");

        String board_idx = req.getParameter("board_idx");
        String comment_idx = req.getParameter("comment_idx");

        int pageNum = (req.getParameter("pageNum") == null || req.getParameter("pageNum").equals(""))
                ? 1
                : Integer.parseInt(req.getParameter("pageNum"));

        String searchField = (req.getParameter("searchField") == null || req.getParameter("searchField").equals(""))
                ? ""
                : req.getParameter("searchField");

        String searchKeyword = (req.getParameter("searchKeyword") == null || req.getParameter("searchKeyword").equals(""))
                ? ""
                : req.getParameter("searchKeyword");

        System.out.println("커멘트 셀렉트 : " + comment_idx + " : " + board_idx + " : " + pageNum + " : " + searchField + " : "
                + searchKeyword);

        int result = dao.notice_comment_delete(comment_idx);
        if (result == 1)
            System.out.println("댓글삭제 성공");

        String encoded = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
        return "redirect:boardNoticeView.do?board_idx=" + board_idx + "&pageNum=" + pageNum +
                "&searchField=" + searchField + "&searchKeyword=" + encoded;
    }

    // 파일 다운로드
    @GetMapping("/noticeBoardDownload.do")
    public void memberGet5(HttpServletRequest req, HttpServletResponse res) {
        String idx = req.getParameter("idx");
        String ofile1 = req.getParameter("ofile1");
        String sfile1 = req.getParameter("sfile1");

        System.out.println(idx + " : " + ofile1 + " : " + sfile1);

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
            System.out.println("물리적 경로 : " + uploadDir);
            FileUtil.download(req, res, uploadDir, sfile1, ofile1);
        } catch (Exception e) {
            System.out.println("download 에러 발생" + e);
            e.printStackTrace();
        }
    }

    // 이미지/동영상/음악 파일 카테고리 확인
    public String fileCheckName(String fileName) {
        String cate = "";
        String ext;

        if (fileName == null || fileName.isEmpty()) {
            ext = "etc";
        } else {
            ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }

        String[] imgExts = { "jpg", "jpeg", "gif", "png", "bmp", "webp" };
        String[] videoExts = { "avi", "mp4", "mov", "wmv", "flv", "mkv" };
        String[] audioExts = { "mp3", "wav", "ogg", "aac", "flac" };

        List<String> imgList = new ArrayList<>(Arrays.asList(imgExts));
        List<String> videoList = new ArrayList<>(Arrays.asList(videoExts));
        List<String> audioList = new ArrayList<>(Arrays.asList(audioExts));

        if (imgList.contains(ext)) {
            cate = "img";
        } else if (videoList.contains(ext)) {
            cate = "video";
        } else if (audioList.contains(ext)) {
            cate = "audio";
        } else {
            cate = "etc";
        }

        System.out.println("카테고리: " + cate);
        return cate;
    }

    // 쿠키생성 및 방문자수/좋아요/싫어요 카운트
    public int cookieOneDay(HttpServletRequest req, Model model, HttpServletResponse resp, String ckName) {

        int result = 0;

        if (ckName == null || ckName.trim().isEmpty()) {
            ckName = "visit";
        } else {
            ckName = ckName.trim().replaceAll("[^a-zA-Z0-9\\-_]", "");
            if (ckName.isEmpty()) {
                ckName = "visit";
            }
        }

        String rk = CookieManager.readCookie(req, ckName);

        if (rk.equals("")) {
            result = 1;
            CookieManager.makeCookie(resp, ckName, "true", 86400);
            model.addAttribute("message", "쿠키생성&조회수update");
        } else {
            model.addAttribute("message", "하루동안처리안함");
            result = 2;
        }

        return result;
    }
}
