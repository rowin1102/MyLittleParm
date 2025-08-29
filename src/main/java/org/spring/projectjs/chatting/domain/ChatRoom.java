// src/main/java/org/spring/projectjs/chatting/domain/ChatRoom.java
package org.spring.projectjs.chatting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CHAT_ROOM")
public class ChatRoom {

    @Id
    @Column(name = "CHAT_ROOM_IDX")
    private Long roomId;

    @Column(name = "CHAT_ROOM_NAME")
    private String roomName;

    @Column(name = "MEMBER_IDX_ROOM")
    private Long ownerMemberId;
}