package org.spring.projectjs.JPA.board;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "BOARD")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JPAboard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_seq_gen")
    @SequenceGenerator(
            name = "board_seq_gen",       // JPA에서 참조할 이름
            sequenceName = "SEQ_BOARD_NUM", // 실제 오라클 시퀀스 이름
            allocationSize = 1             // 시퀀스 증가 단위 (DB랑 맞춰야 함)
    )
    @Column(name = "BOARD_IDX")
    private Long boardIdx;

    @Column(name = "BOARD_TITLE", nullable = false, length = 40)
    private String boardTitle;

    @Column(name = "BOARD_CONTENT", nullable = false, length = 2000)
    private String boardContent;

    @Column(name = "BOARD_VISITCOUNT", nullable = false)
    private Integer VisitCount = 0;

    @CreationTimestamp
    @Column(name = "BOARD_DATE", nullable = false, updatable = false, insertable = false)
    private LocalDateTime boardDate;

    @Column(name = "BOARD_ID")
    private Long boardId;

    @Column(name = "BOARD_GOOD")
    private Integer boardGood = 0;

    @Column(name = "BOARD_WORSE")
    private Integer boardWorse = 0;

    @Column(name = "OFILE1", length = 1000)
    private String ofile1;

    @Column(name = "SFILE1", length = 1000)
    private String sfile1;

    @Column(name = "OFILE2", length = 1000)
    private String ofile2;

    @Column(name = "SFILE2", length = 1000)
    private String sfile2;

    @Column(name = "OFILE3", length = 1000)
    private String ofile3;

    @Column(name = "SFILE3", length = 1000)
    private String sfile3;

    @Column(name = "WRITER", length = 30)
    private String writer;

    @PrePersist
    void prePersist() {
        if (VisitCount == null) VisitCount = 0;
        if (boardGood == null) boardGood = 0;
        if (boardWorse == null) boardWorse = 0;
    }
}
