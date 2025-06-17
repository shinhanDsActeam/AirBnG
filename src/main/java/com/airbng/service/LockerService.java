package com.airbng.service;

import com.airbng.dto.LockerDTO;
import org.springframework.stereotype.Service;

@Service
public interface LockerService {

    void registerLocker(LockerDTO dto);

}
