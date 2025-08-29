package org.spring.projectjs.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

	
	// 커스텀 로그인 페이지 매핑
	@GetMapping("/myLogin.do")
	public String login1(Principal principal, Model model,
			@RequestParam(value = "error", required = false) String error) {
		
		
		System.out.println("error : " + error);
		
		if (principal != null) {
	        model.addAttribute("user_id", principal.getName());
	    }
		
		if (error != null) {
	        String errorMsg = null;
	        
	        if ("1".equals(error)) errorMsg = "아이디나 비밀번호가 맞지 않습니다. 다시 확인해주세요(1)";
	        else if ("2".equals(error)) errorMsg = "아이디나 비밀번호가 맞지 않습니다. 다시 확인해주세요(2)";
	        else errorMsg = "알 수 없는 에러가 발생했습니다.";
	        
	        model.addAttribute("errorMsg", errorMsg);
	    }
		
		return "auth/login";
	}
}
