package com.airbng.admin.domain.review;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @Column(nullable = false)
    private String reviewComment;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column(nullable = false)
    private LocalDateTime reviewedAt;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "pending_locker_id", nullable = false)
    private PendingLocker pendingLocker;

    @Builder.Default
    @OneToOne(mappedBy = "lockerReview", cascade = CascadeType.ALL)
    private Set<PendingLocker> pendingLockers = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

}
