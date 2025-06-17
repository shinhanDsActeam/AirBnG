package com.airbng.service;

import com.airbng.dto.LockerDetailResponse;
import org.springframework.stereotype.Service;

import com.airbng.dto.LockerTop5Response;

import java.util.List;
import java.util.Map;
@Service
public interface LockerService {
    /**
     * 예약 건수 기준 가장 상위 5개 조회
     * */
    public LockerTop5Response findTop5Locker();
    LockerDetailResponse findUserById(Long lockerId);
//    List<String> detailsImage(Long lockerId);
}

