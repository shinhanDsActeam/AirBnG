package com.airbng.service;

import com.airbng.dto.PopularLockerDTO;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LockerService {

    private final LockerMapper lockerMapper;

    public PopularLockerDTO selectTop5Locker(){
        List<PopularLockerDTO.Result> popularLockers = lockerMapper.selectTop5Lockers();

        return PopularLockerDTO.builder()
                .lockerList(popularLockers)
                .build();
    }

}
