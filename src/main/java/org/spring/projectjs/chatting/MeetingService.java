package org.spring.projectjs.chatting;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.spring.projectjs.chatting.domain.Meeting;
import org.springframework.stereotype.Service;

import lombok.Data;


@Service
public class MeetingService {
	
	// 간단한 메모리 저장: 방(roomId)별 최신 미팅 1건 보관
	private final AtomicLong seq = new AtomicLong(0L);
	private final ConcurrentMap<Long, Meeting> latestByRoom = new ConcurrentHashMap<>();
	
	public Meeting createMeeting(Long roomId, Long creatorMemberIdx, double lat, double lng, String address) {
	 if (roomId == null || creatorMemberIdx == null) {
	   throw new IllegalArgumentException("roomId/creatorMemberIdx is null");
	 }
	
	 Meeting m = new Meeting();
	 m.setMeetingId(seq.incrementAndGet());
	 m.setRoomId(roomId);
	 m.setCreatorMemberIdx(creatorMemberIdx);
	 m.setLat(lat);
	 m.setLng(lng);
	 m.setAddress(address);
	 m.setCreatedAt(LocalDateTime.now());
	
	 latestByRoom.put(roomId, m);
	 return m;
	}
	
	public Meeting findLatest(Long roomId) {
	 return latestByRoom.get(roomId);
	}

}

