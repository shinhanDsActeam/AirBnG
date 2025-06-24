package com.airbng.mappers;

import com.airbng.domain.Member;
import com.airbng.dto.MemberMyPageRequest;
import com.airbng.dto.MemberMyPageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);

    boolean findByEmail(String email);

    boolean findByNickname(String nickname);

    boolean findByPhone(String phone);

    MemberMyPageResponse findUserById(@Param("memberId") Long memberId);

    Member findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    boolean isExistMember(Long memberId);
}
