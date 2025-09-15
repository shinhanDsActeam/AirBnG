package com.airbng.admin.domain;

import com.airbng.admin.domain.base.LockerType;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.common.base.BaseStatus;
import com.airbng.common.base.BaseTime;
import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.airbng.platform.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingLocker extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pendingLockerId;

    @Column(nullable = false)
    private String lockerName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressEnglish;

    @Column(nullable = false)
    private String addressDetail;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // TODO : 양방향 1:1 일 경우 fetch = LAZY 설정 적용 안되는듯?
    @OneToOne(mappedBy = "pendingLocker", cascade = CascadeType.ALL)
    private LockerReview lockerReview;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @OneToMany(mappedBy = "pendingLocker")
    private Set<PendingLockerImage> pendingLockerImages;

    @OneToMany(mappedBy = "pendingLocker")
    @Builder.Default
    private Set<PendingLockerJimtype> pendingLockerJimtypes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockerType lockerType;

    @Column(nullable = false)
    private Long memberId;


//    public boolean validatePendingLockerJimtype(JimType jimtype){
//        return pendingLockerJimtypes.stream()
//                .anyMatch(pendingLockerJimtype ->
//                        Objects.equals(pendingLockerJimtype.getJimtypeId(), jimtype.getJimTypeId()));
//    }


//    public void updateReviewStatus(ReviewStatus reviewStatus){
//        lockerReview.reviewStatus = reviewStatus;
//    }
//
//    public void updateStatus(BaseStatus status){
//        this.status = status;
//    }
//
//    public void isAvailableUpdateState(){
//        if(status.equals(BaseStatus.DELETE))
//            throw new BaseResponseStatus.CANNOT_UPDATE_STATE;
//    }

}
