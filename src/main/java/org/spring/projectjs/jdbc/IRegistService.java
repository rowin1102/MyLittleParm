package org.spring.projectjs.jdbc;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IRegistService {

	MemberDTO selectByUserId(String userId);

	int insert(MemberDTO memberDTO);

	int checkDuplicateId(@Param("userId") String userId);

	int checkDuplicateNickname(@Param("nickname") String nickname);

	// ── 사전 인증(공용) 템프 테이블
	int upsertEmailVerifyTemp(@Param("email") String email, @Param("token") String token);

	EmailVerifyTempDTO findTempByToken(@Param("token") String token);

	int markTempVerified(@Param("email") String email);

	String findTempVerifiedFlag(@Param("email") String email); // 'Y'/'N' 또는 null

	int deleteTemp(@Param("email") String email);

	// ── 아이디 찾기/비번 재설정 (분리 컬럼 매칭)
	List<String> findUserIdsByLocalAndDomain(@Param("local") String local, @Param("domain") String domain);

	boolean existsByUserIdAndDomain(@Param("userId") String userId, @Param("local") String local,
			@Param("domain") String domain);

	int updatePassword(@Param("userId") String userId, @Param("encodedPw") String encodedPw);
}
