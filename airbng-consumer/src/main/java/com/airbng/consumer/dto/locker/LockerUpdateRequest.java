package com.airbng.consumer.dto.locker;

import com.airbng.common.base.Available;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerUpdateRequest {
    @NonNull
    private Long lockerId;

    @NonNull
    private String lockerName;

    @NonNull
    private Available isAvailable; // 'YES' or 'NO'

    @NonNull
    private Long keeperId;

    @NonNull
    private String address;

    private String addressEnglish;

    private String addressDetail;

    @NonNull
    private Double latitude;

    @NonNull
    private Double longitude;

    @JsonIgnore
    private List<MultipartFile> images;

    private List<Long> jimTypeIds;
}
