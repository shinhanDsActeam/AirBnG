package com.airbng.domain;

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
public class LockerImage extends BaseTime {
    private Long lockerImageId;
    private Locker locker;
    private Image image;
}