package com.airbng.dto;

import lombok.Data;

import java.util.List;

@Data
public class LockerSearchDTO {
    private List<LockerSearchDTO.Result> lockerList;

    public static class Result {
        private String locationId;
        private String address;
        private String lockerId;
        private String lockerName;
        private String isAvailable;
        private String url;
    }
}
