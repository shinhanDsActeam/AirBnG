package com.airbng.domain;

import com.airbng.domain.base.Available;
import com.airbng.domain.base.BaseTime;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.jimtype.LockerJimType;
import lombok.*;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locker extends BaseTime {
    //    @NonNull
    private Long lockerId;
    @NonNull
    private String lockerName;
    @NonNull
    private Available isAvailable;
    @NonNull
    private String address;
    @NonNull
    private String addressEnglish;
    @NonNull
    private String addressDetail;
    @NonNull
    private Double latitude; // 위도
    @NonNull
    private Double longitude; // 경도
    @NonNull
    private Member keeper;
    private List<LockerImage> lockerImages;
    private List<LockerJimType> lockerJimTypes;
}
