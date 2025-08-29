package org.spring.projectjs.chatting.repository;

import java.util.Optional;

import org.spring.projectjs.chatting.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	Optional<ChatRoom> findByRoomName(String roomName);
	boolean existsByRoomName(String roomName);
}
