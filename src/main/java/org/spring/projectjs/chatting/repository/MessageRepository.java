package org.spring.projectjs.chatting.repository;

import org.spring.projectjs.chatting.domain.Message;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MessageRepository extends JpaRepository<Message, Long> {
	
	Optional<Message> findTopByRoomIdOrderByMessageNumDesc(Long roomId);
	List<Message> findByRoomIdOrderByMessageNumAsc(Long roomId);	
	
	  /** 방의 최신 N개 메시지 (기본 50개) */
	  List<Message> findTop50ByRoomIdOrderByIdDesc(Long roomId);
	
	  /** 무한스크롤: 특정 id보다 이전(작은) 메시지 50개 */
	  List<Message> findTop50ByRoomIdAndIdLessThanOrderByIdDesc(Long roomId, Long beforeId);
	  
	  List<Message> findTop50ByRoomIdOrderByMessageNumDesc(Long roomId);
	//최근 n개 조회용 (오름차순)
	  List<Message> findTop200ByRoomIdOrderByMessageNumAsc(Long roomId);


}
