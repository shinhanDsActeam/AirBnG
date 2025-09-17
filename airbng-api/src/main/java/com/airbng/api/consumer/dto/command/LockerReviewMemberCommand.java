package com.airbng.api.consumer.dto.command;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerReviewMemberCommand {
    private Long memberId;
}
