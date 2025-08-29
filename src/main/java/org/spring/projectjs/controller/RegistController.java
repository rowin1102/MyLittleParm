package org.spring.projectjs.controller;

import java.util.HashMap;
import java.util.Map;

import org.spring.projectjs.jdbc.MemberDTO;
import org.spring.projectjs.jdbc.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class RegistController {
	
	@Autowired
	private MemberService memberService;
	
	@GetMapping("/regist.do")
	public String registGet() {
		return "regist/regist";
	}
	
	@PostMapping("/regist.do")
	public String registPost(MemberDTO memberDTO) {
		memberService.register(memberDTO);
		
		return "main";
	}
	@PostMapping("/test")
	public String mainGet() {
		return "test";
	}
	
	@PostMapping("/checkDuplicate.do") @ResponseBody
    public Map<String, Object> checkDuplicate(@RequestParam("user_id") String userId) {
        Map<String, Object> response = new HashMap<>();
        
        if (userId == null || userId.trim().isEmpty()) {
            response.put("available", false);
            response.put("message", "아이디를 입력해주세요.");
            return response;
        }
        
        try {
            int count = memberService.checkDuplicateId(userId.trim());
            
            if (count > 0) {
                response.put("available", false);
                response.put("message", "이미 사용중인 아이디입니다.");
            } else {
                response.put("available", true);
                response.put("message", "사용가능합니다.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("available", false);
            response.put("message", "중복확인 중 오류가 발생했습니다.");
        }
        
        return response;
    }
	
	@PostMapping("/checkNicknameDuplicate.do")
	@ResponseBody
	public Map<String, Object> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
	    Map<String, Object> response = new HashMap<>();
	    
	    if (nickname == null || nickname.trim().isEmpty()) {
	        response.put("available", false);
	        response.put("message", "닉네임을 입력해주세요.");
	        return response;
	    }

	    try {
	        int count = memberService.checkDuplicateNickname(nickname.trim());
	        if (count > 0) {
	            response.put("available", false);
	            response.put("message", "이미 사용중인 닉네임입니다.");
	        } else {
	            response.put("available", true);
	            response.put("message", "사용가능한 닉네임입니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("available", false);
	        response.put("message", "중복확인 중 오류가 발생했습니다.");
	    }

	    return response;
	}
}
