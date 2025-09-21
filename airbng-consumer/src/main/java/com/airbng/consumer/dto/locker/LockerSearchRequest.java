package com.airbng.consumer.dto.locker;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerSearchRequest {
    private String address;
    private String lockerName;
    private List<Long> jimTypeIds;  // 서버에서 조회 후 채움
}