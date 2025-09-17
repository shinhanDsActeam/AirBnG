package com.airbng.api.consumer.dto.command;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerReviewDetailCommand {

    private Long memberId;
    private List<Long> jimTypeIds;
    private List<Long> imageIds;

}
