package com.airbng.admin.domain.review;

import com.airbng.admin.common.exception.LockerException;
import com.airbng.admin.domain.PendingLockerImage;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.BaseTime;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.jimtype.JimType;
import com.airbng.domain.jimtype.LockerJimType;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.airbng.admin.common.response.status.BaseResponseStatus.CANNOT_UPDATE_STATE;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingLocker extends BaseTime {  //memberName 어떻게 가져오지??

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

    //보관소 상태가 필요해서 적음
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus reviewStatus;

    @OneToOne(mappedBy="pendingLocker")
    private LockerReview reviewComment;

//    //TODO: LockerType 추가
//    @OneToMany(mappedBy = "pendingLocker")
//    private Set<LockerImage> lockerImages;
//
//    //TODO: LockerJimType 추가
//    @OneToMany(mappedBy = "pendingLocker")
//    @Builder.Default
//    private Set<LockerJimType> lockerJimTypes = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;

    @OneToMany(mappedBy = "pendingLocker")
    private Set<PendingLockerImage> pendingLockerImages;

    @OneToMany(mappedBy = "pendingLocker")
    @Builder.Default
    private Set<PendingLockerJimtype> pendingLockerJimtypes = new HashSet<>();

    //매핑요류..?
    @Column(nullable = false)
    private Long memberId;


    public boolean validatePendingLockerJimtype(JimType jimtype){
        return pendingLockerJimtypes.stream()
                .anyMatch(pendingLockerJimtype ->
                        Objects.equals(pendingLockerJimtype.getJimtypeId(), jimtype.getJimTypeId()));
    }


    public void updateReviewStatus(ReviewStatus reviewStatus){
        this.reviewStatus = reviewStatus;
    }

    public void updateStatus(BaseStatus status){
        this.status = status;
    }

    public void isAvailableUpdateState(){
        if(status.equals(BaseStatus.DELETE))
            throw new LockerException(CANNOT_UPDATE_STATE);
    }


}
