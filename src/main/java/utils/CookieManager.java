package utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieManager {
	
	public static void makeCookie(HttpServletResponse resp, String cName,
			String cValue, int cTime) {
		
		Cookie cookie = new Cookie(cName, cValue);
		cookie.setPath("/");
		cookie.setMaxAge(cTime);
		resp.addCookie(cookie);
		
	}
	
	public static String readCookie(HttpServletRequest req, String cName) {
		String cookieValue = "";
		
		Cookie[] cookies = req.getCookies();
		
		if( cookies != null ) {
			for(Cookie c: cookies) {
				String cookieName = c.getName();
				if( cookieName.equals(cName) ) {
					cookieValue = c.getValue();
					System.out.println("cookieValue = " + cookieValue);
				}
			}
		}
		
		return cookieValue;		
	}
	
	public static void deleteCookie(HttpServletResponse resp, String cName) {
		makeCookie(resp, cName, "", 0);
	}	
	
}
