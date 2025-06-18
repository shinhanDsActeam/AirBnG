package com.airbng.domain.image;

import com.airbng.domain.Locker;
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
public class LockerImage extends BaseTime {
    @NonNull
    private Long lockerImageId;
    @NonNull
    private Locker locker;
    @NonNull
    private Image image;
}
