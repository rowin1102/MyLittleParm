// src/main/java/org/spring/projectjs/chatting/ChatRoomService.java
package org.spring.projectjs.chatting;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.spring.projectjs.chatting.domain.ChatRoom;
import org.spring.projectjs.chatting.mapper.ChatRoomMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomMapper chatRoomMapper;

    // 방 이름 생성 규칙 (유저 idx 두 개를 조합)
    public String makeRoomName(long a, long b) {
        long min = Math.min(a, b);
        long max = Math.max(a, b);
        return "U" + min + "#U" + max;
    }

    @Transactional
    public ChatRoom openOrGetByFixedName(String roomName, Long ownerMemberId) {
    	ChatRoom existing = chatRoomMapper.findByName(roomName);  // 이건 null이 나올 수 있음
        if (existing != null) {
            return existing;
        }

        ChatRoom r = new ChatRoom();
        r.setRoomName(roomName);              // CHAT_ROOM_NAME
        r.setOwnerMemberId(ownerMemberId);
        chatRoomMapper.insert(r);                 // <selectKey>로 roomId 채우기 (앞서 안내한 방식)
        if (r.getRoomId() == null) {              // 혹시라도 DTO에 안 채워졌다면
            r = chatRoomMapper.findByName(roomName);
        }
        if (r == null) {
            throw new IllegalStateException("채팅방 생성에 실패했습니다: " + roomName);
        }
        return r;
    }

    public ChatRoom get(Long roomId) {
         return chatRoomMapper.findById(roomId);

    }

    public Long createRoom(String roomName, Long ownerMemberId) {
        ChatRoom room = new ChatRoom();
        room.setRoomName(roomName);
        room.setOwnerMemberId(ownerMemberId);
        chatRoomMapper.insert(room);  // 방 저장
        return room.getRoomId();
    }
    public List<ChatRoom> findRoomsByLoginId(String loginId) {
        return chatRoomMapper.findRoomsByLoginId(loginId);
    }
    
    
    /** 하드 삭제: 참가자인지 확인 후 방/멤버십/메시지 제거 */
    @Transactional
    public void DeleteChatRoom(Long roomId, Long myMemberIdx) {
    	
        if (chatRoomMapper.existsRoom(roomId)==0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방 없음");
        }

        System.out.println("[DELETE service] roomId=" + roomId + " myMemberIdx=" + myMemberIdx);
        chatRoomMapper.deleteMessagesByRoomId(roomId);
        chatRoomMapper.deleteRoomById(roomId);
    }
}
