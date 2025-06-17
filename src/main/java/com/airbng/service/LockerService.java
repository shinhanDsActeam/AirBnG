package com.airbng.service;

import com.airbng.dto.LockerSearchDTO;

import java.util.Map;

public interface LockerService {
    LockerSearchDTO lockerSearch(LockerSearchDTO.Result ls);
}
