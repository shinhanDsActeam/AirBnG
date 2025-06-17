package com.airbng.service;

import com.airbng.dto.LockerSearchDTO;
import com.airbng.mappers.LockerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LockerServiceImpl implements LockerService {

    @Autowired
    private LockerMapper lockerMapper;

    @Override
    public LockerSearchDTO lockerSearch(LockerSearchDTO.Result ls) {
        List<LockerSearchDTO.Result> list = lockerMapper.lockerSearch(ls);
        int count = lockerMapper.lockerSearchCount(ls);

        LockerSearchDTO dto = new LockerSearchDTO();
        dto.setLockerList(list);
        dto.setCount(count);

        return dto;
    }
}
