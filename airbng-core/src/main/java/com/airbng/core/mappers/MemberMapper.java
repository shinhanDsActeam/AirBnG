package com.airbng.core.mappers;

import com.airbng.core.domain.Member;
import com.airbng.core.dto.MemberMyPageResponse;
import com.airbng.core.dto.MemberUpdateRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
  
    boolean findByEmail(String email);
  
    boolean findByNickname(String nickname);
  
    boolean findByPhone(String phone);
  
    boolean findById(Long memberId);
  
    MemberMyPageResponse findUserById(@Param("memberId") Long memberId);

    Member findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    boolean isExistMember(Long memberId);

    Member findMemberByEmail(@Param("email") String email);

    int updateUserById(MemberUpdateRequest memberUpdateRequest);
}
