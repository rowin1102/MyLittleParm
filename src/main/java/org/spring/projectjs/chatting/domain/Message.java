package org.spring.projectjs.chatting.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CHAT_MESSAGE")
@SequenceGenerator(
    name = "CHAT_MESSAGE_NUM_SEQ_GEN",
    sequenceName = "CHAT_MESSAGE_NUM_SEQ",
    allocationSize = 1
)
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHAT_MESSAGE_NUM_SEQ_GEN")
  @Column(name="CHAT_MESSAGE_IDX")
  private Long id;

  @Column(name="CHAT_ROOM_IDX", nullable=false)
  private Long roomId;

  // 방 내부 순번(필수)
  @Column(name="CHAT_MESSAGE_NUM", nullable=false)
  private Long messageNum;

  @Column(name="MEMBER_IDX_MESS", nullable=false)
  private Long senderMemberId;

  @Column(name="CHAT_MESSAGE_CONTENT", length=200)
  private String content;


  @Column(name="CHAT_MESSAGE_DATE")
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    if (createdAt == null) createdAt = LocalDateTime.now();

  }
}
