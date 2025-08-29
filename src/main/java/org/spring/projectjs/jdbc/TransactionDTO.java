package org.spring.projectjs.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionDTO {
	//수정에서 사용
	private int num;
	
	private int transaction_idx;
	private java.sql.Date transaction_date;
	private String transaction_istrans;
	private int transaction_price;
	private String transaction_title;
	private String transaction_content;
	private String ofile1;
	private String sfile1;
	private String ofile2;
	private String sfile2;
	private String ofile3;
	private String sfile3;
    private String writer;
	//private int  member_idx_trans;
	//MEMBER_IDX_TRANS
	
}
