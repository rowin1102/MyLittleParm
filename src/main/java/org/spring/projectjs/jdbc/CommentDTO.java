package org.spring.projectjs.jdbc;

import lombok.Data;

@Data
public class CommentDTO {

	private int comment_idx;
	private int comment_id;
	private java.sql.Date comment_date;
	private String comment_content;
	private String writer;	
	
}
