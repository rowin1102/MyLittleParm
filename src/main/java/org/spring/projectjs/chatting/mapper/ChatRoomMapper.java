// src/main/java/org/spring/projectjs/chatting/mapper/ChatRoomMapper.java
package org.spring.projectjs.chatting.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.projectjs.chatting.domain.ChatRoom;


@Mapper
public interface ChatRoomMapper {
    void insert(ChatRoom chatRoom);

    ChatRoom findById(@Param("roomId") Long roomId);

    ChatRoom findByName(@Param("roomName") String roomName);
    
    Long findMemberIdxByUserId(String userid);
    
    String findUserIdByMemberIdx(Long memberIdx);
    
    List<ChatRoom> findRoomsByLoginId(@Param("loginId") String loginId);
    
    int existsRoom(Long roomId);

    int deleteMessagesByRoomId(Long roomId);
    int deleteRoomById(Long roomId);
   
}
