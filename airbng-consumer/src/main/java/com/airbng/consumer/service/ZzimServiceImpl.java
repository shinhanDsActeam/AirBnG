package com.airbng.consumer.service;

import com.airbng.common.base.BaseStatus;
import com.airbng.consumer.domain.Locker;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.domain.Zzim;
import com.airbng.consumer.exception.LockerException;
import com.airbng.consumer.exception.MemberException;
import com.airbng.consumer.exception.ZzimException;
import com.airbng.consumer.repository.LockerRepository;
import com.airbng.consumer.repository.MemberRepository;
import com.airbng.consumer.repository.ZzimRepository;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class ZzimServiceImpl implements ZzimService {

    private final MemberRepository memberRepository;
    private final LockerRepository lockerRepository;
    private final ZzimRepository zzimRepository;

    @Override
    @Transactional
    public BaseResponseStatus toggleZzim(Long memberId, Long lockerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
        Locker locker = lockerRepository.findById(lockerId).
                orElseThrow(() -> new LockerException(NOT_FOUND_LOCKER));

        // 자기 락커 찜 금지
        if(locker.getKeeper().getMemberId().equals(member.getMemberId())){
            throw new ZzimException(SELF_LOCKER_ZZIM);
        }

        // 찜 존재 여부 확인 후 등록/삭제
        Optional<Zzim> zzim = zzimRepository.findByMemberIdAndLockerId(memberId, lockerId);

        if (zzim.isPresent()) {
            zzimRepository.delete(zzim.get());
            locker.decreaseZzimCount();
            return SUCCESS_DELETE_ZZIM; // 취소됨
        }

        // 찜 등록
        Zzim newZzim = Zzim.builder()
                .member(member)
                .locker(locker)
                .status(BaseStatus.ACTIVE)
                .build();
        zzimRepository.save(newZzim);
        locker.increaseZzimCount();
        return SUCCESS_INSERT_ZZIM; // 찜 등록됨
    }

    @Override
    public boolean isExistZzim(Long memberId, Long lockerId) {
        return zzimRepository.findByMemberIdAndLockerId(memberId, lockerId).isPresent();
    }
}
