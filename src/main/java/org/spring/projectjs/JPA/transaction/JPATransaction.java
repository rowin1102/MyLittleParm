package org.spring.projectjs.JPA.transaction;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "transaction")
public class JPATransaction {

    //일련번호
    @Id
    @SequenceGenerator(
            name = "transaction_seq_gen",
            sequenceName = "SEQ_TRANSACTION_NUM",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq_gen")
    private int transactionIdx;

    //거래일자
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP) //날짜와 시간 전부 가져오기
    private Date transactionDate;

    //거래확인
    @Column(name = "TRANSACTION_ISTRANS", nullable = false)
    private char transactionIsTrans;

    //가격
    @Column(nullable = false)
    private int transactionPrice;

    //제목
    @Column(nullable = false)
    private String transactionTitle;

    //내용
    @Column(nullable = false)
    private String transactionContent;

    //사진들 o=기본 사진이름 s=저장한 사진이름
    private String ofile1;
    private String ofile2;
    private String ofile3;
}
