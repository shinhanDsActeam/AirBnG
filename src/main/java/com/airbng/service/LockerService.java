package com.airbng.service;

import com.airbng.dto.UserFindByIdResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public interface LockerService {
    UserFindByIdResponse findUserById(Long lockerId);
//    List<String> detailsImage(Long lockerId);
}

