package org.spring.projectjs.chatting;

import org.spring.projectjs.chatting.domain.Message;
import org.spring.projectjs.chatting.repository.MessageRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    @Transactional
    public Message send(Long roomId, Long senderMemberIdx, String content) {
        long nextNum = messageRepository.findTopByRoomIdOrderByMessageNumDesc(roomId)
                .map(m -> m.getMessageNum() + 1)
                .orElse(1L);

        Message m = new Message();
        m.setRoomId(roomId);
        m.setSenderMemberId(senderMemberIdx);
        m.setContent(content);
        m.setMessageNum(nextNum);
        // isRead/createdAt은 @PrePersist에서 기본값 세팅
        return messageRepository.save(m);
    }
}
