package com.airbng.mappers;

import com.airbng.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);

    boolean findByEmail(String email);

    boolean findByNickname(String nickname);

    boolean findByPhone(String phone);
  
    Member findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    boolean isExistMember(Long memberId);

}
