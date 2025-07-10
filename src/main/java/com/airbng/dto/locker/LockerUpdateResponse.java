package com.airbng.dto.locker;

import com.airbng.domain.base.Available;
import com.airbng.dto.jimType.LockerJimTypeUpdateResult;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerUpdateResponse {

    private Long lockerId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private Available isAvailable;
    private Long keeperId;
    private String keeperName;
    private String keeperPhone;
    private List<LockerJimTypeUpdateResult> jimTypeResults;
    private List<String> images; // 이미지 리스트

}
