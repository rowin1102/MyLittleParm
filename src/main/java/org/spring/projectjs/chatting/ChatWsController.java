package org.spring.projectjs.chatting;

import lombok.RequiredArgsConstructor;

import org.spring.projectjs.chatting.domain.Meeting;
import org.spring.projectjs.chatting.domain.Message;
import org.spring.projectjs.chatting.mapper.ChatMessageMapper;
import org.spring.projectjs.chatting.mapper.ChatRoomMapper;
import org.spring.projectjs.map.MapService.MeetingCreateReq;
import org.spring.projectjs.map.MapService.MeetingRes;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import org.spring.projectjs.chatting.SecurityUserUtil;

@Controller
@RequiredArgsConstructor
public class ChatWsController {

  private final MessageService messageService;
  private final SimpMessagingTemplate broker;
  private final MeetingService meetingService;
  private final ChatRoomMapper chatRoomMapper;

  // chat.js -> stomp.send('/app/chat.send', {}, JSON.stringify(payload))
  @MessageMapping("/chat.send")
  public void handle(ChatMessageMapper p,
		  @AuthenticationPrincipal(expression = "username") String loginId) {
	
    // 1) DB 저장
    Message saved = messageService.send(p.getRoomId(), p.getSenderMemberId(), p.getContent());
    System.out.println("saved.getSenderMemberId():"+saved.getSenderMemberId());
    // 2) 브로드캐스트 (chat.js에서 구독하는 경로와 동일)
    ChatMessageMapper out = new ChatMessageMapper();
    out.setRoomId(saved.getRoomId());
    out.setSenderMemberId(saved.getSenderMemberId());
    out.setContent(saved.getContent());
    out.setSentAt(System.currentTimeMillis());
    broker.convertAndSend("/topic/rooms." + saved.getRoomId(), out);
  }
  
  @PostMapping("/api/meetings")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public MeetingRes createMeeting(
      @AuthenticationPrincipal(expression = "username") String loginId,
      @RequestBody MeetingCreateReq req
  ) {
    if (loginId == null || loginId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }
    if (req == null || req.getRoomId() == null || req.getLat() == null || req.getLng() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roomId/lat/lng 누락");
    }

    Long myMemberIdx = SecurityUserUtil.resolveMyMemberIdx(loginId, chatRoomMapper);

    Meeting saved = meetingService.createMeeting(
    	    req.getRoomId(), myMemberIdx, req.getLat(), req.getLng(), req.getAddress()
    	);

    MeetingRes res = new MeetingRes();
    res.setMeetingId(saved.getMeetingId());
    res.setRoomId(saved.getRoomId());
    res.setLat(saved.getLat());
    res.setLng(saved.getLng());
    res.setAddress(saved.getAddress());
    res.setCreatedAt(saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : null);
    return res;
  }
}

