package com.airbng.dto;

import com.airbng.domain.base.Available;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class LockerDTO {

    @NonNull
    private String lockerName;

    @NonNull
    private Available isAvailable;

    // 🟨 기존: locationId → 제거
    @NonNull
    private String address;

    @NonNull
    private String addressEnglish;

    @NonNull
    private Long ownerId;

    private List<Long> imageIds;
}
