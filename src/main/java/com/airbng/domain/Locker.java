package com.airbng.domain;

import java.util.List;

import com.airbng.domain.base.Available;
import com.airbng.domain.base.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locker extends BaseTime {
    private Long lockerId;
    private Available isAvailable;
    private Location location;
    private Member owner;
    private List<LockerImage> lockerImages;
}
