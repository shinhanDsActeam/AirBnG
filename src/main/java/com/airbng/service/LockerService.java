package com.airbng.service;

import com.airbng.domain.Locker;
import com.airbng.domain.Location;
import com.airbng.domain.Member;
import com.airbng.dto.LockerDTO;
import com.airbng.mappers.LockerImageMapper;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LockerService {

    private final LockerMapper lockerMapper;
    private final LockerImageMapper lockerImageMapper;

    @Transactional
    public void registerLocker(LockerDTO dto) {

        Locker locker = Locker.builder()
                .lockerName(dto.getLockerName())
                .isAvailable(dto.getIsAvailable())
                .owner(Member.builder().memberId(dto.getOwnerId()).build())
                .build();

        lockerMapper.insertLocker(locker); // ✅ lockerId 자동 생성됨

        if (dto.getImageIds() != null && !dto.getImageIds().isEmpty()) {
            lockerImageMapper.insertLockerImages(locker.getLockerId(), dto.getImageIds());
        }
    }

}
