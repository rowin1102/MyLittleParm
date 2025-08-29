package org.spring.projectjs;

import java.util.ArrayList;
import java.util.List;

import org.spring.projectjs.jdbc.BoardDTO;
import org.spring.projectjs.jdbc.IBoardNotice;
// 자유게시판 DAO 인터페이스명/패키지가 다르면 여기 변경
import org.spring.projectjs.jdbc.IBoard;

import org.spring.projectjs.jdbc.ParameterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired(required = false)
    private IBoardNotice noticeDao;   // 공지사항

    @Autowired(required = false)
    private IBoard boardDao;          // 자유게시판

    @GetMapping("/")
    public String main(Model model) {

        // 공지사항 Top 5
        try {
            if (noticeDao != null) {
                ParameterDTO p = new ParameterDTO();
                p.setStart(1);
                p.setEnd(5);
                List<BoardDTO> noticeTop = noticeDao.notice_listPage(p);
                model.addAttribute("noticeTop", noticeTop != null ? noticeTop : new ArrayList<>());
            } else {
                model.addAttribute("noticeTop", new ArrayList<BoardDTO>());
            }
        } catch (Exception e) {
            model.addAttribute("noticeTop", new ArrayList<BoardDTO>());
        }

        // 자유게시판 Top 5 (프로젝트의 DAO/메서드명에 맞게 필요시 변경)
        try {
            if (boardDao != null) {
                ParameterDTO p2 = new ParameterDTO();
                p2.setStart(1);
                p2.setEnd(5);
                List<BoardDTO> freeTop = boardDao.listPage(p2);
                model.addAttribute("freeTop", freeTop != null ? freeTop : new ArrayList<>());
            } else {
                model.addAttribute("freeTop", new ArrayList<BoardDTO>());
            }
        } catch (Exception e) {
            model.addAttribute("freeTop", new ArrayList<BoardDTO>());
        }

        return "main"; // /WEB-INF/views/main.jsp
    }
}
