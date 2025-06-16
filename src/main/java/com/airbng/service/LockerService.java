package com.airbng.service;

import com.airbng.domain.Locker;
import com.airbng.domain.Location;
import com.airbng.domain.Member;
import com.airbng.domain.base.Available;
import com.airbng.dto.LockerDTO;
import com.airbng.repository.LockerImageRepository;
import com.airbng.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LockerService {

    private final LockerRepository lockerRepository;
    private final LockerImageRepository lockerImageRepository;

    @Transactional
    public void registerLocker(LockerDTO dto) {
        Locker locker = Locker.builder()
                .isAvailable(Available.valueOf(dto.getIsAvailable()))
                .location(Location.builder().locationId(dto.getLocationId()).build())
                .owner(Member.builder().memberId(dto.getOwnerId()).build())
                .build();

        lockerRepository.insertLocker(locker); // lockerId가 채워짐

        if (dto.getImageIds() != null && !dto.getImageIds().isEmpty()) {
            lockerImageRepository.insertLockerImages(locker.getLockerId(), dto.getImageIds());
        }
    }
}
