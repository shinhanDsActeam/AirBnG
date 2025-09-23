package com.airbng.api.consumer.dto.command;

import com.airbng.common.base.Available;
import com.airbng.common.base.BaseStatus;
import lombok.*;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerReviewApproveCommand {

    private Long memberId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Double latitude;
    private Available isAvailable; // 'YES' or 'NO'
    private Double longitude;
    private String lockerType;
    private List<Long> jimTypeId;
    private List<Long> imageId;
    private BaseStatus status;
    private Long zzimCount;

}
