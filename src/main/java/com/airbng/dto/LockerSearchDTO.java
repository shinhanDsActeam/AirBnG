package com.airbng.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LockerSearchDTO {
    private int count;
    private List<LockerSearchDTO.Result> lockerList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private String address;
        private String lockerName;
        private String isAvailable;
        private String url;
    }
}
