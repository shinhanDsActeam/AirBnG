package com.airbng.service;

import com.airbng.dto.LockerPreviewResult;
import com.airbng.dto.LockerSearchRequest;
import com.airbng.dto.LockerSearchResponse;


public interface LockerService {
    LockerSearchResponse findAllLockerBySearch(LockerSearchRequest condition);
}
