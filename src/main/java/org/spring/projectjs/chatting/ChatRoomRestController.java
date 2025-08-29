// src/main/java/org/spring/projectjs/chatting/ChatRoomRestController.java
package org.spring.projectjs.chatting;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.chatting.domain.ChatRoom;
import org.spring.projectjs.chatting.mapper.ChatRoomMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomRestController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomMapper memberMapper;

    @PostMapping("/room/open")
    public Map<String, Object> openRoom(
            @AuthenticationPrincipal(expression = "username") String loginId, // 내 userId
            @RequestParam("writerUserId") String writerUserId                 // 프런트에서 전달
    ) {
        if (loginId == null || loginId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (writerUserId == null || writerUserId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "writerUserId 필요");
        }

        // 내 member_idx
        Long myMemberIdx = SecurityUserUtil.resolveMyMemberIdx(loginId, memberMapper);

        // 상대 member_idx (검증용)
        Long otherMemberIdx = memberMapper.findMemberIdxByUserId(writerUserId);
        if (otherMemberIdx == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상대 사용자 없음: " + writerUserId);
        }
        if (myMemberIdx.equals(otherMemberIdx)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인과의 1:1 채팅은 생성할 수 없습니다.");
        }

        // 방 제목 = CHAT_ROOM_NAME (항상 같은 규칙: 두 ID를 정렬)
        String left  = (loginId.compareToIgnoreCase(writerUserId) <= 0) ? loginId      : writerUserId;
        String right = (loginId.compareToIgnoreCase(writerUserId) <= 0) ? writerUserId : loginId;
        String roomTitle = left + "-" + right + "채팅방";

        ChatRoom room = chatRoomService.openOrGetByFixedName(roomTitle, myMemberIdx);
        return Map.of(
            "roomId",   room.getRoomId(),
            "roomName", room.getRoomName() // DB에 저장된 제목 그대로
        );
    }
    private String extractTargetUserId(String roomName, String loginId) {
        // roomName 형식: tnfla1130-springUser01채팅방
        if (roomName == null || !roomName.contains("-")) return "unknown";
        String[] parts = roomName.replace("채팅방", "").split("-");
        if (parts.length < 2) return "unknown";
        return parts[0].equalsIgnoreCase(loginId) ? parts[1] : parts[0];
    }
 // ChatRoomRestController.java (추가)
    @GetMapping("/rooms")
    public List<SimpleChatRoomDto> listRooms(
            @AuthenticationPrincipal(expression = "username") String loginId
    ) {
        if (loginId == null || loginId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // 현재 로그인 아이디가 포함된 방만 조회
        List<ChatRoom> rooms = chatRoomService.findRoomsByLoginId(loginId);

        // 방 이름 형식:  left-right채팅방  (left/right = userId)
        return rooms.stream()
        		 .map(r -> new SimpleChatRoomDto(
        	                r.getRoomId(),
        	                extractTarget(loginId, r.getRoomName()),
        	                r.getRoomName()
        	        ))
        	        .toList();
    }

    /** roomName에서 상대 userId만 추출 */
    private String extractTarget(String myId, String roomName) {
        if (roomName == null) return "";
        String base = roomName.replace("채팅방", "");
        String[] parts = base.split("-");
        if (parts.length < 2) return base.trim();
        // 내가 왼쪽이면 오른쪽, 내가 오른쪽이면 왼쪽
        return parts[0].equalsIgnoreCase(myId) ? parts[1] : parts[0];
    }
    
    @DeleteMapping("/room/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoom(
            @AuthenticationPrincipal(expression = "username") String loginId,
            @PathVariable("roomId") Long roomId
    ) {
    	
        if (loginId == null || loginId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Long myMemberIdx = SecurityUserUtil.resolveMyMemberIdx(loginId, memberMapper);
        System.out.println("[DELETE] roomId=" + roomId + " myMemberIdx=" + myMemberIdx);
        chatRoomService.DeleteChatRoom(roomId, myMemberIdx);
    }

}
