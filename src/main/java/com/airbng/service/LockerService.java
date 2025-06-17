package com.airbng.service;

import com.airbng.dto.LockerInsertRequest;
import org.springframework.stereotype.Service;

@Service
public interface LockerService {

    void registerLocker(LockerInsertRequest dto);

}
