package com.airbng.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopularLockerDTO {
    private List<Result> lockerList;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private String locationId;
        private String address;
        private String lockerId;
        private String lockerName;
        private String isAvailable;
        private String url;
    }
}
