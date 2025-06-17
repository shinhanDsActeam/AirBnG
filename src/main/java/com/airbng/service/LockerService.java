package com.airbng.service;

import com.airbng.dto.LockerTop5Response;
import org.springframework.stereotype.Service;

@Service
public interface LockerService {

    /**
     * 예약 건수 기준 가장 상위 5개 조회
     * */
    public LockerTop5Response selectTop5Locker();

}
