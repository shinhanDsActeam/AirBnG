package com.airbng.api.consumer.dto.command;

import com.airbng.common.base.BaseStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerReviewRejectCommand {

    private Long memberId;
    private String lockerName;  // 반려된 보관소 이름
    private String reason;      // 반려 사유
}
