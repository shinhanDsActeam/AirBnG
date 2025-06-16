package com.airbng.service;

import com.airbng.dto.PopularLockerDTO;
import org.springframework.stereotype.Service;

@Service
public interface LockerService {

    /**
     * 예약 건수 기준 가장 상위 5개 조회
     * */
    public PopularLockerDTO selectTop5Locker();

}
