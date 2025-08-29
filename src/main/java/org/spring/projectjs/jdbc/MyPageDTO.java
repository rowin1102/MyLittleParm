package org.spring.projectjs.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MyPageDTO {

	private int member_idx;
	private String user_id;
	private String password;
	private String email;
	private String phone;
	private int point;
	private String nickname;
	private java.sql.Date postdate;
	private String member_auth;
	private String domain;
	private String address;
    private String detailaddress;
	
}
