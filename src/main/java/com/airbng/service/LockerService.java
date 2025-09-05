package com.airbng.service;

import com.airbng.dto.locker.*;
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
    LockerDetailResponse findLockerById(Long lockerId);

    LockerDetailResponse findMyLocker(Long memberId);

    void updateLockerActivation(Long lockerId);

    boolean isExistLocker(Long memberId);

    void updateLocker(LockerUpdateRequest request) throws IOException;

    LockerUpdateResponse findUpdateUserById(Long lockerId);

    LockerUpdateResponse findUpdateMyLocker(Long memberId);

    void deleteLocker(Long lockerId);



}

