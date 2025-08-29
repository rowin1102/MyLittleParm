package org.spring.projectjs.chatting;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleChatRoomDto {
    private Long roomId;
    private String targetUserId;
    private String roomName;
}
