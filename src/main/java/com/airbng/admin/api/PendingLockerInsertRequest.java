package com.airbng.admin.api;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.domain.base.BaseStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PendingLockerInsertRequest {
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Double latitude;
    private Double longitude;

    private Long memberId;         // keeper ID
    private String keeperName;     // keeper 이름
    private String keeperphone;    // keeper 이메일

    private List<Long> jimTypeId;   // PendingLockerJimtype 생성용
    private List<MultipartFile> image;  // PendingLockerImage 생성용

    private ReviewStatus reviewStatus; // 초기: WAITING
    private BaseStatus status;         //

}
