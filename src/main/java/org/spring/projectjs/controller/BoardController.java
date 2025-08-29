package org.spring.projectjs.controller;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spring.projectjs.jdbc.BoardDTO;
import org.spring.projectjs.jdbc.CommentDTO;
import org.spring.projectjs.jdbc.IBoard;
import org.spring.projectjs.jdbc.ParameterDTO;
import org.spring.projectjs.utils.MyFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import utils.CookieManager;
import utils.FileUtil;
import utils.PagingUtil;

@Controller
public class BoardController {

    @Autowired
    IBoard dao;

    /* ===================== 공통 유틸 ===================== */
    private String trimOrEmpty(String v) {
        return v == null ? "" : v.trim();
    }
    private int parseIntOrDefault(String v, int def) {
        if (v == null) return def;
        String t = v.trim();
        if (!t.matches("-?\\d+")) return def;
        try { return Integer.parseInt(t); }
        catch (Exception e) { return def; }
    }

    /* ===================== 권한 유틸 ===================== */
    private boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    private boolean isBoardOwner(String boardIdx, String username) {
        if (username == null) return false;
        BoardDTO q = new BoardDTO();
        q.setBoard_idx(boardIdx);
        BoardDTO board = dao.view(q);
        return board != null && username.equals(board.getWriter());
    }

    private boolean isCommentOwner(String boardIdx, String commentIdx, String username) {
        if (username == null) return false;
        ArrayList<CommentDTO> comments = dao.comment_select(boardIdx);
        if (comments == null) return false;
        String target = trimOrEmpty(commentIdx);
        for (CommentDTO c : comments) {
            String cid = String.valueOf(c.getComment_idx());
            if (cid.equals(target)) {
                return username.equals(c.getWriter());
            }
        }
        return false;
    }

    /* ===================== 댓글 삭제 (GET/POST 허용) ===================== */
    @RequestMapping(value = "/commentDelete.do", method = {RequestMethod.GET, RequestMethod.POST})
    public String commentDelete(HttpServletRequest req, Model model) {
        String board_idx   = trimOrEmpty(req.getParameter("board_idx"));
        String comment_idx = trimOrEmpty(req.getParameter("comment_idx"));

        int pageNum = parseIntOrDefault(req.getParameter("pageNum"), 1);
        String searchField   = trimOrEmpty(req.getParameter("searchField"));
        String searchKeyword = trimOrEmpty(req.getParameter("searchKeyword"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String me = currentUser();
        boolean admin = isAdmin(auth);

        // 작성자 또는 ADMIN만 허용
        if (!admin && !isCommentOwner(board_idx, comment_idx, me)) {
            String redirect = "redirect:boardView.do?board_idx=" + board_idx
                    + "&pageNum=" + pageNum
                    + "&searchField=" + searchField
                    + "&searchKeyword=" + URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8)
                    + "&error=forbidden";
            return redirect;
        }

        int result = dao.comment_delete(comment_idx);
        if (result == 1) System.out.println("댓글삭제 성공");

        return "redirect:boardView.do?board_idx=" + board_idx
                + "&pageNum=" + pageNum
                + "&searchField=" + searchField
                + "&searchKeyword=" + URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
    }

    /* ===================== 댓글 쓰기 ===================== */
    @PostMapping("/commentWrite.do")
    public String commentWrite(
            @RequestParam(name = "board_idx") Long boardIdx,
            @RequestParam(name = "pageNum",      defaultValue = "1")  int pageNum,
            @RequestParam(name = "searchField",  defaultValue = "")   String searchField,
            @RequestParam(name = "searchKeyword",defaultValue = "")   String searchKeyword,
            CommentDTO comment,
            Principal principal
    ) {
        String commentContent = comment.getComment_content();
        String writer = principal.getName();

        commentContent = commentContent.replace("\r\n", "<br>");
        dao.comment_insert(boardIdx, commentContent, writer);

        return "redirect:boardView.do?board_idx=" + boardIdx
            + "&pageNum=" + pageNum
            + "&searchField=" + trimOrEmpty(searchField)
            + "&searchKeyword=" + URLEncoder.encode(trimOrEmpty(searchKeyword), StandardCharsets.UTF_8);
    }

    /* ===================== 목록 ===================== */
    @GetMapping("/boardList.do")
    public String member2(Model model, HttpServletRequest req,
                          ParameterDTO parameterDTO) {

        // 검색 파라미터 먼저 정리
        String searchField = trimOrEmpty(req.getParameter("searchField"));
        String searchKeyword = trimOrEmpty(req.getParameter("searchKeyword"));
        int pageNum = parseIntOrDefault(req.getParameter("pageNum"), 1);

        int totalCount = dao.totalCount(parameterDTO);
        int pageSize = 10;
        int blockPage = 5;

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

        ArrayList<BoardDTO> lists = dao.listPage(parameterDTO);
        model.addAttribute("lists", lists);

        String pagingImg = PagingUtil.pagingImg(totalCount, pageSize, blockPage, pageNum,
                req.getContextPath() + "/boardList.do?", searchField, searchKeyword,req.getContextPath());
        model.addAttribute("pagingImg", pagingImg);

        return "board/boardList";
    }

    /* ===================== 글쓰기 등록 ===================== */
    @PostMapping("/boardWrite.do")
    public String boardWritePOST(HttpServletRequest req, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        String board_title = trimOrEmpty(req.getParameter("board_title"));
        String board_content = trimOrEmpty(req.getParameter("board_content"));

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
            Map<String, String> saveFileMap = new HashMap<>();

            Collection<Part> parts = req.getParts();
            for (Part part : parts) {
                if (!"ofile".equals(part.getName())) continue;

                String partHeader = part.getHeader("content-disposition");
                String[] phArr = partHeader.split("filename=");
                String originalFileName = phArr[1].trim().replace("\"", "");

                String savedFileName = "";
                if (!originalFileName.isEmpty()) {
                    part.write(uploadDir + File.separator + originalFileName);
                    savedFileName = MyFunctions.renameFile(uploadDir, originalFileName);
                }
                saveFileMap.put(originalFileName, savedFileName);
            }

            int i = 1;
            BoardDTO board = new BoardDTO();
            board.setBoard_title(board_title);
            board.setBoard_content(board_content);
            board.setWriter(userId);

            for (String key : saveFileMap.keySet()) {
                if (i == 1) {
                    board.setOfile1(key); board.setSfile1(saveFileMap.get(key));
                } else if (i == 2) {
                    board.setOfile2(key); board.setSfile2(saveFileMap.get(key));
                } else if (i == 3) {
                    board.setOfile3(key); board.setSfile3(saveFileMap.get(key));
                }
                i++;
            }

            int result = dao.insert(board);
            if (result == 1) System.out.println("입력성공");

        } catch (Exception e) {
            System.out.println("등록 파일 업로드 에러" + e);
            e.printStackTrace();
        }

        return "redirect:boardList.do";
    }

    /* ===================== 글쓰기 화면 ===================== */
    @GetMapping("/boardWrite.do")
    public String boardWriteForm() {
        return "board/boardWrite";
    }

    /* ===================== 상세 보기 ===================== */
    @GetMapping("/boardView.do")
    public String memberGet3(HttpServletResponse resp, HttpServletRequest req, BoardDTO boardDTO, Model model) {

        String idx = trimOrEmpty(req.getParameter("board_idx"));

        int num = parseIntOrDefault(req.getParameter("num"), 0);

        String boName = "visit";
        String ckName = boName + "-" + idx;
        int result = cookieOneDay(req, model, resp, ckName);

        if (result == 1) {
            int count = dao.board_visitcounter(idx);
            if (count == 1) System.out.println("방문자수 증가");
        }

        if (num == 5) {
            String ckName2 = "good-" + idx;
            int result2 = cookieOneDay(req, model, resp, ckName2);
            if (result2 == 1) dao.board_good_count(idx);
        } else if (num == 6) {
            String ckName3 = "worse-" + idx;
            int result3 = cookieOneDay(req, model, resp, ckName3);
            if (result3 == 1) dao.board_worse_count(idx);
        }

        String searchField = trimOrEmpty(req.getParameter("searchField"));
        String searchKeyword = trimOrEmpty(req.getParameter("searchKeyword"));
        String pageNum = trimOrEmpty(req.getParameter("pageNum"));

        Map<String, String> maps = new HashMap<>();
        maps.put("searchField", searchField);
        maps.put("searchKeyword", searchKeyword);
        maps.put("pageNum", pageNum);

        boardDTO = dao.view(boardDTO);
        boardDTO.setBoard_content(boardDTO.getBoard_content().replace("\r\n", "<br>"));

        String cate1 = fileCheckName(boardDTO.getOfile1());
        String cate2 = fileCheckName(boardDTO.getOfile2());
        String cate3 = fileCheckName(boardDTO.getOfile3());

        if (cate1 != null) maps.put("cate1", cate1);
        if (cate2 != null) maps.put("cate2", cate2);
        if (cate3 != null) maps.put("cate3", cate3);

        ArrayList<CommentDTO> comment_list = dao.comment_select(idx);

        model.addAttribute("maps", maps);
        model.addAttribute("boardDTO", boardDTO);
        model.addAttribute("lists", comment_list);

        return "board/boardView";
    }

    /* ===================== 수정창 (작성자 또는 ADMIN) ===================== */
    @GetMapping("/boardEdit.do")
    public String memberGet4(BoardDTO boardDTO, Model model, HttpServletRequest req) {
        String me = currentUser();
        boolean admin = isAdmin(SecurityContextHolder.getContext().getAuthentication());

        BoardDTO view = dao.view(boardDTO);
        if (!admin && (view == null || !me.equals(view.getWriter()))) {
            return "redirect:boardView.do?board_idx=" + boardDTO.getBoard_idx() + "&error=forbidden";
        }

        Map<String, String> maps = new HashMap<>();
        String cate1 = fileCheckName(view.getOfile1());
        String cate2 = fileCheckName(view.getOfile2());
        String cate3 = fileCheckName(view.getOfile3());
        if (cate1 != null) maps.put("cate1", cate1);
        if (cate2 != null) maps.put("cate2", cate2);
        if (cate3 != null) maps.put("cate3", cate3);

        model.addAttribute("maps", maps);
        model.addAttribute("boardDTO", view);
        return "board/boardEdit";
    }

    /* ===================== 수정하기 (작성자 또는 ADMIN) ===================== */
    @PostMapping("/boardEdit.do")
    public String memberPost4(HttpServletRequest req, BoardDTO boardDTO, Model model) {

        String board_idx = trimOrEmpty(req.getParameter("board_idx"));
        String board_title = trimOrEmpty(req.getParameter("board_title"));
        String board_content = trimOrEmpty(req.getParameter("board_content"));

        String me = currentUser();
        boolean admin = isAdmin(SecurityContextHolder.getContext().getAuthentication());
        if (!admin && !isBoardOwner(board_idx, me)) {
            return "redirect:boardView.do?board_idx=" + board_idx + "&error=forbidden";
        }

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
            Map<String, String> saveFileMap = new HashMap<>();

            Collection<Part> parts = req.getParts();
            for (Part part : parts) {
                if (!"ofile".equals(part.getName())) continue;

                String partHeader = part.getHeader("content-disposition");
                String[] phArr = partHeader.split("filename=");
                String originalFileName = phArr[1].trim().replace("\"", "");
                String savedFileName = "";

                if (!originalFileName.isEmpty()) {
                    part.write(uploadDir + File.separator + originalFileName);
                    savedFileName = MyFunctions.renameFile(uploadDir, originalFileName);
                }
                saveFileMap.put(originalFileName, savedFileName);
            }

            BoardDTO board = dao.view(boardDTO);
            for (String key : saveFileMap.keySet()) {
                if (board.getOfile1() == null || board.getOfile1().isEmpty()) {
                    board.setOfile1(key); board.setSfile1(saveFileMap.get(key)); board.setNum(1); board.setBoard_idx(board_idx);
                    dao.updateFile(board);
                } else if (board.getOfile2() == null || board.getOfile2().isEmpty()) {
                    board.setOfile2(key); board.setSfile2(saveFileMap.get(key)); board.setNum(2); board.setBoard_idx(board_idx);
                    dao.updateFile(board);
                } else if (board.getOfile3() == null || board.getOfile3().isEmpty()) {
                    board.setOfile3(key); board.setSfile3(saveFileMap.get(key)); board.setNum(3); board.setBoard_idx(board_idx);
                    dao.updateFile(board);
                }
            }

            int result = dao.updateTitleContent(board_title, board_content, board_idx);
            if (result == 1) System.out.println("타이틀 컨텐츠 수정성공");
        } catch (Exception e) {
            System.out.println("등록 파일 업로드 에러" + e);
            e.printStackTrace();
        }

        return "redirect:boardList.do";
    }

    /* ===================== 파일 다운로드 ===================== */
    @GetMapping("/boardDownload.do")
    public void memberGet5(HttpServletRequest req, HttpServletResponse res) {
        String ofile1 = trimOrEmpty(req.getParameter("ofile1"));
        String sfile1 = trimOrEmpty(req.getParameter("sfile1"));

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
            FileUtil.download(req, res, uploadDir, sfile1, ofile1);
        } catch (Exception e) {
            System.out.println("download 에러 발생" + e);
            e.printStackTrace();
        }
    }

    /* ===================== 글 삭제 (작성자 또는 ADMIN) ===================== */
    @PostMapping("/boardDelete.do")
    public String member6(HttpServletRequest req) {

        String board_idx = trimOrEmpty(req.getParameter("board_idx"));
        String sfile1 = trimOrEmpty(req.getParameter("sfile1"));
        String sfile2 = trimOrEmpty(req.getParameter("sfile2"));
        String sfile3 = trimOrEmpty(req.getParameter("sfile3"));

        String me = currentUser();
        boolean admin = isAdmin(SecurityContextHolder.getContext().getAuthentication());
        if (!admin && !isBoardOwner(board_idx, me)) {
            return "redirect:boardView.do?board_idx=" + board_idx + "&error=forbidden";
        }

        // 자식 먼저 삭제
        dao.comment_write_del(board_idx);
        int result = dao.delete(board_idx);
        if (result == 1) System.out.println("삭제 성공");

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
            if (!sfile1.isEmpty()) FileUtil.deleteFile(req, uploadDir, sfile1);
            if (!sfile2.isEmpty()) FileUtil.deleteFile(req, uploadDir, sfile2);
            if (!sfile3.isEmpty()) FileUtil.deleteFile(req, uploadDir, sfile3);
        } catch (Exception e) {
            System.out.println("uploadDir 에러 발생 : " + e);
            e.printStackTrace();
        }

        return "redirect:boardList.do";
    }

    /* ===================== 파일 하나 삭제 (작성자 또는 ADMIN) ===================== */
    @PostMapping("/boardDeleteFileOne.do")
    public String deleteFileOne(HttpServletRequest req) {

        String board_idx = trimOrEmpty(req.getParameter("board_idx"));
        String sfile = trimOrEmpty(req.getParameter("sfile"));
        String imgCount = trimOrEmpty(req.getParameter("imgCount"));

        String me = currentUser();
        boolean admin = isAdmin(SecurityContextHolder.getContext().getAuthentication());
        if (!admin && !isBoardOwner(board_idx, me)) {
            return "redirect:boardView.do?board_idx=" + board_idx + "&error=forbidden";
        }

        int result = dao.deleteFileOne(board_idx, imgCount);
        if (result == 1) System.out.println("파일 업데이트 성공");

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
            if (!sfile.isEmpty()) FileUtil.deleteFile(req, uploadDir, sfile);
        } catch (Exception e) {
            System.out.println("uploadDir 에러 발생 : " + e);
            e.printStackTrace();
        }

        return "redirect:boardEdit.do?board_idx=" + board_idx;
    }

    /* ===================== 파일 전체 삭제 (작성자 또는 ADMIN) ===================== */
    @PostMapping("/boardDeleteFileAll.do")
    public String boardDeleteFileAll(HttpServletRequest req) {

        String board_idx = trimOrEmpty(req.getParameter("board_idx"));

        String me = currentUser();
        boolean admin = isAdmin(SecurityContextHolder.getContext().getAuthentication());
        if (!admin && !isBoardOwner(board_idx, me)) {
            return "redirect:boardView.do?board_idx=" + board_idx + "&error=forbidden";
        }

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoard_idx(board_idx);
        BoardDTO boardDTO2 = dao.view(boardDTO);

        String sfile1 = trimOrEmpty(boardDTO2.getSfile1());
        String sfile2 = trimOrEmpty(boardDTO2.getSfile2());
        String sfile3 = trimOrEmpty(boardDTO2.getSfile3());

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();
            if (!sfile1.isEmpty()) FileUtil.deleteFile(req, uploadDir, sfile1);
            if (!sfile2.isEmpty()) FileUtil.deleteFile(req, uploadDir, sfile2);
            if (!sfile3.isEmpty()) FileUtil.deleteFile(req, uploadDir, sfile3);
        } catch (Exception e) {
            System.out.println("파일 전체 삭제 오류" + e);
            e.printStackTrace();
        }

        int result = dao.boardDeleteFileAll(board_idx);
        if (result == 1) System.out.println("전체삭제 성공");

        return "redirect:boardEdit.do?board_idx=" + board_idx;
    }

    /* ===================== 파일 종류 분류 ===================== */
    public String fileCheckName(String fileName) {
        String cate;
        String ext;

        if (fileName == null || fileName.isEmpty()) {
            ext = "etc";
        } else {
            int dot = fileName.lastIndexOf(".");
            ext = (dot >= 0 ? fileName.substring(dot + 1) : fileName).toLowerCase();
        }

        String[] imgExts = { "jpg", "jpeg", "gif", "png", "bmp", "webp" };
        String[] videoExts = { "avi", "mp4", "mov", "wmv", "flv", "mkv" };
        String[] audioExts = { "mp3", "wav", "ogg", "aac", "flac" };

        List<String> imgList = new ArrayList<>(Arrays.asList(imgExts));
        List<String> videoList = new ArrayList<>(Arrays.asList(videoExts));
        List<String> audioList = new ArrayList<>(Arrays.asList(audioExts));

        if (imgList.contains(ext)) cate = "img";
        else if (videoList.contains(ext)) cate = "video";
        else if (audioList.contains(ext)) cate = "audio";
        else cate = "etc";

        return cate;
    }

    /* ===================== 쿠키 하루 ===================== */
    public int cookieOneDay(HttpServletRequest req, Model model,
                            HttpServletResponse resp, String ckName) {

        int result = 0;

        if (ckName == null || ckName.trim().isEmpty()) {
            ckName = "visit";
        } else {
            ckName = ckName.trim().replaceAll("[^a-zA-Z0-9\\-_]", "");
            if (ckName.isEmpty()) ckName = "visit";
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

    /* ===================== 수업자료 데모 ===================== */
    @GetMapping("/fileUpload.do")
    public String fileUpload() {
        return "fileUpload";
    }

    @PostMapping("/uploadProcess.do")
    public String uploadProcess(HttpServletRequest req, Model model) {

        try {
            String uploadDir = ResourceUtils.getFile("classpath:static/uploads/").toPath().toString();

            Part part = req.getPart("ofile");
            String partHeader = part.getHeader("content-disposition");
            String[] phArr = partHeader.split("filename=");
            String originalFileName = phArr[1].trim().replace("\"", "");

            if (!originalFileName.isEmpty()) {
                part.write(uploadDir + File.separator + originalFileName);
            }

            String savedFileName = MyFunctions.renameFile(uploadDir, originalFileName);

            model.addAttribute("originalFileName", originalFileName);
            model.addAttribute("savedFileName", savedFileName);
            model.addAttribute("title", trimOrEmpty(req.getParameter("title")));
            model.addAttribute("cate", req.getParameterValues("cate"));

        } catch (Exception e) {
            System.out.println("업로드 실패" + e);
        }

        return "fileUploadOk";
    }
}
