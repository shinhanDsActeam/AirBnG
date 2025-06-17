package com.airbng.service;

import com.airbng.dto.LockerTop5Response;
import com.airbng.dto.LockerInsertRequest;
import org.springframework.stereotype.Service;

@Service
public interface LockerService {

    void registerLocker(LockerInsertRequest dto);
    /**
     * 예약 건수 기준 가장 상위 5개 조회
     * */
    public LockerTop5Response findTop5Locker();

}
