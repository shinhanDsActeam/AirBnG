package com.airbng.service;

import com.airbng.dto.locker.LockerDetailResponse;
import com.airbng.dto.locker.LockerTop5Response;
import com.airbng.dto.locker.LockerInsertRequest;
import com.airbng.dto.locker.LockerSearchRequest;
import com.airbng.dto.locker.LockerSearchResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface LockerService {

    LockerSearchResponse findAllLockerBySearch(LockerSearchRequest condition);

    void registerLocker(LockerInsertRequest dto) throws IOException;

    /**
     * 예약 건수 기준 가장 상위 5개 조회
     * */
    public LockerTop5Response findTop5Locker();
     /**
     *보관소 상세 조회
     * */
    LockerDetailResponse findUserById(Long lockerId);

}

