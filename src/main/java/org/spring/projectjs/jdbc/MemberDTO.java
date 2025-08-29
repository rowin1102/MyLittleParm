package org.spring.projectjs.jdbc;

import lombok.Data;

@Data
public class MemberDTO{
	
    private int member_idx;
    private String user_id;
    private String password;
    private String email;
    private String domain;
    private String phone;
    private String nickname;
    private java.sql.Date postdate;
    private String member_auth;
    private int point;
    private String address;
    private String detailaddress;
    private String postcode;
    
    private String email_verified;
}
