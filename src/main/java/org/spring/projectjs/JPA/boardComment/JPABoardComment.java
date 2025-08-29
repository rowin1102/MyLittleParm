// src/main/java/org/spring/projectjs/JPA/App/Entity/BoardComment.java
package org.spring.projectjs.JPA.boardComment;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "BOARDCOMMENT") // Oracle은 대문자 테이블명 권장
@SequenceGenerator(
        name = "seq_board_comment_num",
        sequenceName = "seq_board_comment_num", // DB에 생성 필요 (아래 참고)
        allocationSize = 1
)
public class JPABoardComment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_board_comment_num")
    @Column(name = "COMMENT_IDX")
    private Long commentIdx;

    /** 어떤 게시물(글)에 달린 댓글인지 (외래키 용도) */
    @Column(name = "COMMENT_ID", nullable = false)
    private Long commentId;

    /** 작성일시 (Oracle DATE → TIMESTAMP로 매핑) */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "COMMENT_DATE", nullable = false)
    private Date commentDate;

    @Column(name = "COMMENT_CONTENT", nullable = false, length = 300)
    private String commentContent;

    @Column(name = "WRITER", nullable = false, length = 40)
    private String writer;

    @PrePersist
    void onCreate() {
        if (commentDate == null) commentDate = new Date(); // DB DEFAULT(SYSDATE)와도 호환
    }
}
