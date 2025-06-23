package com.airbng.mappers;

import com.airbng.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
    boolean findByEmail(String email);
    boolean findByNickname(String nickname);
    boolean findByPhone(String phone);
    boolean findById(Long memberId);
}
