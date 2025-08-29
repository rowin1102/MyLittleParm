package org.spring.projectjs.chatting.domain;

import lombok.Data;

@Data
public class Meeting {
    private Long meetingId;
    private Long roomId;
    private Long creatorMemberIdx;
    private Double lat;
    private Double lng;
    private String address;
    private java.time.LocalDateTime createdAt;
}
