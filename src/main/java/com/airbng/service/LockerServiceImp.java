package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.dto.PopularLockerDTO;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKER;

@Service
@RequiredArgsConstructor
public class LockerServiceImp implements LockerService{

    private final LockerMapper lockerMapper;

    public PopularLockerDTO selectTop5Locker(){
        List<PopularLockerDTO.Result> popularLockers = lockerMapper.selectTop5Lockers();

        if(popularLockers.isEmpty()) throw new LockerException(NOT_FOUND_LOCKER);

        return PopularLockerDTO.builder()
                .lockerList(popularLockers)
                .build();
    }

}
