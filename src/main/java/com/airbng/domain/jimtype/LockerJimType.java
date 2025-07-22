package com.airbng.domain.jimtype;


import com.airbng.domain.Locker;
import com.airbng.domain.Reservation;
import com.airbng.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LockerJimType extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lockerJimTypeId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "jimtype_id", nullable = false)
    private JimType jimType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "locker_id", nullable = false)
    private Locker locker;
}
