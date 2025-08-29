package org.spring.projectjs.auth;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class MyAuthFailureHandler implements AuthenticationFailureHandler {
	
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException exp)
	        throws IOException, ServletException {
	    
		String errorCode = "1";
	    if (exp instanceof InternalAuthenticationServiceException) {
	        errorCode = "2";
	    }

	    res.sendRedirect(req.getContextPath() + "/myLogin.do?error=" + errorCode);
		
	}
	
	public void loginFailureCnt(String username) {
		System.out.println("요청 아이디:" + username);
	}
}
