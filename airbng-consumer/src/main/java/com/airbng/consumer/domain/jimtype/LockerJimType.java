package com.airbng.consumer.domain.jimtype;


import com.airbng.consumer.domain.Locker;
import com.airbng.common.base.BaseStatus;
import com.airbng.common.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;
}
