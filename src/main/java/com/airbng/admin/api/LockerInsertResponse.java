package com.airbng.admin.api;

import com.airbng.domain.base.Available;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerInsertResponse { //승인된 관소 등록 요청 -> user

    @NonNull
    private String lockerName;

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
