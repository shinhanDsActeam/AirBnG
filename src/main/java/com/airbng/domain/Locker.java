package com.airbng.domain;

import java.util.List;

import com.airbng.domain.base.Available;
import com.airbng.domain.base.BaseTime;
import com.airbng.domain.image.LockerImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locker extends BaseTime {
    @NonNull
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
    private String addressDetail; // 상세 주소
    @NonNull
    private Double latitude; // 위도
    @NonNull
    private Double longitude; // 경도
    @NonNull
    private Member owner;
    private List<LockerImage> lockerImages;
}
