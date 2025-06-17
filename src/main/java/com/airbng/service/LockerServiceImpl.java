package com.airbng.service;

import com.airbng.domain.Locker;
import com.airbng.domain.Member;
import com.airbng.domain.base.Available;
import com.airbng.domain.image.Image;
import com.airbng.dto.ImageInsertRequest;
import com.airbng.dto.LockerInsertRequest;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LockerServiceImpl implements LockerService {

    private final LockerMapper lockerMapper;

    @Transactional
    @Override
    public void registerLocker(LockerInsertRequest dto) {

        Locker locker = Locker.builder()
                .lockerName(dto.getLockerName())
                .isAvailable(dto.getIsAvailable())
                .address(dto.getAddress())
                .addressEnglish(dto.getAddressEnglish())
                .addressDetail(dto.getAddressDetail())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .owner(Member.withId(dto.getOwnerId()))
                .build();

        lockerMapper.insertLocker(locker); // 등록 + lockerId 반환

        List<Long> imageIds = new ArrayList<>();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (ImageInsertRequest imgDTO : dto.getImages()) {
                Image image = Image.builder()
                        .url(imgDTO.getUrl())
                        .uploadName(imgDTO.getUploadName())
                        .build();
                lockerMapper.insertImage(image); // imageId 자동 세팅
                imageIds.add(image.getImageId());
            }

            // 3. LockerImage 테이블에 insert
            lockerMapper.insertLockerImages(locker.getLockerId(), imageIds);
        }

        // 4. JimType 연관 insert
        if (dto.getJimTypeIds() != null && !dto.getJimTypeIds().isEmpty()) {
            lockerMapper.insertLockerJimTypes(locker.getLockerId(), dto.getJimTypeIds());
        }
    }
}
