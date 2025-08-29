package org.spring.projectjs.map;

import org.spring.projectjs.jdbc.MemberMapper;
import org.springframework.stereotype.Service;

@Service
public class MapService {
	private final MemberMapper memberMapper;
	private final KakaoLocalDao kakaoLocalDao;

	public MapService(MemberMapper memberMapper, KakaoLocalDao kakaoLocalDao) {
		this.memberMapper = memberMapper;
		this.kakaoLocalDao = kakaoLocalDao;
	}

	public Result getLatLngByUserId(String userId) {
		// 1) DB에서 주소 조회
		MemberLocation ml = null;
		try {
//	        ml = memberMapper.findByUserId("test12");
			ml = memberMapper.findByUserId(userId);
		} catch (Exception e) {
			e.printStackTrace();
			// 로그만 찍고 안전 폴백
			// log.warn("DB 조회 실패", e);
		}
		System.out.println(ml);
		String address = (ml != null && ml.getAddress() != null && !ml.getAddress().isBlank()) ? ml.getAddress()
				: "서울특별시 중구 세종대로 110"; // 폴백 주소

		// 2) 주소 → 좌표
		double lat = 37.5665, lng = 126.9780; // 폴백 좌표
		try {
			var geo = kakaoLocalDao.geocode(address);
			if (geo != null) {
				lat = geo.lat;
				lng = geo.lng;
			}
		} catch (Exception ignore) {
		}

		return new Result(address, lat, lng);
	}

	public static class Result {
		public final String address;
		public final double lat, lng;

		public Result(String address, double lat, double lng) {
			this.address = address;
			this.lat = lat;
			this.lng = lng;
		}
	}
	@lombok.Data
    public static class MeetingCreateReq {
        private Long   roomId;
        private Double lat;
        private Double lng;
        private String address;
    }

    @lombok.Data
    public static class MeetingRes {
        private Long   meetingId;
        private Long   roomId;
        private Double lat;
        private Double lng;
        private String address;
        private String createdAt;
    }
}
