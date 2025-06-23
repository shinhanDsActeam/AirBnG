package com.airbng.domain.jimtype;


import com.airbng.domain.Locker;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.BaseTime;
import lombok.*;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerJimType extends BaseTime {
    @NonNull
    private Long lockerJimTypeId;
    @NonNull
    private JimType jimType;
    @NonNull
    private Locker locker;
}
