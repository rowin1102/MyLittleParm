package org.spring.projectjs.chatting.mapper;

import lombok.Data;

@Data
public class ChatMessageMapper {
  private Long roomId;
  private Long senderMemberId;
  private String content;
  private Long sentAt;
}
