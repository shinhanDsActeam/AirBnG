package com.airbng.dto;

import com.airbng.domain.base.Available;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class LockerInsertRequest {
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

    private List<Long> jimTypeIds;

    @JsonIgnore
    private List<MultipartFile> images; // List<ImageInsertRequest> → 변경
}
