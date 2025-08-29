package org.spring.projectjs.jdbc;

import lombok.Data;

@Data
public class EmailVerifyTempDTO {
	private String email;
	private String token;
	private java.sql.Timestamp expires_at;
	private String verified;
	private java.sql.Timestamp verified_at;
}
