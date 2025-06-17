package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerTop5Response;
import com.airbng.dto.UserFindByIdResponse;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKER;

@Service
@RequiredArgsConstructor
public class LockerServiceImpl implements LockerService{

    private final LockerMapper lockerMapper; //final → 생성자 주입

    @Override
    public UserFindByIdResponse findUserById(Long lockerId) {
        UserFindByIdResponse result = lockerMapper.findUserById(lockerId);
        System.out.println(result);
        result.setImages(lockerMapper.findImageById(lockerId));

        return UserFindByIdResponse.builder().
                lockerId(result.getLockerId())
                .lockerName(result.getLockerName())
                .address(result.getAddress())
                .addressEnglish(result.getAddressEnglish())
                .addressDetail(result.getAddressDetail())
                .keeperId(result.getKeeperId())
                .keeperName(result.getKeeperName())
                .images(result.getImages())
                .build();
    }

    @Override
    public LockerTop5Response findTop5Locker(){
        List<LockerPreviewResult> popularLockers = lockerMapper.findTop5Lockers(ReservationState.CONFIRMED);

        if(popularLockers.isEmpty()) throw new LockerException(NOT_FOUND_LOCKER);

        return LockerTop5Response.builder()
                .lockers(popularLockers)
                .build();
    }
}
