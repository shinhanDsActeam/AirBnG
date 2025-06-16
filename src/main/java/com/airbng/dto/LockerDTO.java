package com.airbng.dto;

import com.airbng.domain.LockerImage;
import com.airbng.domain.base.Available;
import lombok.Data;

import java.util.List;

@Data
public class LockerDTO {
    private String isAvailable;       // enum Available -> String으로 받기 (예: "YES", "NO")
    private Long locationId;          // Location 객체 대신 ID
    private Long ownerId;             // Member 객체 대신 ID
    private List<Long> imageIds;      // 이미지 ID 리스트
}
