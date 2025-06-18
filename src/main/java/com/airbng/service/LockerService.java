package com.airbng.service;

import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerSearchRequest;
import com.airbng.dto.LockerSearchResponse;
import com.airbng.dto.LockerDetailResponse;
import com.airbng.dto.LockerTop5Response;
import com.airbng.dto.LockerInsertRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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