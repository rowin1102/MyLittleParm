// src/main/java/org/spring/projectjs/chatting/ChatEnterController.java
package org.spring.projectjs.chatting;

import lombok.RequiredArgsConstructor;
import org.spring.projectjs.chatting.domain.ChatRoom;
import org.spring.projectjs.chatting.mapper.ChatRoomMapper;
import org.spring.projectjs.map.MapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class chattingController {

    private final ChatRoomService chatRoomService;
    private final TransactionQueryPort txQueryPort; // 글쓴이 member_idx 조회
    private final ChatRoomMapper memberMapper;
    
    private final MapService mapService;

    @Value("${kakao.js.key}")
    private String kakaoJsKey;

    @GetMapping("/chat.do")
    public String enterChat(
            @RequestParam(value = "roomId",          required = false) Long roomId,
            @RequestParam(value = "writerMemberIdx", required = false) Long writerMemberIdx,
            @RequestParam(value = "transaction_idx", required = false) Integer transactionIdx,
            @AuthenticationPrincipal(expression = "username") String loginId,
            Model model
    ) {
    	// 1) 내 member_idx (username -> member_idx)
    	if (loginId == null || loginId.isBlank()) {
    		return "redirect:/myLogin.do";
    	}
    	Long myMemberIdx = SecurityUserUtil.resolveMyMemberIdx(loginId, memberMapper);
    	model.addAttribute("myMemberIdx",myMemberIdx);
    	// 공통: 카카오 JS 키는 항상 내려줌 (chat.jsp에서 사용)
    	model.addAttribute("kakaoJsKey", kakaoJsKey);
    	
        // 2) roomId가 이미 있으면 그대로 렌더
    	if (roomId != null) {
    	    ChatRoom room = chatRoomService.get(roomId);
    	    model.addAttribute("roomId", room.getRoomId());
    	    model.addAttribute("roomName", room.getRoomName()); // 제목 추가
    	    
    	    injectMapCenterByUserId(model, loginId);
    	    return "chatting/chat";
    	}

        
        // 3) 상대 member_idx 결정 (writerMemberIdx 우선, 없으면 거래글로부터 조회)
        Long targetMemberIdx = writerMemberIdx;
        if (targetMemberIdx == null && transactionIdx != null) {
            targetMemberIdx = txQueryPort.findWriterMemberIdxByTransactionIdx(transactionIdx);
        }

        // 상대 정보가 없으면 빈 화면 렌더
        if (targetMemberIdx == null) {
            model.addAttribute("roomId", null);
            injectMapCenterByUserId(model, loginId);
            return "chatting/chat";
        }

        // 자기 자신과의 채팅 방지
        if (myMemberIdx.equals(targetMemberIdx)) {
            model.addAttribute("roomId", null);
            injectMapCenterByUserId(model, loginId);
            return "chatting/chat"; // 혹은 오류 처리/알림
        }

        // 4) 방 제목(=CHAT_ROOM_NAME) 생성: 두 userId를 정렬하여 항상 동일하게
        String otherUserId = memberMapper.findUserIdByMemberIdx(targetMemberIdx);
        if (otherUserId == null || otherUserId.isBlank()) {
            // 상대 userId가 없으면 안전하게 빈 화면 (또는 에러 페이지)
            model.addAttribute("roomId", null);
            injectMapCenterByUserId(model, loginId);
            return "chatting/chat";
        }
        else {
        	System.out.println("상대 userId가 없습니다.");
        }

        String left  = (loginId.compareToIgnoreCase(otherUserId) <= 0) ? loginId : otherUserId;
        String right = (loginId.compareToIgnoreCase(otherUserId) <= 0) ? otherUserId : loginId;
        String roomTitle = left + "-" + right + "채팅방"; // == CHAT_ROOM_NAME

        // 5) 방 생성/조회 후 리다이렉트
        ChatRoom room = chatRoomService.openOrGetByFixedName(roomTitle, myMemberIdx);
        return "redirect:/chat.do?roomId=" + room.getRoomId();
    }
    private void injectMapCenterByUserId(Model model, String userId) {
        MapService.Result loc = mapService.getLatLngByUserId(userId);
        model.addAttribute("lat",     loc.lat);
        model.addAttribute("lng",     loc.lng);
        model.addAttribute("address", loc.address);
    }
    
}
