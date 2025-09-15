package com.airbng.api.admin.dto.command;

import com.airbng.common.base.BaseStatus;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 보관소 심사 요청 DTO 예시
public class LockerReviewCommand {
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Double latitude;
    private Double longitude;

    private Long memberId;         // keeper ID
//    private String keeperName;     // keeper 이름
//    private String keeperphone;    // keeper 이메일
    private String lockerType;    // 보관소 타입
    private List<Long> jimTypeId;   // PendingLockerJimtype 생성용
    private List<Long> imageId;  // PendingLockerImage 생성용

    private BaseStatus status;


}
