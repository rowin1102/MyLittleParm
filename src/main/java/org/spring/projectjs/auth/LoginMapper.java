package org.spring.projectjs.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.projectjs.jdbc.MemberDTO;


@Mapper
public interface LoginMapper {
	MemberDTO selectIdAndPassword(@Param("userId") String userId, @Param("password") String password);
}
