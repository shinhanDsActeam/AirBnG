package com.airbng.consumer.service;

import com.airbng.consumer.dto.locker.*;
import com.airbng.consumer.auth.CustomUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface LockerService {

    LockerSearchResponse findAllLockerBySearch(LockerSearchRequest request);


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

    void updateLocker(Long keeperId, LockerUpdateRequest request, List<MultipartFile> images) throws IOException;

    LockerUpdateResponse findUpdateUserById(Long lockerId);

    LockerUpdateResponse findUpdateMyLocker(Long memberId);

    void deleteLocker(Long lockerId, CustomUserDetails userDetails);



}

