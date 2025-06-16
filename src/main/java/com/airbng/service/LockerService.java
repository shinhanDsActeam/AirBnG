package com.airbng.service;

import com.airbng.dto.PopularLockerDTO;

public interface LockerService {

    /**
     * 예약 건수 기준 가장 상위 5개 조회
     * */
    public PopularLockerDTO selectTop5Locker();

}
