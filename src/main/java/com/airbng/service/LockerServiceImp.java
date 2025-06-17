package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerTop5Response;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKER;

@Service
@RequiredArgsConstructor
public class LockerServiceImp implements LockerService{

    private final LockerMapper lockerMapper;

    @Override
    public LockerTop5Response findTop5Locker(){
        List<LockerPreviewResult> popularLockers = lockerMapper.findTop5Lockers(ReservationState.CONFIRMED);

        if(popularLockers.isEmpty()) throw new LockerException(NOT_FOUND_LOCKER);

        return LockerTop5Response.builder()
                .lockers(popularLockers)
                .build();
    }

}
