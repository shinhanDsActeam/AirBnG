package com.airbng.admin.domain;

import com.airbng.admin.domain.base.ReviewStatus;
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
public class LockerReview extends BaseTime {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long lockerReviewId;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ReviewStatus reviewStatus;

        @Column
        private String reviewComment;

        @OneToOne(fetch = LAZY)
        @JoinColumn(name = "pending_locker_id", nullable = false)
        private PendingLocker pendingLocker;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, columnDefinition = "VARCHAR(10)")
        private BaseStatus status;

}

