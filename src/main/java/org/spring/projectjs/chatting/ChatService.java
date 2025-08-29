package org.spring.projectjs.chatting;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.chatting.domain.Message;
import org.spring.projectjs.chatting.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

  private final MessageRepository messageRepo;

  /** 최근 50개: 화면에 자연스럽게 보이도록 messageNum ASC로 반환 */
  public List<Message> loadRecent(Long roomId) {
	  List<Message> rows = messageRepo.findTop50ByRoomIdOrderByMessageNumDesc(roomId); // 최신→과거
	  Collections.reverse(rows); // 과거→최신(오름차순)
	  return rows;
  }

  /** 무한스크롤: 특정 id 이전 50개 (최신에서 과거로), 클라에서 prepend 시 ASC로 다시 정렬 권장 */
  public List<Message> loadBefore(Long roomId, Long beforeId) {
    return messageRepo.findTop50ByRoomIdAndIdLessThanOrderByIdDesc(roomId, beforeId);
  }


  
}
