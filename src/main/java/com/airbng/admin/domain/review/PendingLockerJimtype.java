package com.airbng.admin.domain.review;

import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingLockerJimtype extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pendingLockerJimtypeId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pending_locker_id", nullable = false)
    private PendingLocker pendingLocker;

    //관계매핑 오류..?
    @Column(nullable = false)
    private Long jimtypeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;
}
