package com.airbng.mappers;

import com.airbng.dto.UserFindByIdResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface LockerMapper {
    UserFindByIdResponse findUserById(Long lockerId);
    List<String> findImageById(Long lockerId);
}
