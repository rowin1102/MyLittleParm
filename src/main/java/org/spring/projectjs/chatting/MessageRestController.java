package org.spring.projectjs.chatting;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.chatting.domain.Message;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageRestController {

  private final ChatService chatService;

  /** 최근 50개 로드 */
  @GetMapping("/{roomId}/recent")
  public List<Message> recent(@PathVariable("roomId") Long roomId,
		  @AuthenticationPrincipal(expression = "username") String loginId) {
	  List<Message> check =chatService.loadRecent(roomId);
    return check;
  }

  /** 무한스크롤 */
  @GetMapping("/{roomId}/before/{beforeId}")
  public List<Message> before(@PathVariable("roomId") Long roomId, @PathVariable("beforeId") Long beforeId) {
    return chatService.loadBefore(roomId, beforeId);
  }
  



}
