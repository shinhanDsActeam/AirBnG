package com.airbng.mappers;

import com.airbng.domain.Member;
import com.airbng.dto.MemberMyPageResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
    boolean findByEmail(String email);
    boolean findByNickname(String nickname);
    boolean findByPhone(String phone);
    MemberMyPageResult findUserById(@Param("memberId") Long memberId);
}
