package com.airbng.consumer.service;

import com.airbng.consumer.exception.LockerException;
import com.airbng.consumer.exception.MemberException;
import com.airbng.consumer.exception.ZzimException;
import com.airbng.consumer.mappers.LockerMapper;
import com.airbng.consumer.mappers.MemberMapper;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import com.airbng.consumer.mappers.ZzimMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class ZzimServiceImpl implements ZzimService {

    private final ZzimMapper zzimMapper;
    private final MemberMapper memberMapper;
    private final LockerMapper lockerMapper;

    @Override
    @Transactional
    public BaseResponseStatus toggleZzim(Long memberId, Long lockerId) {
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
            zzimMapper.deleteZzim(memberId, lockerId); // zzim_count 감소
            zzimMapper.decreaseZzimCount(lockerId);
            return SUCCESS_DELETE_ZZIM; // 취소됨
        }

        // 찜 등록 (중복 insert 예외 방지)
        try {
            zzimMapper.insertZzim(memberId, lockerId);
            zzimMapper.increaseZzimCount(lockerId); // zzim_count 증가
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
