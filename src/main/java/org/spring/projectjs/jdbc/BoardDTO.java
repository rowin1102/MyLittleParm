package org.spring.projectjs.jdbc;

import lombok.Data;

@Data
public class BoardDTO {
	
	//DAO에 사용할 번호
	private int num;
	
	private String board_idx;
	private String board_title;
	private String board_content;		
	private int board_visitcount;
	private java.sql.Date board_date;
	private int board_id;
	private int board_good;
	private int board_worse;	
	private String ofile1;
	private String sfile1;
	private String ofile2;
	private String sfile2;
	private String ofile3;
	private String sfile3;
    private String writer;
	
	
}
