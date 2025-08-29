package org.spring.projectjs.chatting;

import org.spring.projectjs.chatting.mapper.ChatRoomMapper;

public class SecurityUserUtil {

    /** username → 내 member_idx 조회 */
    public static Long resolveMyMemberIdx(String username, ChatRoomMapper memberMapper) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("로그인 아이디(username)가 비어 있습니다.");
        }
        Long idx = memberMapper.findMemberIdxByUserId(username);
        if (idx == null) {
            throw new IllegalStateException("사용자 없음: " + username);
        }
        return idx;
    }
}
