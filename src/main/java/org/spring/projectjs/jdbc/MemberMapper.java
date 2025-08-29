package org.spring.projectjs.jdbc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.spring.projectjs.map.MemberLocation;

@Mapper
public interface MemberMapper {
    MemberLocation findByUserId(@Param("userId") String userId);
}
