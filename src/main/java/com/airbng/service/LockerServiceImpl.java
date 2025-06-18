package com.airbng.service;

import com.airbng.common.exception.LockerException;
import com.airbng.dto.JimTypeResult;
import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerSearchRequest;
import com.airbng.dto.LockerSearchResponse;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKERSEARCH;
@Slf4j
@RequiredArgsConstructor
@Service
public class LockerServiceImpl implements LockerService {

    private final LockerMapper lockerMapper;

    @Override
    public LockerSearchResponse findAllLockerBySearch(LockerSearchRequest request) {
        log.info("LockerServiceImpl.findAllLockerBySearch");
        List<LockerPreviewResult> lockers = lockerMapper.findAllLockerBySearch(request);
        if (lockers.isEmpty()) throw new LockerException(NOT_FOUND_LOCKERSEARCH);

        LockerSearchResponse response = LockerSearchResponse.builder()
                .count(lockerMapper.findLockerCount(request))
                .lockers(lockers)
                .build();

        return response;
    }


}