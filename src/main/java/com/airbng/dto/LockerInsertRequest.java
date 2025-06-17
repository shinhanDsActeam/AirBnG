package com.airbng.dto;

import com.airbng.domain.base.Available;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
public class LockerInsertRequest {
    @NonNull
    private String lockerName;

    @NonNull
    private Available isAvailable; // 'YES' or 'NO'

    @NonNull
    private Long ownerId;

    @NonNull
    private String address;

    private String addressEnglish;

    private String addressDetail;

    @NonNull
    private Double latitude;

    @NonNull
    private Double longitude;

    private List<ImageInsertRequest> images;

    private List<Long> jimTypeIds;
}
