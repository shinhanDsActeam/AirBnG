package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.SessionException;
import com.airbng.common.exception.ZzimException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.mappers.LockerMapper;
import com.airbng.mappers.MemberMapper;
import com.airbng.mappers.ZzimMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class ZzimServiceImpl implements ZzimService {

    private final ZzimMapper zzimMapper;
    private final MemberMapper memberMapper;
    private final LockerMapper lockerMapper;

    @Override
    @Transactional
    public BaseResponseStatus toggleZzim(Long sessionMemberId, Long memberId, Long lockerId) {
        // 세션의 사용자와 요청된 사용자 ID가 일치하는지 확인
        if (!Objects.equals(sessionMemberId, memberId)) {
            throw new SessionException(SESSION_MISMATCH);
        }

        // 멤버 존재 여부 확인
        if (!memberMapper.isExistMember(memberId)) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }
        // 락커 존재 여부 확인
        if (!lockerMapper.isExistLocker(lockerId)) {
            throw new LockerException(NOT_FOUND_LOCKER);
        }
        // 자기 락커 찜 금지
        if (lockerMapper.isLockerKeeper(lockerId, memberId)) {
            throw new ZzimException(SELF_LOCKER_ZZIM);
        }
        // 찜 존재 여부 확인 후 등록/삭제
        if (zzimMapper.isExistZzim(memberId, lockerId) == 1) {
            zzimMapper.deleteZzim(memberId, lockerId);
            return SUCCESS_DELETE_ZZIM; // 취소됨
        }

        // 찜 등록 (중복 insert 예외 방지)
        try {
            zzimMapper.insertZzim(memberId, lockerId);
            return SUCCESS_INSERT_ZZIM; // 찜 등록됨
        } catch (DuplicateKeyException e) {
            throw new ZzimException(DUPLICATE_ZZIM);
        }
    }

    @Override
    public boolean isExistZzim(Long memberId, Long lockerId) {
        return zzimMapper.isExistZzim(memberId, lockerId) == 1;
    }
}
