package com.airbng.dto.locker;

import com.airbng.domain.base.Available;
import com.airbng.dto.jimType.JimTypeResult;
import com.airbng.dto.jimType.LockerJimTypeResult;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerDetailResponse {

    private Long lockerId;
    private String lockerName;
    private String address;
    private String addressEnglish;
    private String addressDetail;
    private Available isAvailable;
    private Long keeperId;
    private String keeperName;
    private String keeperPhone;
    private List<LockerJimTypeResult> jimTypeResults; // 짐 타입 목록; // 종류
    private List<String> images; // 이미지 리스트

}
