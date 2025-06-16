package com.airbng.domain;

import java.util.List;

import com.airbng.domain.base.Available;
import com.airbng.domain.base.BaseTime;
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
    private Available isAvailable;
    @NonNull
    private Location location;
    @NonNull
    private Member owner;
    private List<LockerImage> lockerImages;
}
