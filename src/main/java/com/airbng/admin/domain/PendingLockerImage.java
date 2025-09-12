package com.airbng.admin.domain;

import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingLockerImage extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pendingLockerImageId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pending_locker_id", nullable = false)
    private PendingLocker pendingLocker;

    @Column(nullable = false)
    private List<Long> image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;
}
