package com.airbng.api.consumer.dto.view;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockerReviewDetailView {



    private String memberName;
    private String memberPhone;
    private String jimTypeName;
    private List<LockerJimTypeResult> jimTypes;
    private List<String> imageUrls;

}
